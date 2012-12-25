/**
 *
 */
package sim.net.overlay.cdn;

import sim.collections.ArrayRangeCounts;

/**
 * @author Andrew Brampton
 *
 */
public abstract class Common {

	/**
	 *  This flag decides if packets are actually sent for the streams
	 *  If true many packets are generated and the simulations slows
	 */
	public static final boolean streamPackets = true;

	/**
	 * This flag decides if CDN Nodes receive ticks. The ticks allow for each packet of a request
	 * to be served. If realTicks == false then packets will never be streamed
	 */
	public static final boolean realTicks = true;

	/**
	 * The size of each media packet (in bytes)
	 */
	//public final static long packetSize = 1400;
	public final static long packetSize = 10000;

	/**
	 * Keeps log of which seconds were hits/misses
	 */
	public static boolean logMissHit = false;
	public static ArrayRangeCounts hits = null;
	public static ArrayRangeCounts miss = null;
}
