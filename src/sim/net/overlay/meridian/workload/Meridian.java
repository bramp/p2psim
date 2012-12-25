/**
 *
 */
package sim.net.overlay.meridian.workload;

import java.util.ArrayList;
import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.InvalidHostException;
import sim.net.links.NormalLink;
import sim.net.overlay.meridian.Client;
import sim.net.overlay.meridian.LatencyCalculationEvent;
import sim.net.overlay.meridian.Node;
import sim.net.overlay.meridian.RequestEvent;
import sim.net.overlay.meridian.Server;
import sim.net.router.EdgeRouter;
import sim.net.router.Router;
import sim.workload.Workload;

/**
 * @author Andrew Brampton
 *
 */

public class Meridian implements Workload {
	protected static void joinAllRouters() throws InvalidHostException {
		Router.createRoutingTables();
	}

	public static void setupNodes(int servers, int clients, int limit) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

        //Object[] arrayRouters = edgeRouters.toArray();

		// First join the servers

		for (int i=0; i<servers; i++) {

			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();  //  randomly distributed clients to servers

//		  	Distribution zd = new Zipf(1.2, Global.hosts.getType(EdgeRouter.class).size() ); // zipf law distributed of clients per serv
//			EdgeRouter e = (EdgeRouter) arrayRouters[(zd.nextInt() -1) ];


			Server p = new Server(Global.lastAddress, limit , 0);


			/*		   	requests = received requests could be set 0, or some values meaning server capacity
		   	check requests <= limit

			Server p = new Server(Global.lastAddress, limit, requests);
			Server p = new Server(Global.lastAddress); */


			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p, e, NormalLink.BANDWIDTH_1024k, d);
		}

		// Now add some clients
		for (int i=0; i<clients; i++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			Client sp = new Client(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(sp, e, NormalLink.BANDWIDTH_1024k, d);

		}
	}

	public Meridian(String[] arglist) throws Exception {

		int server = Integer.parseInt(arglist[0]);		// e.g. 10
		int client = Integer.parseInt(arglist[1]);		// e.g. 990
		int limit =  Integer.parseInt(arglist[2]);

		ArrayList<Integer> lmtList = new ArrayList<Integer>();
        for (int i = 0; i< server; i++){

        	lmtList.add(limit);    // EQUAL distribution of limits


        }

//		System.out.println("maxLoad  "+ limit);


// 		int limit = (int) clients/servers;

 	//	int limit = (int) 5;

		// RANDOM distribution of limits

//		int limit =  (int) (Global.rand.nextDouble()*clients/4);

		// UNIFORM distribution of limits []
//		Distribution u = new Uniform(1, clients/4);
//	    limit =  (int) u.nextDouble() ;

/*			int capacity =   (int) u.nextDouble();
		while(limit < capacity){
			capacity =   (int) u.nextDouble();
		}
		System.out.println("CP="+ capacity +  "  " +clients);
*/


		setupNodes(server, client,limit);
		joinAllRouters();

		// Now give all the Nodes a selection of the servers to ping


		HostSet servers = Global.hosts.getType(Server.class);
		HostSet clients = Global.hosts.getType(Client.class);

		Iterator<Host> i;

		// All servers ping all the other servers
		i = servers.iterator();
		while ( i.hasNext() ) {
			Node n = (Node) i.next();

			Iterator<Host> ii = servers.iterator();
			while ( ii.hasNext() ) {
				n.sendPing( ii.next().getAddress() );
			}
		}

		// Clients ping some servers; (1/3) of them

		int cnt = 0;
		i = clients.iterator();

		while ( i.hasNext() ) {
			ArrayList<Host> sl = new ArrayList<Host>();
			Node n = (Node) i.next();
			while (cnt <= servers.size()/3){
				Host s = servers.getRandom();

				if (sl.contains(s)){
					s = servers.getRandom();
				} else {
 					sl.add(s);
 					n.sendPing(s.getAddress() );
 					cnt++;
				}
			}
			cnt = 0;
		}

		// Clients ping all the servers
		/*
		i = clients.iterator();
		while ( i.hasNext() ) {
			Node n = (Node) i.next();

			Iterator<Host> ii = servers.iterator();
			while ( ii.hasNext() ) {
				n.sendPing( ii.next().getAddress() );
			}
		}*/


		Server s = (Server) servers.first();

		// Now queue a RequestEvent

		i = clients.iterator();
		while ( i.hasNext() ) {
			Client c = (Client) i.next();
			Events.addAfterLastEvent(new RequestEvent(c, s.getAddress()));
		}

		Events.addAfterLastEvent ( new LatencyCalculationEvent() );

	}

	@Override
	public void simulationFinished() {
		// needs filling
	}

	@Override
	public void simulationStart() {
		// needs filling
	}
}
