package sim.workload.sigcomm;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.InvalidHostException;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.router.EdgeRouter;
import sim.net.router.Router;

public class Default {
	public static void setupPeers(int service, int stealth) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

		// First join normal peers
		for (int i=0; i<service; i++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			ServicePeer p = new ServicePeer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p, e, NormalLink.BANDWIDTH_1024k, d);
		}

		// Now add some Stealth peers
		for (int ii=0; ii<stealth; ii++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			StealthPeer sp = new StealthPeer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;
			// connect peer to a random edge router
			new NormalLink(sp, e, NormalLink.BANDWIDTH_1024k, d);
		}
	}

	protected static void joinAllRouters() throws InvalidHostException {
		Router.createRoutingTables();
	}

	public static void fastJoinAll() throws InvalidHostException {
		joinAllRouters();

		// Valid nodes
		HostSet peers = Global.hosts.getType(DHTInterface.class);

		Iterator<Host> i = peers.iterator();
		while (i.hasNext()) {
			Host h = i.next();
			if (h.hasFailed()) {
				peers.remove(h);
				i = peers.iterator();
			}
		}

		// join peers
		i = Global.hosts.getType(Peer.class).iterator();
		while (i.hasNext()) {
			Peer p = (Peer) i.next();

			// Remove this peer from the peers list
			boolean ret = peers.remove(p);

			// Now fast join this node
			p.fastJoin(peers);

			if (ret)
				peers.add(p);
		}
	}

	/**
	 * Places peerData into the network
	 * @param count
	 * @return A list of DHT nodes that now have keys
	 */
	public static Set<DHTInterface> addPeerData(HostSet hosts, int count, int replication) {

		Set<DHTInterface> hostsWithKeys = new TreeSet<DHTInterface>();

		int i = 0;
		int hostcount = hosts.size();

		if (hostcount == 0) {
			throw new RuntimeException("There are no hosts to add PeerData to!");
		}

		// Insert all hosts into an array
		NodeAddressPair[] pairs = new NodeAddressPair[hostcount];
		Iterator<Host> it = hosts.iterator();
		while (it.hasNext()) {
			Host h = it.next();
			pairs[i] = new NodeAddressPair(((DHTInterface)h).getID(), h.getAddress() );
			i++;
		}

		// Sort the array
		Arrays.sort(pairs);

		// Create a quick lookup array
		DHTInterface[] nodes = new DHTInterface[hostcount];
		for (i = 0; i < hostcount; i++ ) {
			nodes[i] = (DHTInterface)hosts.get( pairs[i].address );
		}

		int idx;

		// Now create count number of data!
		for (i = 0; i < count; i++ ) {
			PeerData data = PeerData.newContent(replication);

			idx = Arrays.binarySearch(pairs, data.getHash());

			// If the idx wasn't found determine the closest
			if (idx < 0) {
				idx = (-1 * (idx + 1)) % pairs.length;
				int idx2 = (idx - 1) % pairs.length;
				while (idx2 < 0)
					idx2 += pairs.length;

				long diffA = Math.abs( pairs[idx].nodeID - data.getHash() );
				long diffB = Math.abs( pairs[idx2].nodeID - data.getHash() );

				if (diffA > diffB) {
					idx = idx2;
				}
			}

			// Now find the K nearest
			for (int k = 0; k < replication; k++) {
				int idx2 = (idx + (k % 2 == 0 ? -1 : 1) * k);

				if (idx2 >= hostcount)
					idx2 -= hostcount;
				else if (idx2 < 0)
					idx2 += hostcount;

				nodes[ idx2 ].localPut(data);
				hostsWithKeys.add( nodes[ idx2 ] );
			}
		}

		return hostsWithKeys;
	}

	public static Set<DHTInterface> addPeerData(int count, int replication) {
		return addPeerData( Global.hosts.getType(ServicePeer.class), count, replication );
	}
}
