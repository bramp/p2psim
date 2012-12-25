/**
 *
 */
package sim.net.router;

import java.util.Iterator;

import sim.events.Events;
import sim.net.BrokenLinkException;
import sim.net.ErrorPacket;
import sim.net.Host;
import sim.net.InvalidHostException;
import sim.net.Packet;
import sim.net.RoutingException;
import sim.net.UnreachablePacket;
import sim.net.events.SendEvent;
import sim.net.links.Link;
import sim.net.links.SharedLink;
import sim.net.links.SharedLinkBase;

/**
 * @author macquire
 *
 */
public class MobileRouter extends EdgeRouter {

	SharedLinkBase base;

	/**
	 * @param address
	 */
	public MobileRouter(int address, int sharedBandwidth, int sharedDelay) {
		super(address);
		base = new SharedLinkBase(sharedBandwidth, sharedDelay);
	}

	/* (non-Javadoc)
	 * @see sim.net.Host#addLink(sim.net.links.Link)
	 */
	@Override
	public void addLink(Link c) throws InvalidHostException {
		if (c instanceof SharedLink) {
			((SharedLink)c).shareWith(base);
		}

		super.addLink(c);
	}

	public int getSharedBandwidth() {
		return base.bandwidth;
	}

	public int getSharedDelay() {
		return base.delay;
	}

	public void setSharedBandwidth(int bandwidth) {
		if (bandwidth <= 0)
			throw new RuntimeException("Bandwidth can't be <= zero");

		base.bandwidth = bandwidth;
		notifyBaseUpdate();
	}

	public void setSharedDelay(int delay) {
		if (delay <= 0)
			throw new RuntimeException("Delay can't be <= zero");

		base.delay = delay;
		notifyBaseUpdate();
	}

	/**
	 * This is called when the base is updated.
	 * It informs all links and makes sure their bandwidht/delay etc
	 * is all correct
	 */
	protected void notifyBaseUpdate() {
		// Update all the shared links
		Iterator<Link> i = links.iterator();
		while (i.hasNext()) {
			Link l = i.next();
			if (l instanceof SharedLink) {
				((SharedLink)l).shareWith(base);
			}
		}
	}

	public SharedLinkBase getSharedBase() {
		return base;
	}

	/* (non-Javadoc)
	 * @see sim.net.router.Router#send(sim.net.Packet)
	 */
	@Override
	public void send(Packet p) throws RoutingException {

		try {

			int lnumber = routingTable[p.to];

			if (lnumber == INVALID_LINK) {
				throw new BrokenLinkException("Can't route to " + Host.toString( p.to ));
			}

			Link link = links.get(lnumber);

			link.send(this, p);
		} catch (BrokenLinkException e) {
			// Send a "unreachable" packet back faking the destinations IP
			// Only if this wasn't a ErrorPacket to begin with
			if (!(p instanceof ErrorPacket)) {
				Packet errorpacket = UnreachablePacket.newPacket(p.to, p.from, p);

				// Also delay this UnreachablePacket by the shared link delay
				Events.addFromNow(SendEvent.newEvent(this, errorpacket), getSharedDelay());
			}

		} catch (InvalidHostException e) {
			e.printStackTrace();
		}
	}
}
