/*
 * Created on 05-Mar-2005
 */
package sim.net.overlay.dht.events;

import sim.events.Event;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.Message;

/**
 * A Packet is sent to a Peer, which then trys to route it
 * In most cases this event is called on the Peer that the packet is destined for
 * @author Andrew Brampton
 */
public class RecvEvent extends Event {
	public DHTInterface peer;
	public Message m;

	public static RecvEvent newEvent(DHTInterface peer, Message m) {
		RecvEvent e = (RecvEvent) Event.newEvent(RecvEvent.class);
		e.peer = peer;
		e.m = m;
		return e;
	}

	@Override
	public void run() {
		peer.recv(m);
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}
}