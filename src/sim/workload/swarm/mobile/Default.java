package sim.workload.swarm.mobile;

import sim.main.Global;
import sim.net.HostSet;
import sim.net.links.SharedLink;
import sim.net.overlay.dht.swarm.SwarmPeer;
import sim.net.router.MobileRouter;

/*
 * Created on Feb 8, 2005
 */

/**
 * @author Andrew Brampton
 */
public abstract class Default extends sim.workload.Default {

	/**
	 * @param arglist
	 * @throws Exception
	 */
	public Default(String[] arglist) throws Exception {
		super(arglist);
	}

	public static HostSet setupPeers(int count, double cap) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(MobileRouter.class);
		HostSet peers = new HostSet();

		if (edgeRouters.isEmpty())
			throw new RuntimeException("There are no mobile routers!");

		for (int i=0;i<count;i++) {
			SwarmPeer p = new SwarmPeer(Global.lastAddress);
			p.setCapability(cap);

			MobileRouter e = (MobileRouter)edgeRouters.getRandom();

			new SharedLink(e, p);

			peers.add(p);
		}

		return peers;
	}

}
