package sim.workload.stealth;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.Peer;
import sim.net.overlay.dht.stealth.events.JoinAndPassEvent;

public class RecoveryTest extends Default {
	public RecoveryTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double ratio = Double.parseDouble(arglist[1]);
		Distribution joinDistribution = new Constant(100000);

		setupPeers(count, ratio);

		joinAllRouters();

		Peer.USE_RECOVERY_PIGGYBACK = true;

		// Valid nodes
		HostSet peers = Global.hosts.getType(DHTInterface.class);

		Iterator<Host> i = peers.iterator();
		while (i.hasNext())
			if (i.next().hasFailed())
				i.remove();

		i = Global.hosts.getType(ServicePeer.class).iterator();
		while (i.hasNext()) {
			Peer p = (Peer) i.next();
			peers.remove(p);
			p.fastJoin(peers);
			peers.add(p);
		}

		// Join them due to this distribution
		Events.addNow(JoinAndPassEvent.newEvent(joinDistribution, (int) (count * (1 - ratio))));
	}
}
