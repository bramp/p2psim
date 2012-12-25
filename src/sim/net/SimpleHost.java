/*
 * Created on 13-Feb-2005
 */
package sim.net;

import sim.main.Global;
import sim.main.Helper;
import sim.net.links.Link;
import sim.net.router.Router;
import static sim.stats.StatsObject.SEPARATOR;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * A SimpleHost that can only have one link.
 * Also replys to pings!
 * @author Andrew Brampton
 */
public class SimpleHost extends Host {

	// Avoid using the ArrayListe links, and use this single link
	protected Link link = null;

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.Host#addLink(net.bramp.p2psim.Link)
	 */
	@Override
	public void addLink(final Link c) throws InvalidHostException {

		//SimpleHosts only have one link, thus check if we are going OTT
		if (link != null)
			throw new RuntimeException("Too many links for a SimpleHost");

		link = c;
		super.addLink(c);
	}

	/* (non-Javadoc)
	 * @see sim.net.Host#removeLink(sim.net.links.Link)
	 */
	@Override
	public void removeLink(final Link c) {
		if (link != c)
			throw new RuntimeException("Removing a link not connected to this host");

		link = null;
		super.removeLink(c);
	}

	/**
	 * @param address
	 */
	public SimpleHost(final int address) {
		super(address);
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.Host#recv(net.bramp.p2psim.Link, net.bramp.p2psim.Packet)
	 */
	@Override
	public void recv(final Link link, final Packet p) {
		//if (p.getClass().equals(MulticastPacket.class)) {
		//	Trace.println(LogLevel.DEBUG, "*** received at " + this);
		//}

		// If this is being called the packet must be for us :)
		if (Global.debug_log_packets)
			Trace.println(LogLevel.LOG1, Host.toString(getAddress()) + ": " + p.toString());

		Global.stats.log("Net" + SEPARATOR + "Packet" + SEPARATOR + "Hops", p.getHopCount());
		Global.stats.log("Net" + SEPARATOR + "Packet" + SEPARATOR + "E2ELatency", p.getDelay());

		final String packettype = Helper.getShortName(p);
		Global.stats.log("Net" + SEPARATOR + packettype + SEPARATOR + "Hops", p.getHopCount());
		Global.stats.log("Net" + SEPARATOR + packettype + SEPARATOR + "E2ELatency", p.getDelay());

		//If its a ping, reply!
		if (p.to == address && p instanceof PingPacket) {
			PongPacket pong = PongPacket.newPacket((PingPacket)p);
			try {
				send(pong);
			} catch (RoutingException e) {
				Trace.println(LogLevel.WARN, this + ": WARNING " + e + " (" + p + ")");
			}

			p.data = null;
		}
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.Host#send(net.bramp.p2psim.Packet)
	 */
	@Override
	public void send(final Packet p) throws RoutingException {

		if (p.to == this.address) {
			//Trace.println(LogLevel.DEBUG, this +  ":" + address + " blah");
			recv(null, p);
			//System.out.println(p.to + " " + p.from);
			return;
		}

		if (link == null)
			throw new RoutingException("No default route");

		// Catch if the link is broken and this peer is all alone
		try {
   			   link.send(this, p);

		} catch (BrokenLinkException e) {
			// Send a "unreachable" packet back
			recv(link, UnreachablePacket.newPacket(p.to, p.from, p));
		} catch (InvalidHostException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method uses global knowledge to work out the delay between
	 * this host and the address in the parameter (if it was sent directly,
	 * ie Unicast)
	 * @param host The delay to the other host
	 * @return The delay in ms to host. or -1 if it can't be worked out error
	 */
	public int getUnicastDelay(final int host) {

		// If this is us, well duh, zero :)
		if (host == address)
			return 0;

		// If we don't have any links
		if (link == null) {
			return -1;
		}

		int delay = link.getCost();

		try {
			// We assume we are connected to a router
			Router r = (Router) link.getOtherHost(this);
			delay += r.getDelay(host);

			// If the router didn't know the correct route
			if (delay < 0)
				return -1;

		} catch (InvalidHostException e) {
			e.printStackTrace();
		}

		return delay;
	}

}
