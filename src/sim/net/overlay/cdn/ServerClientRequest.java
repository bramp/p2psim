package sim.net.overlay.cdn;

/**
 * Class representing status of client requests stored on a server
 */
class ServerClientRequest extends Request {

	/**
	 * The client's IP
	 */
	final int host;

	/**
	 * Indicates if this request has been cancelled
	 */
	public boolean cancelled;

	/**
	 * @param requestID
	 * @param media
	 * @param start
	 * @param end
	 * @param byterate
	 */
	public ServerClientRequest(final int from, final int requestID, final int media, final long start, final long end, final int byterate) {
		super(requestID, media, start, end, byterate);

		this.host = from;
		this.cancelled = false;
	}

}