package sim.net.overlay.dht.events.repeatable;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.RegTestMessage;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.events.RecvEvent;

public class RandomIDMessagesSingleService extends Event {
	protected long nodeID;
	protected int msgCount;
	protected Constructor<? extends Message> constructor = null;

	public static RandomIDMessagesSingleService newEvent(int msgCount, long nodeID) {
		RandomIDMessagesSingleService e = (RandomIDMessagesSingleService) Event.newEvent(RandomIDMessagesSingleService.class);
		e.msgCount = msgCount;
		e.nodeID = nodeID;

		return e;
	}

	@Override
	public void run() throws Exception {
		// get according to nodeID
		NodeAddressPairs findChosen = new NodeAddressPairs();
		Iterator<Host> i = Global.hosts.getType(ServicePeer.class).iterator();
		while(i.hasNext()) {
			ServicePeer s = (ServicePeer)i.next();
			findChosen.add(new NodeAddressPair(s.getID(),s.getAddress()));
		}
		int chosenAddr = findChosen.findNumClosest(nodeID).address;

		Host chosen = Global.hosts.get(chosenAddr);

		if (!chosen.hasFailed()) {
			for(int ii=0; ii<msgCount; ii++) {
				Message m = new RegTestMessage(chosen.getAddress(), ((DHTInterface)chosen).getID(), Global.rand.nextLong());

				Event e = RecvEvent.newEvent(((DHTInterface)chosen), m);
				Events.addNow(e);
			}
		}

	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}

}
