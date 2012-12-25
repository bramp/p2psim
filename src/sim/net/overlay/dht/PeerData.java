package sim.net.overlay.dht;

import sim.math.Distribution;
import sim.math.Pareto2;
import sim.math.Zipf;
import sim.net.TrackableObject;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.overlay.dht.pastry.PeerBase;

final public class PeerData extends TrackableObject {
	public static final int DEFAULT_SIZE = 128;	// default filled packet size
	public static final int EMPTY = 4;			// empty packet size

	private static Distribution dist_id = null;
	private static Distribution dist_size = null;
	private static int count = 0;

	//TODO: this is currently entirely unfair for those putting in
	// content at a later date - newer content is far less popular than
	// the older content due to content IDs being based on rank
	// - is there a better way of doing this?
	private static long newHash() {
		count++;
		return PeerBase.hash(Double.toString(count));
	}

	/**
	 * Returns a valid ID for a piece of content
	 * @return
	 */
	public static long getValidID() {

		// recalculate distribution for new count
		if (dist_id == null)
			dist_id = new Zipf(1.0,count);

        return PeerBase.hash(Double.toString(dist_id.nextDouble()));
	}

	/**
	 * Quickly populates the network with 1 million pieces of content
	 *
	 */
	public static void quickHack() {
		count = 1000000;

		// recalculate distribution for new count
		dist_id = new Zipf(1.0, count);
	}

	public static PeerData newContent(int k) {
		//TODO: check this makes sense
		if (dist_size == null) {
			dist_size = new Pareto2(1.2, 12);
		}
		return new PeerData(newHash(), "data", dist_size.nextInt() + 1, k);
	}

	// instance variables
	private long hash;
	private Object data;
	private int size;
	private int k;

	public PeerData(long hash, Object data, int size, int k) {
		this.hash = hash;
		this.data = data;
		this.size = size;
		this.k = k;

		// only allow valid values of k
		if (k < 1 || k > PeerBase.l )
			throw new RuntimeException("Invalid k value " + k);
	}

	public PeerData(long hash, Object data) {
		this(hash, data, DEFAULT_SIZE, 1);
	}

	public PeerData(long hash) {
		// empty PeerData object - used if an object is not retrievable
		this(hash, null, EMPTY, 1);
	}

	public long getHash() {
		return hash;
	}

	public Object getData() {
		return data;
	}

	public int getSize() {
		return size;
	}

	public int getK() {
		return k;
	}

	/**
	 * Sets the replication factor
	 * @param k represents the number of nodes that should store this PeerData
	 */
	public void setK(int k) {
		this.k = k;
		toStringCache = null;
	}

	private String toStringCache = null;
	public String toString() {
		if (toStringCache == null)
			toStringCache = "PeerData(" + objectID + ") {" + Peer.toString(hash, true) + " k:" + k + " data:" + data + "}";

		return toStringCache;
	}
}
