package sim.workload;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.events.Event;
import sim.events.EventException;
import sim.events.Events;
import sim.events.RunnableEvent;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.math.Pareto;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.InvalidHostException;
import sim.net.events.FailEvent;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.events.JoinEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.PutAndPassEvent;
import sim.net.overlay.dht.events.repeatable.FailAndPassEvent;
import sim.net.overlay.dht.events.repeatable.RandomRecvCountToAll;
import sim.net.overlay.dht.events.repeatable.RandomRecvCountPerPeer;
import sim.net.overlay.dht.events.repeatable.RecvToAllEvent;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.router.EdgeRouter;
import sim.net.router.Router;

import static sim.stats.StatsObject.SEPARATOR;

/*
 * Created on Feb 8, 2005
 */

/**
 * @author Andrew Brampton
 */
public abstract class Default implements  Workload {

	public Default(String[] arglist) throws Exception {

	}

	public static void setupNormal(int count) throws Exception {
		setupPeers(count);
		fastJoinAllNodes();
	}

	public static void setupSimulFail(int fail,int count) throws Exception {
		setupNormal(count);
		failRandomPeers(new Constant(0),fail);
	}

	public static void slowFailFromStart(int fail, long lasttime) throws Exception {
		long interval = 0;
		if (fail > 0) {interval = lasttime / fail;}
		// failures start at time 0
		Events.add(FailAndPassEvent.newEvent(new Constant(interval),fail),0);
	}

