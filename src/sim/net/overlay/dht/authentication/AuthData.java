/**
 *
 */
package sim.net.overlay.dht.authentication;

import sim.main.Global;
import sim.net.TrackableObject;
import sim.net.overlay.dht.pastry.Peer;
import static sim.net.overlay.dht.DHTInterface.INVALID_ID;

/**
 * This is a certificate
 * @author Andrew Brampton
 *
 */
public class AuthData extends TrackableObject {

	public static final long GLOBAL_KEY = -2;

	public static final long INVALID_KEY = INVALID_ID;

	/**
	 * Whos is this AuthData?
	 */
	protected final long owner;

	/**
	 *  Who signed this certificate
	 */
	protected long signedby = INVALID_KEY;

	/**
	 * Flag to indicate if this AuthData can be used to sign other AuthDatas
	 */
	protected boolean canSignOthers = false;

	/**
	 * Simple check to see if this is signed correctly
	 */
	//public boolean valid = true;

	/**
	 * Construct a AuthData object that has not been signed yet
	 * This AuthData is not valid until signed
	 * @param owner
	 */
	public AuthData(long owner) {
		this.owner = owner;
		//this.valid = false;
		this.toStringCache = null;
	}

	/**
	 * Construct a new AuthData object that has been signed
	 * @param owner
	 * @param signedby
	 */
	public AuthData(long owner, long signedby) {
		this.owner = owner;
		this.signedby = signedby;
		this.canSignOthers = true;
		this.toStringCache = null;
	}

	/**
	 * Sign this AuthData with the passed in AuthData
	 * @param auth
	 */
	public void sign(AuthData auth) {
		this.signedby = auth.owner;
		//this.valid = true;

		toStringCache = null;
	}

	/**
	 * Returns the ID of the signer
	 * @return
	 */
	public final long signer() {
		return signedby;
	}


	/**
	 * Returns the owner of this AuthData
	 * @return
	 */
	public final long owner() {
		return owner;
	}

	public final boolean canSignOthers() {
		return canSignOthers;
	}

	/* (non-Javadoc)
	 * @see sim.net.TrackableObject#getSize()
	 */
	@Override
	public final int getSize() {
		int size = 0;

		// Our public key
		size += Global.auth_key_size / 8;

		// The signature of our certificate
		size += Global.auth_key_size / 8;

		// Some info about the signer (ie name)
		size += 10;

		// Bitset for the permission
		size += 4;

		// Validility dates
		size += 8;

		return size;
	}

	/*
	public int compareTo(Object arg) {

		if (arg instanceof AuthData) {
			AuthData auth = (AuthData) arg;

			if ( auth.owner > owner )
				return -1;
			else if (auth.owner < owner)
				return 1;
			else // if (arg.owner == owner)
				return (int) (auth.signedby - signedby);

		} else if (arg instanceof Long) {
			return (int) ((Long) arg - owner);
		} else {
			throw new ClassCastException ();
		}
	}
	*/

	private String toStringCache = null;
	public String toString() {
		if (toStringCache == null) {
			toStringCache = "AuthData(" + objectID + ") {owner " + Peer.toString(owner, true) + ", issuer ";

			if (signedby == GLOBAL_KEY)
				toStringCache += "{GLOBAL_KEY}";
			else if (signedby == INVALID_KEY)
				toStringCache += "{INVALID_KEY}";
			else
				toStringCache += Peer.toString(signedby, true);

			toStringCache += "}";
		}

		return toStringCache;
	}
}
