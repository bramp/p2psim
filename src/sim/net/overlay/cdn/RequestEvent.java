package sim.net.overlay.cdn;

import sim.events.Event;

public class RequestEvent extends Event {

	Client c;
	int server;
	Media media;
	long start;
	long end;

	/**
	 * Creates an request with no end time
	 * @param c
	 * @param server
	 * @param media
	 * @param start Start time in bytes
	 * @return
	 */
	public static RequestEvent newEvent(final Client c, final int server, final Media media, final long start) {
		return newEvent(c, server, media, start, media.getByteLength());
	}

	/**
	 *
	 * @param c
	 * @param server
	 * @param media
	 * @param start Start time in bytes
	 * @param end  End time in bytes (if end == -1 then the request ends at the end of the fil)
	 * @return
	 */
	public static RequestEvent newEvent(final Client c, final int server, final Media m, final long start, final long end) {
		RequestEvent e = (RequestEvent) Event.newEvent(RequestEvent.class);
		e.c = c;

		// validate values
		if (start > m.getByteLength() || start < 0)
			throw new RuntimeException("Invalid start time " + start + " for " + m );

		if (end > m.getByteLength() || end < start)
			throw new RuntimeException("Invalid end time " + end + " for " + m );

		e.server = server;
		e.media = m;
		e.start = start;
		e.end = end;
		return e;
	}

	@Override
	public void run() throws Exception {
		c.startRequest(server, media, start, end, null);
	}

	@Override
	public long getEstimatedRunTime() {
		return 0;
	}
}
