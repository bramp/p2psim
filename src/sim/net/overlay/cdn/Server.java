package sim.net.overlay.cdn;

import static sim.stats.StatsObject.SEPARATOR;

import java.util.Map;
import java.util.TreeMap;

import sim.events.Events;
import sim.math.Constant;
import sim.net.ErrorPacket;
import sim.net.Packet;
import sim.net.links.Link;
import sim.net.overlay.cdn.cache.Cache;

/**
 * A server node on the network that supplies content
 * @author Andrew Brampton
 *
 */
public class Server extends AbstractClient implements Tickable<ServerClientRequest> {

	/**
	 * List of clients current being served (indexed by RequestID)
	 */
	Map <Integer, ServerClientRequest> clients = new TreeMap<Integer, ServerClientRequest>();

	public Server(int address, Cache cache) {
		super(address, cache);
	}

	/**
	 * Creates a requestID from the client's IP + unique client number
	 * @return
	 */
	private static int makeRequestID(int client, int request) {
		// TODO we can not have more than 65535 requests, OR this might fail
		return client * 0xFFFF + (request & 0xFFFF);
	}

	/* (non-Javadoc)
	 * @see sim.net.Host#recv(sim.net.links.Link, sim.net.Packet)
	 */
	@Override
	public void recv(Link link, Packet p) {
		super.recv(link, p);

//		if (Global.debug_log_messages)
//			Trace.println(LogLevel.LOG2, Host.toString(getAddress()) + " recv " + p.toString());

		if (p instanceof RequestPacket) {
			incomingRequest((RequestPacket) p);

		} else if (p instanceof StopPacket) {
			incomingStop((StopPacket) p);

		} else if (p instanceof ErrorPacket) {
			throw new RuntimeException("Opps, we shouldn't have to deal with errors! (" + p + ")");
		}
	}

	/**
	 * This method is called to serve a client request
	 * @param c
	 * @return True if we want future ticks, otherwise false
	 */
	public boolean tick(ServerClientRequest c) {

		//Trace.println(LogLevel.DEBUG, "Tick: " + c);

		final long start = c.position;
		final long end = Math.min(c.position + Common.packetSize, c.end);

		long miss = checkCache(cache, c.media, c.position, end, "CDN" + SEPARATOR + "ServerCache" + SEPARATOR);

		// If something was missed now fill the cache
		// TODO actually make the server request this chunk from upstream
		if (miss > 0) {
			// Fill the cache with the missing segment
			cache.add(c.media, start, end);

			//Trace.println(LogLevel.DEBUG, cache);
		}

		// Add this request to what's popular
		//RangeCounts pop = getPopularity(c.media);
		//pop.add( new Range(start, end) );

		// Check if we actually want to send the MediaPacket
		if (Common.streamPackets) {
			// Construct a new mediaPacket
			MediaPacket mp = MediaPacket.newPacket(this.address, c.host, c.requestID, c.media, start, end);

			send(mp);
		}

		// Now move this request along
		c.position = end;

		// and check if its ended
		if (c.position >= c.end || c.cancelled) {
			doStop( makeRequestID( c.host, c.requestID) );
			return false;
		}

		return true;
	}

	protected void incomingRequest(RequestPacket p) {
		// TODO Check if we have room for this request

		final int requestID = makeRequestID(p.from, p.request);

		// Stop serving the previous client's request (if one exists)
		doStop( requestID );

		// Now create the new ClientRequest
		ServerClientRequest c = new ServerClientRequest(p.from, p.request, p.media, p.start, p.end, p.byterate);

		// and add him to the clients we are serving
		clients.put(requestID, c);

		// If Media is a 1megabit/second CBR stream, and each packet is 1400 bytes, then one packet is
		// needed every 0.0112 seconds (or 11ms)
		if (Common.realTicks) {
			final double byterate = p.byterate; //Media.getMedia(p.media).getByterate();
			final double interval = Math.ceil( 1.0 / (byterate / (double)Common.packetSize) * 1000.0 );
			final int count = (int) Math.ceil( (p.end - p.start) / (double)Common.packetSize ) + 1;

			Events.addNow( TickEvent.newEvent(new Constant(interval), count, this, c ) );
		}

		// Echo a request has started
		//Trace.println(LogLevel.LOG1, Host.toString(address) + ": Request " + requestID + " of " + clients.size() + " started " + c);

		// Work out the current hotspots
		//RangeCounts pop = getPopularity(c.media);
		//hotspots.put( c.media , pop.top( 0.3 ) );
		//System.out.println( hotspots.get(c.media) );
	}

	protected void incomingStop(StopPacket p) {
		doStop ( makeRequestID(p.from, p.request) );
	}

	protected void doStop(int requestID) {

		ServerClientRequest c = clients.remove(requestID);

		// If this request doesn't exist, then exit (maybe throw a warning?)
		if (c == null) {
			return;
			//Trace.println(LogLevel.ERR, "Stopped invalid request " + requestID);
			//throw new RuntimeException("Stopped invalid request");
		}

		// If we are not streaming the packets, artificially move the position along
		if (!Common.realTicks) {
			Media m = Media.getMedia(c.media);

			c.position += (long)((Events.getTime() - c.startTime) / 1000.0 * m.getByterate());

			if ( c.position > m.getByteLength() )
				c.position = m.getByteLength();
		}

		//Trace.println(LogLevel.LOG1, Host.toString(address) + ": Request " + requestID + " stopped " + c);
		c.cancelled = true;

		// The last request lasted for 0 seconds!
		//if (c.startTime == Events.getTime()) {
		//	throw new RuntimeException("Removing a request that lasted for zero seconds!");
		//}
	}
}
