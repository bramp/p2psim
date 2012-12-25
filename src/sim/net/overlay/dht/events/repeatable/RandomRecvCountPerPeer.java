package sim.net.overlay.dht.events.repeatable;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.TestMessage;
import sim.net.overlay.dht.events.RecvEvent;

public class RandomRecvCountPerPeer extends RepeatableHelperEvent {
	protected Iterator<Host> i;
	protected int msgCount;
	protected boolean direct;
	protected static final int DEFAULT_DELAY = 10000; // The time between each event
	protected Constructor<? extends Message> constructor = null;

	public static RandomRecvCountPerPeer newEvent(int msgCount, boolean direct) {
		return newEvent(new Constant(DEFAULT_DELAY), msgCount, direct);
	}

	public static RandomRecvCountPerPeer newEvent(Distribution d, int msgCount, boolean direct) {
		return newEvent(d, msgCount, direct, TestMessage.class);
	}

	public static RandomRecvCountPerPeer newEvent(int msgCount, boolean direct, Class<? extends Message> messageType) {
		return newEvent(new Constant(DEFAULT_DELAY), msgCount, direct, messageType);
	}

	protected void init(int msgCount, boolean direct, Class<? extends Message> messageType) {
		init(new Constant(DEFAULT_DELAY), msgCount, direct, messageType);
	}

	@SuppressWarnings("unchecked")
	protected void init(Distribution d, int msgCount, boolean direct, Class<? extends Message> messageType) {
		init(d,(Global.hosts.getType(DHTInterface.class).size()));
		this.msgCount = msgCount;
		this.direct = direct;
		i = null;

		// Find the correct constructor
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

	protected static RandomRecvCountPerPeer newEvent(Distribution d, int msgCount, boolean direct, Class<? extends Message> messageType) {
		RandomRecvCountPerPeer e = (RandomRecvCountPerPeer) Event.newEvent(RandomRecvCountPerPeer.class);
		e.init(d, msgCount, direct, messageType);
		return e;
	}

	@Override
	public void run() throws Exception {

		if (i == null)
			i = Global.hosts.getType(DHTInterface.class).iterator();

		if (i.hasNext()) {
			HostSet peers = Global.hosts.getType(DHTInterface.class);
			Host h0 = i.next();
			DHTInterface p0 = (DHTInterface) h0;

			if (!h0.hasFailed()) {
				for(int i=0; i<msgCount; i++) {
					long nodeID;

					if (direct) {
						Host h1 = peers.getRandom();
						HostSet joined = getJoinedPeers();
						joined.remove(h0); // Remove me so I can't be picked

						h1 = joined.getRandom();

						DHTInterface p1 = (DHTInterface)h1;
						nodeID = p1.getID();
					}
					else {
						nodeID = Global.rand.nextLong();
					}

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
