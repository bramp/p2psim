/**
 *
 */
package sim.net.links;

import java.io.Serializable;

import sim.events.Events;
import sim.main.Global;
import sim.main.Helper;
import sim.net.BrokenLinkException;
import sim.net.Disposable;
import sim.net.Host;
import sim.net.InvalidHostException;
import sim.net.Packet;
import sim.net.RoutingException;
import sim.net.events.ReceiveEvent;
import sim.net.router.Router;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;
import static sim.stats.StatsObject.SEPARATOR;

/**
 * @author Andrew Brampton
 *
 */
public abstract class Link implements Serializable, Disposable {
	public final static int BANDWIDTH_1k = 1024 / 8;
	public final static int BANDWIDTH_9_6k = (int)(9.6 * BANDWIDTH_1k);
	public final static int BANDWIDTH_14_4k = (int)(14.4 * BANDWIDTH_1k);
	public final static int BANDWIDTH_28k = 28 * BANDWIDTH_1k;
	public final static int BANDWIDTH_56k = 56 * BANDWIDTH_1k;
	public final static int BANDWIDTH_128k = 128 * BANDWIDTH_1k;
	public final static int BANDWIDTH_256k = 256 * BANDWIDTH_1k;
	public final static int BANDWIDTH_512k = 512 * BANDWIDTH_1k;
	public final static int BANDWIDTH_1024k = 1024 * BANDWIDTH_1k;
	public final static int BANDWIDTH_2048k = 2048 * BANDWIDTH_1k;
	public final static int BANDWIDTH_4096k = 4096 * BANDWIDTH_1k;
	public final static int BANDWIDTH_8192k = 8192 * BANDWIDTH_1k;
	public final static int BANDWIDTH_10M   = 10 * BANDWIDTH_1024k;
	public final static int BANDWIDTH_100M  = 100 * BANDWIDTH_1024k;
	public final static int BANDWIDTH_1000M = 1000 * BANDWIDTH_1024k;

	//TODO change this later to be truely infinite
	public final static int BANDWIDTH_UNLIMITED = Integer.MAX_VALUE;

	public final static int BANDWIDTH_DEFAULT = BANDWIDTH_1024k;
	public final static int DELAY_DEFAULT = 10;

	public final static int BANDWIDTH_NA = Integer.MIN_VALUE;
	public final static int DELAY_NA = Integer.MIN_VALUE;

	protected int bandwidth;
	protected int cost;

	/* 2 Element array representing each end of the connection */
	protected final Host[] endPoint = new Host[2];

	protected boolean failed = false;

	/* The time at which this link is free */
	protected long nextFreeTime = 0;

	/* Constructs a link that hasn't been connected yet */
	public Link(final int bandwidth, final int delay) {
		setBandwidth(bandwidth);
		setDelay(delay);

		// Log the average bandwidth and delay (for this link)
		String name = "Link" + SEPARATOR + Helper.getShortName(this);

		Global.stats.logCount("Link");
		Global.stats.logCount(name);

		if (getBandwidth() != BANDWIDTH_NA) { // Only log if the bandwidth is not Not Applicable
			Global.stats.logAverage("Link" + SEPARATOR + "Bandwidth", getBandwidth());
			Global.stats.logAverage(name + SEPARATOR + "Bandwidth", getBandwidth());
		}

		if (getDelay() != DELAY_NA) { // Only log if the delay is not Not Applicable
			Global.stats.logAverage(name + SEPARATOR + "Latency", getCost());
			Global.stats.logAverage("Link" + SEPARATOR + "Latency", getCost());
		}

	}

	public Link(final Host host1, final Host host2) {
		this(host1, host2, BANDWIDTH_DEFAULT, DELAY_DEFAULT);
	}

	/**
	 * Constructs a link between two hosts, with certain bandwidth and
	 * delay properties.
	 * @param host1 One end of the link
	 * @param host2 Other end of the link
	 * @param bandwidth The bandwidth of the link (in bytes/second)
	 * @param delay The delay on the link (in milliseconds)
	 */
	public Link(final Host host1, final Host host2, final int bandwidth, final int delay) {
		this(bandwidth, delay);
		connect(host1, host2);
	}

	/**
	 * @return
	 */
	public int getBandwidth() {
		return bandwidth;
	}

	/* (non-Javadoc)
	 * @see sim.net.Disposable#dispose()
	 */
	public void dispose() {
		disconnect();
	}

