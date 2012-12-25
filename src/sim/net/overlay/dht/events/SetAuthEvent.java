/**
 *
 */
package sim.net.overlay.dht.events;

import sim.events.Event;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.authentication.AuthData;

/**
 * Causes a given peer to attempt to retrieve data from the DHT, based
 * on the hash to which the GetMessage is sent
 * @author macquire
 */
public class SetAuthEvent extends Event {
	public DHTInterface peer;
	public AuthData auth;

	public static SetAuthEvent newEvent(DHTInterface peer, AuthData auth) {
		SetAuthEvent e = (SetAuthEvent) Event.newEvent(SetAuthEvent.class);
		e.peer = peer;
		e.auth = auth;
		return e;
	}

	@Override
	public void run() {
		peer.setAuth(auth);
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}
}
