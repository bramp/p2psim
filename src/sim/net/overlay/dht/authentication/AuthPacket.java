/**
 *
 */
package sim.net.overlay.dht.authentication;

import sim.net.Packet;
import sim.net.TrackableObject;

/**
 * A Packet containing sender auth data
 * @author Andrew Brampton
 *
 */
public abstract class AuthPacket extends Packet {

		// The sender's auth data. This may be unsigned
		public AuthData auth = null;

		protected void init(final int from, final int to, final AuthData auth) {
			init(from, to, auth, null);
		}

		protected void init(final int from, final int to, final AuthData auth, final TrackableObject data) {
			super.init(from, to, data);
			this.auth = auth;
			size += auth.getSize();
		}

		public static AuthPacket newPacket(int from, int to, final AuthData auth) {
			AuthPacket p = (AuthPacket) Packet.newPacket(AuthPacket.class);
			p.init(from, to, auth);
			return p;
		}

		/* (non-Javadoc)
		 * @see sim.net.Packet#_toString()
		 */
		@Override
		public String _toString() {
			return "Auth:" + auth.toString();
		}
}
