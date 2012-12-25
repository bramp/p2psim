/*
 * Created on Feb 8, 2005
 */
package sim.net;

import java.util.List;
import sim.collections.ClassLists;
import sim.events.Events;
import sim.main.Global;
import sim.main.Helper;
import static sim.stats.StatsObject.SEPARATOR;

/**
 * @author Andrew Brampton
 */
public class Packet extends TrackableObject {
	public int to;
	public int from;

	public int hops;

	public int size;
	public TrackableObject data;

	/**
	 * Does this packet need to be sent (or can the sim end early?)
	 */
	public boolean critical;

	/**
	 * The time this packet was created
	 */
	public long sentTime = Long.MIN_VALUE;

	protected static ClassLists<Packet> freePackets = new ClassLists<Packet>();
	protected static Packet newPacket(final Class<? extends Packet> c) {
		Packet p;
		List<? extends Packet> packets = freePackets.getType(c);

		Global.stats.logCount("Net" + SEPARATOR + "Packet");

		if (!c.equals(Packet.class))
			Global.stats.logCount("Net" + SEPARATOR + Helper.getShortName(c));

		if (packets.isEmpty()) {
			try {
				return c.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		p = packets.remove(packets.size() - 1);
		freePackets.remove(p);

		// Reset this packet, and return
		p.newID();

		return p;
	}

	/*
	public static Packet newPacket(final int from, final int to, final int size, final TrackableObject data) {
		Packet p = newPacket(Packet.class);
		p.init(from, to, size, data);
		return p;
	}
	public static Packet newPacket(final int from, final int to, final TrackableObject data) {
		return newPacket(from, to, data != null ? data.toString().length() : 0, data);
	}
	*/
	public static Packet newPacket(final int from, final int to, final TrackableObject data) {
		Packet p = newPacket(Packet.class);
		p.init(from, to, data);
		return p;
	}

	public void free() {
		freePackets.add(this);
	}

	/* This could be placed here, however freePackets is a classList
	 * not a classSet, meaning we should never double free!
	 * So, its safer to try and free() packets when we finish with them
	 * not when the GC finishes with them
	protected void finalize() throws Throwable {
		super.finalize();
		this.free();
	} */

	protected Packet() {}

	/*
	protected void init(final int from, final int to, final int size, final TrackableObject data) {
		this.from = from;
		this.to = to;
		this.data = data;
		this.size =	20 + ((data != null) ? data.getSize() : 0); // 20 Byte IP header + data
		hops = 0;
		sentTime = Events.getTime();
		toStringCache = null;
		critial = true;
	}

	protected void init(final int from, final int to, final TrackableObject data) {
		init(from, to, data != null ? data.toString().length() : 0, data);
	}
	*/
	protected void init(final int from, final int to, final TrackableObject data) {
		this.from = from;
		this.to = to;
		this.data = data;
		this.size =	20 + ((data != null) ? data.getSize() : 0); // 20 Byte IP header + data
		hops = 0;
		sentTime = Events.getTime();
		toStringCache = null;
		critical = true;

		assert from > 0;
		assert to > 0;
		assert this.size > 0;
	}

	public void addHop() {
		// If this is the first hop, set the time
		if (sentTime == Long.MIN_VALUE)
			sentTime = Events.getTime();

		hops++;
	}

	public int getHopCount() {
		return hops;
	}

	public int getDelay() {
		return (int)(Events.getTime() - sentTime);
	}

	public String _toString() {
		return "";
	}

	private String toStringCache = null;
	@Override
	public final String toString() {
		StringBuilder sb;

		if (toStringCache == null) {
			sb = new StringBuilder();
			sb.append(Helper.getShortName(this));
			sb.append('(');
			sb.append(objectID);
			sb.append(") ");
			sb.append(Host.toString(from));
			sb.append('>');
			sb.append(Host.toString(to));
			sb.append(" size:");
			sb.append(size);
			sb.append("b data:");

			if (data == null) {
				sb.append( "(null)" );
			} else {
				sb.append(Helper.getShortName( data ) );
				sb.append('(');
				sb.append(data.objectID );
				sb.append(')');
			}

			sb.append(_toString());

			toStringCache = sb.toString();
		} else {
			// TODO change this to cache the StringBuilder (not the string)
			//   and then setlength here
			sb = new StringBuilder(toStringCache);
		}

		sb.append(" hops:");
		sb.append(getHopCount());
		sb.append(" delay:");
		sb.append(getDelay());

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see sim.net.TrackableObject#getSize()
	 */
	@Override
	public int getSize() {
		return size;
	}

	//public void setSize(final int size) {
	//	this.size = size;
	//}
}
