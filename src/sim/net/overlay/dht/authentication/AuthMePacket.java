/**
 *
 */
package sim.net.overlay.dht.authentication;

import sim.net.Packet;

/**
 * Message used to check login creditials
 * The auth field is set to the unsigned AuthData
 * @author Andrew Brampton
 *
 */
public class AuthMePacket extends AuthPacket {

		public static AuthMePacket newPacket(int from, int to, final AuthData auth) {

			if (auth.signer() != AuthData.INVALID_KEY)
				throw new RuntimeException("Can't send AuthMePacket without a signed AuthData");

			AuthMePacket p = (AuthMePacket) Packet.newPacket(AuthMePacket.class);
			p.init(from, to, auth);
			return p;
		}
}
