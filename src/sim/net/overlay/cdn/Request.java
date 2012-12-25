package sim.net.overlay.cdn;

import sim.events.Events;

/**
 * Class representing status of a request
 */
public class Request {

	/**
	 * The unique ID of the media
	 */
	protected final int media;

	/**
	 * The beginning byte of the request
	 */
	protected final long start;

	/**
	 * The simulator time this request started
	 */
	protected final long startTime;

	/**
	 * The position within the media that this client wants
	 */
	protected long position;

	/**
	 * The final byte requested by this request
	 */
	protected final long end;

	/**
	 * The ID of this request
	 */
	protected final int requestID;

	/**
	 * The byterate this request asks for
	 */
	protected final int byterate;

	/**
	 *
	 * @param media
	 * @param start in bytes
	 * @param end in bytes
	 */
	public Request (final int requestID, final int media, final long start, long end, final int byterate) {

		// validate values
		Media m = Media.getMedia(media);

		if (end == -1)
			end = m.getByteLength();

		if (start > m.getByteLength() || start < 0)
			throw new RuntimeException("Invalid start time");

		if (end > m.getByteLength() || end < start)
			throw new RuntimeException("Invalid end time");

		this.requestID = requestID;

		this.media = media;
		this.start = start;
		this.position = start;
		this.end = end;

		this.byterate = byterate;

		this.startTime = Events.getTime();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Request(" + requestID + " " + Media.getMedia(media) + " Start:" + start + " Pos:" + position + " End:" + end + ")";
	}

	public long getPosition() {
		return position;
	}

	public int getMedia() {
		return media;
	}
}