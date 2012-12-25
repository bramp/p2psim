package sim.net.overlay.dht.events.repeatable;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.events.RecvEvent;

public class ConstantIDMessagePerStealthPeer extends RepeatableHelperEvent {
	protected Iterator<Host> i;
	protected int msgCount;
	protected long nodeID;
	protected Constructor<? extends Message> constructor = null;

	@SuppressWarnings("unchecked")
	protected void init(Distribution d, int msgCount, long nodeID, Class<? extends Message> messageType) {
		init(d,(Global.hosts.getType(StealthPeer.class).size()));
		this.msgCount = msgCount;
		this.nodeID = nodeID;
		i = null;

		// Find the correct constructor
		@SuppressWarnings("unchecked")
		Constructor<?> consts[] = messageType.getConstructors();
		for (int ii = 0; ii < consts.length && constructor == null; ii++) {
			Class params[] = consts[ii].getParameterTypes();

			// Make sure the params match
			if (params.length == 3) {
				if (params[0] != int.class) //int fromAddress
					continue;

				if (params[1] != long.class) //long fromID
					continue;

				if (params[2] != long.class) //long toID
					continue;

				constructor = (Constructor<? extends Message>)consts[ii];
				break;
			}
		}

		if (constructor == null) {
			throw new RuntimeException("RandomRecvCountPerPeer was given a non message type as a param");
		}
	}

	public static ConstantIDMessagePerStealthPeer newEvent(Distribution d, int msgCount, long nodeID, Class<? extends Message> messageType) {
		ConstantIDMessagePerStealthPeer e = (ConstantIDMessagePerStealthPeer) Event.newEvent(ConstantIDMessagePerStealthPeer.class);
		e.init(d, msgCount, nodeID, messageType);
		return e;
	}

	@Override
	public void run() throws Exception {

		if (i == null)
			i = Global.hosts.getType(StealthPeer.class).iterator();

		if (i.hasNext()) {
			Host h0 = i.next();
			DHTInterface p0 = (DHTInterface) h0;

			if (!h0.hasFailed()) {
				for(int i=0; i<msgCount; i++) {
					Object params[] = {h0.getAddress(), p0.getID(), nodeID};
					Message m = constructor.newInstance( params );

					Event e = RecvEvent.newEvent(p0, m);
					Events.addNow(e);
				}
			}
		}

		reschedule();
	}
}
