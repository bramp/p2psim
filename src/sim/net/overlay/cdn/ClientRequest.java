package sim.net.overlay.cdn;

/**
 * Class representing status of client requests
 */
public class ClientRequest extends Request {

	/**
	 * Is this a prefetch request?
	 */
	final boolean prefetch;

	/**
	 * Have we logged the latency for this request yet?
	 */
	boolean loggedLatency = false;
	
	/**
	 *
	 * @param requestID
	 * @param media
	 * @param start The first byte to request
	 * @param end The last byte to request
	 */
	public ClientRequest (final int requestID, final int media, final long start, final long end) {
		this (requestID, media, start, end, Media.getMedia(media).getByterate(), false);
	}

	/**
	 *
	 * @param requestID
	 * @param media
	 * @param start The first byte to request
	 * @param end The last byte to request
	 * @param byterate
	 * @param prefetch
	 */
	public ClientRequest (final int requestID, final int media, final long start, final long end, final int byterate, final boolean prefetch) {
		super (requestID, media, start, end, byterate);
		this.prefetch = prefetch;
	}

	public boolean isPrefetch() {
		return prefetch;
	}

	@Override
	public String toString() {
		return (prefetch ? "Prefetch" : "") + "Request(" + requestID + " " + Media.getMedia(media) + " Start:" + start + " Pos:" + position + " End:" + end + ")";
	}

}