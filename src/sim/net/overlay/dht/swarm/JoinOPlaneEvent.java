package sim.net.overlay.dht.swarm;
import sim.events.Event;
/*
 * Created on 15-Apr-2005
 */

/**
 * @author Andrew Brampton
 */
public class JoinOPlaneEvent extends Event {

	SwarmPeer p;
	int oPlane = OPlane.INVALIDOPLANE;
	long toJoin;

	/**
	 * @param p The peer joining the OPlane
	 * @param toJoin The address of the peer to join
	 * @param oPlane The number of the OPlane
	 */
	public JoinOPlaneEvent(SwarmPeer p, long toJoin, int oPlane) {
		this.p = p;
		this.toJoin = toJoin;
		this.oPlane = oPlane;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.events.Event#run()
	 */
	@Override
	public void run() {
		p.joinOPlane(toJoin, oPlane);
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}

}
