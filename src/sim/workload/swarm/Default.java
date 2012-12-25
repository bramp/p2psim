/**
 *
 */
package sim.workload.swarm;

import sim.main.Global;
import sim.net.HostSet;
import sim.net.InvalidHostException;
import sim.net.links.CrapLink;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.swarm.SwarmPeer;
import sim.net.router.EdgeRouter;

/**
 * @author Andrew Brampton
 *
 */
public class Default extends sim.workload.Default {

	public Default(String[] arglist) throws Exception {
		super(arglist);
	}

	public static void setupPeers(int highcap, int lowcap, double high, double low) throws InvalidHostException {

		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

		SwarmPeer p;
		EdgeRouter e;
		//int d;

		// Create all the Peers
		for (int i=0;i<highcap;i++) {
			p = new SwarmPeer(Global.lastAddress);
			p.setCapability(high);

			e = (EdgeRouter)edgeRouters.getRandom();

			// connect peer to a random edge router
			//new Link(p, e, Link.BANDWIDTH_1024k,d);
			new CrapLink(p, e, NormalLink.BANDWIDTH_1024k, 90, 110);
		}

		for (int i=0;i<lowcap;i++) {
			p = new SwarmPeer(Global.lastAddress);
			p.setCapability(low);

			e = (EdgeRouter)edgeRouters.getRandom();

			// connect peer to a random edge router
			new CrapLink(p, e, NormalLink.BANDWIDTH_56k, 100, 240);
			//new Link(p, e, Link.BANDWIDTH_56k, d);
		}

		fastJoinAllNodes();
	}
}