	public static List<Peer> setupPeers(int count) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);
		List<Peer> peers = new ArrayList<Peer>();

		for (int i=0;i<count;i++) {
			Peer p = new Peer(Global.lastAddress);
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p,e,NormalLink.BANDWIDTH_1024k,d);

			peers.add(p);

			Global.stats.logCount("Host" + SEPARATOR + "Wired");
		}

		return peers;
	}


	/**
	 * Sets up number of a given class with a lifetime
	 * @param type the class to be given a lifespan
	 * @param count the number to set up within the class, use -1 to set up all
	 */
	public static void setupLifetimes(Class<?> type, int count) {
		Distribution die = new Pareto(0.8598, 26.7993);
		Distribution resurrect = new Exponential(360000);

		HostSet peerList = Global.hosts.getType(type);
		Iterator<Host> peers = peerList.iterator();

		if (count < 0) {count = peerList.size();}

		while(peers.hasNext() || count > 0) {
			Peer p = (Peer)peers.next();
			// set this peer up to fail
			long failtime = die.nextLong() * 1000;

			// Fail the peer at time failtime
			Events.add(FailEvent.newEvent(p,true),failtime);
			long returntime = failtime + resurrect.nextLong();

			// Bring the peer back to life at time returntime
			Events.add(FailEvent.newEvent(p,false),returntime);
			// and rejoin him
			Events.add(JoinEvent.newEvent(p,p.getJoinAddress()),returntime);
			count--;
		}
	}

	/**
	 * Send DHT Messages from everyone to everyone
	 * @throws EventException
	 */
	public static void sendMessageToAll() {
		Events.addAfterLastEvent(RecvToAllEvent.newEvent(new Exponential(10000)));
	}

	/**
	 * Sends a given number of messages randomly between the addresses of known
	 * existing peers on the DHT
	 * @param count the number of messages to be sent
	 * @param direct whether or not messages should be sent to known existing addresses
	 * @throws EventException
	 */
	public static void sendRandomMessages(int count, boolean direct) {
		Events.addAfterLastEvent(RandomRecvCountToAll.newEvent(count, direct));
	}

	public static void sendRandomMessages(int count, boolean direct, long starttime) {
		Events.add(RandomRecvCountToAll.newEvent(count, direct), starttime);
	}

	public static void sendRandomMessagesPerPeer(int count, boolean direct) {
		Events.addAfterLastEvent(RandomRecvCountPerPeer.newEvent(count, direct));
	}

	public static void sendRandomMessagesPerPeer(int count, boolean direct, long starttime) {
		Events.add(RandomRecvCountPerPeer.newEvent(count, direct), starttime);
	}

	/**
	 * Sets count keys to be put, with a k replication, and returns the time
	 * that this should finish
	 * @param count
	 * @param k
	 * @return
	 */
	public static long generateRandomPuts(int count,int k) {
		Event e = PutAndPassEvent.newEvent(count, k);
		Events.addAfterLastEvent(e);
		return e.getEstimatedFinishedTime();
	}

	public static void generateRandomPuts(int count,int k, long starttime) {
		Events.add(PutAndPassEvent.newEvent(count, k),starttime);
	}

	public static void generateRandomGets(Distribution d,int count,long starttime) {
		Events.add(GetAndPassEvent.newEvent(d,count),starttime);
	}

	public static void generateRandomGets(HostSet hosts, Distribution d,int count) {
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(hosts, d,count));
	}

	public static void generateRandomGets(Distribution d,int count) {
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(d,count));
	}

	public static void failRandomPeers(Distribution d,int count) {
		Events.addAfterLastEvent(FailAndPassEvent.newEvent(d,count));
	}

	public static void failRandomPeers(int count) {
		Events.addAfterLastEvent(FailAndPassEvent.newEvent(count));
	}

	protected static void joinAllRouters() throws InvalidHostException {
		Router.createRoutingTables();
	}

	public static void fastJoinAllNodes() throws InvalidHostException {

		joinAllRouters();

		// Valid nodes
		HostSet peers = Global.hosts.getType(DHTInterface.class);

		Iterator<Host> i = peers.iterator();
		while (i.hasNext())
			if (i.next().hasFailed())
				i.remove();

		i = Global.hosts.getType(Peer.class).iterator();

		while (i.hasNext()) {
			Peer p = (Peer) i.next();
			peers.remove(p);
			p.fastJoin(peers);
			peers.add(p);
		}
	}

	public static void joinAllNodes() throws InvalidHostException {
		boolean firstPeer = true;

		joinAllRouters();

		HostSet joinedPeers = new HostSet();
		HostSet peers = Global.hosts.getType(DHTInterface.class);
		Iterator<Host> i = peers.iterator();
		while (i.hasNext()) {
			Peer p = (Peer)i.next();
			int joinAddress;

			if (firstPeer) {
				// Do a join to the special INVALID_ADDRESS
				// Basically don't join anyone ;)
				firstPeer = false;
				joinAddress = Host.INVALID_ADDRESS;
			} else {
				//Random joins
				joinAddress = joinedPeers.getRandom().getAddress();
			}

			Events.addAfterLastEvent(JoinEvent.newEvent(p, joinAddress));
			joinedPeers.add(p);
		}

	}

	public static void _workOutAverageTableSize() {
		Iterator<Host> i = Global.hosts.getType(Peer.class).iterator();

		while (i.hasNext()) {
			Peer p = (Peer) i.next();
			Global.stats.log("Peer" + SEPARATOR + "RoutingTable" + SEPARATOR + "Size", p.routingTable.size());
			Global.stats.log("Peer" + SEPARATOR + "LeafSet" + SEPARATOR + "Size", p.leafSet.size());

			for(int ii=0;ii<p.routingTable.getRows();ii++) {
				NodeAddressPairs[] row = p.routingTable.getRow(ii);

				for(int j=0;j<row.length;j++) {
					NodeAddressPairs cell = row[j];
					int size = 0;

					if (cell != null)
						size = cell.size();

					Global.stats.log("Peer" + SEPARATOR + "Cell(" + ii + "," + j + ")" + SEPARATOR + "Size", size);
				}
			}
		}
	}

	/**
	 * Works out the average routing table size
	 * and stores the results in the stats object
	 */
	public static void workOutAverageTableSize() {
		Events.addAfterLastEvent(new RunnableEvent() {

			@Override
			public void run() throws Exception {
				_workOutAverageTableSize();
			}

			@Override
			public long getEstimatedRunTime() {
				return 0;
			}

		});
	}

	/* (non-Javadoc)
	 * @see sim.workload.Workload#simulationFinished()
	 */
	public void simulationFinished() {
	}

	/* (non-Javadoc)
	 * @see sim.workload.Workload#simulationStart()
	 */
	public void simulationStart() {
	}

}
