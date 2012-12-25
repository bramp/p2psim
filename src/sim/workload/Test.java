/**
 *
 */
package sim.workload;

import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.router.Router;
import sim.net.topology.reader.InetFileReader;
import sim.net.topology.reader.NodeLoader;
import sim.stats.StatsObject;

/**
 * @author macquire
 *
 */
public class Test extends Default {
	public Test(String[] arglist) throws Exception {
		super(arglist);

		HostSet hosts = null;

		String network = "topology//inet-100";
		/*
		NodeStateReader stateReader = new NodeStateReader(network + ".state");

		//Load from file if we can, otherwise re-make network
		try {
			hosts = stateReader.load();
		} catch(Exception e) {}
		*/

		if (hosts == null) {
			//Global.trace.println("Unable to load old state");
			InetFileReader reader = new InetFileReader(network);
			reader.load(new NodeLoader() {
				public Host createHost(String type, int address) {
					if (type.equals("R")) {
						Global.stats.logCount("Node"+StatsObject.SEPARATOR+"Router");
						Global.stats.logCount("Node");
						return new Router(address);
					}
					else if (type.equals("N")) {
						Global.stats.logCount("Node"+StatsObject.SEPARATOR+"Peer");
						Global.stats.logCount("Node");
						return new Peer(address);
					}
					else {
						System.err.println("Unknown Node Type " + type);
					}

					return null;
				}
			});
			//stateReader.save(hosts);
		}

		/*
		final int PEERS = 100;
		NodeList hosts = new NodeList();
		Router r = new Router(0);
		hosts.add(r);

		for (int i = 1; i <= PEERS; i++) {
			Peer p = new Peer(i, (long)(Long.MAX_VALUE * Math.random()));
			//Peer p = new Peer(i, i);
			new Link(r, p);
			hosts.add(p);
		}
		*/

		joinAllNodes();

		//((Peer)hosts.get(99)).printRoutingTable();

		sendMessageToAll();

		/*Iterator i = hosts.iterator();
		while (i.hasNext()) {
			Host h = (Host) i.next();

			if (h instanceof Peer) {
				Peer p = (Peer) h;
				Global.trace.println(p + " " + p.routingTable.size());
				//p.printRoutingTable();
			}
		}*/

		//((Peer)hosts.get(0x37)).printRoutingTable();
		//((Peer)hosts.get(0x38)).printRoutingTable();

		/*
		LeafSet l = new LeafSet(0x0000000000000010, 16);
		RoutingTable r = new RoutingTable(0x0000000000000010, 4, 64, 16);

		for (long i = 0; i < 32; i++) {
			NodeAddressPair p = new NodeAddressPair( i, 0);

			//r.add(new NodeAddressPair(0x0000000000000001, 0));
			r.add(p);
			l.add(p);
			Global.trace.println(r.toString());
		}
		*/


	}
}
