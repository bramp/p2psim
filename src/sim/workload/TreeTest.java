package sim.workload;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.overlay.tree.events.JoinEvent;
import sim.net.overlay.tree.tbcp.Node;
import sim.net.router.Router;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * @author Andrew Brampton
 */
public class TreeTest {

	public TreeTest() throws Exception {

	}

	public void init() throws Exception {

		int i;
		final int PEERS = 100;

		HostSet hosts = new HostSet();
		Router r = new Router(0);
		hosts.add(r);

		for (i = 1; i <= PEERS; i++) {
			Node n = new Node(i);
			NormalLink l = new NormalLink(r, n);
			l.setDelay(Global.rand.nextInt(100)); //Make the delay a little random
			hosts.add(n);
		}

		Router.createRoutingTables();
		Iterator<Host> it = hosts.getType(Node.class).iterator();
		Node n1 = (Node)it.next();

		while (it.hasNext()) {
			Node n = (Node) it.next();
			Events.addAfterLastEvent(new JoinEvent(n, n1.getAddress()));
		}

		// Loops alot
		i = 0;
		while (Events.runNextEvent()) {
			//Trace.println("Event " + i++);
			i++;
		}

		Trace.println(LogLevel.INFO, "Simulation ended at " + Events.getTime());

		// Print out tree
		it = hosts.getType(Node.class).iterator();
		it.next(); // Skip the first

		while (it.hasNext()) {
			Node n = (Node)it.next();
			Trace.println(LogLevel.INFO, n.getAddress() + "-" + n.getParent());
		}
	}
}
