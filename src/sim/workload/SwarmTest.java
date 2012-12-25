package sim.workload;
import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.swarm.CreateOPlaneEvent;
import sim.net.overlay.dht.swarm.JoinOPlaneEvent;
import sim.net.overlay.dht.swarm.SwarmPeer;
import sim.net.router.Router;
import sim.net.topology.reader.InetFileReader;
import sim.net.topology.reader.NodeLoader;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;


/*
 * Created on 27-Mar-2005
 */

/**
 * @author Andrew Brampton
 */
public class SwarmTest extends Default {

	public SwarmTest(String arglist[]) throws Exception {
		super(arglist);

		//System.setOut(new EventPrintStream(System.out));

		String network = "inet-1000";
		InetFileReader reader = new InetFileReader(network);
		reader.load(new NodeLoader() {
			public Host createHost(String type, int address) {
				if (type.equals("R"))
					return new Router(address);
				else if (type.equals("N"))
					return new SwarmPeer(address);
				else
					System.err.println("Unknown Node Type " + type);

				return null;
			}
		});

		joinAllNodes();

		HostSet peers = Global.hosts.getType(SwarmPeer.class);

		//Find first peer
		SwarmPeer p1 = (SwarmPeer) peers.getRandom();

		Events.add(new CreateOPlaneEvent(p1, 1), 0);

		// Loops alot
		while (Events.runNextEvent()) {}

		//Events.add((PEERS + 1) * 1000, new JoinOPlaneEvent((Peer)hosts.get(2), p1.nodeID, 1));
		//p =(Peer)hosts.get(2);

		int inOPlane = 0;
		int joinedOPlane = 0;
		//long joinStart = Events.getTime();

		while (inOPlane < peers.size()) {

			SwarmPeer p;

			do {
				p = (SwarmPeer)peers.getRandom();
			} while (p.joinedOPlane);

			Events.addAfterLastEvent( new JoinOPlaneEvent(p, p1.nodeID, 1));

			//Events.add(joinEnd + 3 * JOINTIME, new StreamEvent(p1,1, 1024, 10000));

			// Loops alot
			while (Events.runNextEvent()) {}

			//Trace.println("Simulation ended at " + Events.getTime());

			inOPlane = 0;
			joinedOPlane = 0;

			Iterator<Host> i = peers.iterator();
			while (i.hasNext()) {
				p = (SwarmPeer)i.next();
				//Trace.println(p + " " + p.OPlanes);
				if (!p.OPlanes.isEmpty()) {
					inOPlane++;
					if (p.joinedOPlane)
						joinedOPlane++;
				}
			}

			Trace.println(LogLevel.INFO, "Total Peers " + peers.size() + ", Numbered joined OPlane " + joinedOPlane + ", number in OPlane " + inOPlane);
		}
	}
}
