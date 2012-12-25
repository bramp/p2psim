/**
 *
 */
package sim.net.overlay.dht.authentication;

import sim.net.Packet;

/**
 * A reply message to a AuthMePacket.
 * The auth field contains the sender's AuthData
 * The authreply field contains their signed AuthData, or null (if invalid) as the first element,
 * 	plus any others are useful AuthDatas
 * The data field should contain NULL
 *
 * @author Andrew Brampton
 *
 */
public class AuthReplyPacket extends AuthPacket {

	public AuthData[] authreply;

	protected void init(final int from, final int to, final AuthData auth, final AuthData[] authreply) {
		super.init(from, to, auth, null);

		stringCache = null;
		this.authreply = authreply;

		if (authreply != null) {
			for (int i = 0; i < authreply.length; i++)
				size += authreply[i].getSize();
		}
	}

	public static AuthReplyPacket newPacket(int from, int to, final AuthData auth, final AuthData authreply) {
		AuthReplyPacket p = (AuthReplyPacket) Packet.newPacket(AuthReplyPacket.class);
		p.init(from, to, auth, new AuthData[] { authreply } );
		return p;
	}

	/*
	public static AuthReplyPacket newPacket(int from, int to, final AuthData auth, final AuthData[] authreply) {
		AuthReplyPacket p = (AuthReplyPacket) Packet.newPacket(AuthReplyPacket.class);
		p.init(from, to, auth, authreply);
		return p;
	}
	*/

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.authentication.AuthPacket#_toString()
	 */
	@Override
	public String _toString() {

		if (stringCache == null) {
			stringCache = super._toString() + " AuthReply: [";
			for (int i = 0; i < authreply.length; i++)
				stringCache += " " + authreply[i].toString();
			stringCache += " ]";
		}

		return stringCache;
	}
	private String stringCache = null;

}