	/**
	 * Remove this link from both end points
	 */
	public void disconnect() {
		// Remove the link from each endpoint
		if (endPoint[0] != null && endPoint[1] != null) {
			Trace.println(LogLevel.LOG1, "Link removed " + this);


			Global.stats.logCount("Link" + SEPARATOR + "Disconnect");
			Global.stats.logCount("Link" + SEPARATOR + Helper.getShortName(this) + SEPARATOR + "Disconnect");

			endPoint[0].removeLink(this);
			endPoint[0] = null;

			endPoint[1].removeLink(this);
			endPoint[1] = null;
		}
	}

	/**
	 * Returns the cost on this link (which is mean delay)
	 * @return
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * The delay on this link
	 * @return
	 */
	public int getDelay() {
		return cost;
	}

	protected long getNextFreeTime() {
		if (nextFreeTime < Events.getTime())
			nextFreeTime = Events.getTime();

		return nextFreeTime;
	}

	public Host getOtherHost(final Host me) throws InvalidHostException {
		if (endPoint[0] == me) {
			return endPoint[1];
		} else if (endPoint[1] == me) {
			return endPoint[0];
		} else {
			throw new InvalidHostException(me);
		}
	}

	public boolean hasFailed() {
		return failed;
	}

	/**
	 * Sends a packet over the link
	 * @param from endPoint this packet came from
	 * @param p Packet
	 * @throws RoutingException
	 * @throws InvalidHostException
	 */
	public void send(final Host from, final Packet p) throws RoutingException, InvalidHostException {

		if (Global.debug_log_packets)
			Trace.println(LogLevel.LOG1, Host.toString(from.getAddress()) + ": " + p.toString());

		if (p.to == from.getAddress())
			throw new RuntimeException("Sending packet to ourselves from ourselves PKT send from:" + Host.toString(from.getAddress()) + " to:" + Host.toString(p.to) + " p:" + p.toString());

		// Check this link is alive
		if (failed)
			throw new BrokenLinkException("Link is dead");

		// Figure out the delay
		int delay = getDelay() + ((p.size * 1000) / bandwidth) ;

		// add another hop to this packet's current count
		p.addHop();

		//Figure out which host this is going to
		Host to = getOtherHost(from);

		long nextFree = getNextFreeTime();

		//Add to event table
		Events.add(ReceiveEvent.newEvent(to, this, p), nextFree + delay);

		//Work out when we are free next
		setNextFreeTime(nextFree + (p.size / bandwidth) * 1000);
	}

	public void setDelay(final int delay) {
		if (delay < 0 && DELAY_NA != delay)
			throw new RuntimeException("Delay can't be < zero");

		this.cost = delay;
	}

	public void setFailed(final boolean failed) {
		// If both end points are routers, we can't do this
		if (endPoint[0] instanceof Router && endPoint[1] instanceof Router)
			throw new RuntimeException("Can't fail a link between routers");

		this.failed = failed;
	}

	protected void setNextFreeTime(final long nextFreeTime) {
		this.nextFreeTime = nextFreeTime;
	}

	@Override
	public String toString() {
		return Host.toString( endPoint[0].getAddress() ) + "<->" +
			   Host.toString( endPoint[1].getAddress() ) +
			   " Bandwidth:" + bandwidth + " Cost:" + cost;
	}

	/**
	 * @param i
	 * @return
	 */
	public Host get(final int i) {
		return endPoint[i];
	}

	/**
	 * @param host1
	 * @param host2
	 */
	public void connect(final Host host1, final Host host2) {
		assert host1 != null;
		assert host2 != null;

		endPoint[0] = host1;
		endPoint[1] = host2;

		try {
			host1.addLink(this);
			host2.addLink(this);
		} catch (InvalidHostException e) {
			e.printStackTrace();
		}

		Trace.println(LogLevel.LOG1, "Link added " + this);
		Global.stats.logCount("Link" + SEPARATOR + "Connect");
		Global.stats.logCount("Link" + SEPARATOR + Helper.getShortName(this) + SEPARATOR + "Connect");
	}

	/**
	 * @param bandwidth
	 */
	public void setBandwidth(final int bandwidth) {
		if (bandwidth <= 0 && BANDWIDTH_NA != bandwidth)
			throw new RuntimeException("Bandwidth can't be <= zero");

		this.bandwidth = bandwidth;
	}
}
