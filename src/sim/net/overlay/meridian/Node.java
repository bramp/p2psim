/**
 *
 */
package sim.net.overlay.meridian;

import java.util.Set;
import java.util.TreeSet;

import sim.net.ErrorPacket;
import sim.net.Packet;
import sim.net.PingPacket;
import sim.net.PongPacket;
import sim.net.RoutingException;
import sim.net.SimpleHost;
import sim.net.links.Link;

/**
 * A Merdian Node
 *
 * @author Andrew Brampton
 * @author Idris A. Rai
 *
 */
public class Node extends SimpleHost {


	/** The radius of each ring **/

	public int[] radii = {80, 160, 320, 640, 1280};
	//public int[] radii = {10, 20, 30, 40, 50, 60};

	Rings rings = new Rings(radii);

	/**
	 * List of nodes that have had pings sent to them
	 */
	Set<Integer>  probedHosts = new TreeSet<Integer>();

	public Set<Integer> getProbed(){
		return probedHosts;
	}
	/**
	 * @param address
	 */
	public Node(int address) {
		super(address);
	}

	/* (non-Javadoc)
	 * @see sim.net.Host#recv(sim.net.links.Link, sim.net.Packet)
	 */
	@Override
	public void recv(Link link, Packet p) {
		super.recv(link, p);

		if (p instanceof PongPacket) {
			PongPacket pong = (PongPacket) p;
			pongArrived ( pong.from, pong.getRoundTripTime() );
		} else if (p instanceof ErrorPacket) {
			throw new RuntimeException("Opps, we shouldn't have to deal with errors! (" + p + ")");
		}
	}

	public void pongArrived(final int host, final int distance) {
		// find this host in the probedHosts vector
		if (probedHosts.contains(host)) {
			rings.add(host, distance, radii);

			probedHosts.remove (host);

		} /* else {} */
	}

	public void sendPing(final int host) throws RoutingException {
		probedHosts.add(host);
		//System.out.print(probedHosts);
		send( PingPacket.newPacket(this.address, host) );
	}

	/**
	 * Returns the ring a host is in
	 * @param host
	 * @return
	 */
	public int getRing(final int host, int[] radii) {
	    int  d = getUnicastDelay(host);
		return rings.getRing(d, radii);
	}

	public void dispose() {
		rings.clear();
		rings = null;
	}
}
