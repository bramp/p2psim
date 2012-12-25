/**
 *
 */
package sim.net.overlay.meridian;

import java.util.HashSet;
import java.util.Iterator;

import sim.main.Global;
import sim.net.Host;

/**
 * Class to represent the Meridian Rings
 * @author Andrew Brampton
 *
 */
public class Rings {

	protected class HostPair implements Comparable<HostPair> {

		public final static int INVALID_DISTANCE = Integer.MAX_VALUE;

		public final int host;
		public final int distance;

		public HostPair(final int host, final int distance) {
			this.host = host;
			this.distance = distance;
		}

		public HostPair(final int host) {
			this.host = host;
			this.distance = INVALID_DISTANCE;
		}

		public int compareTo(HostPair o) {
			return host - o.host;
		}

		public boolean equals(Object h) {
			if (!(h instanceof HostPair))
				return false;
			else
				return equals( (HostPair) h );
		}

		public boolean equals(HostPair h) {
			return this.host == h.host;
		}

		public boolean equals(int host) {
			return this.host == host;
		}

		@Override
		public int hashCode() {
			return host;
		}
	}

	protected class HostPairs extends HashSet<HostPair> {}

	/**
	 * Radius of each ring from the center
	 */

	int[] radii;

	/**
	 * The hosts in each ring
	 */
	HostPairs[] rings;

	public Rings(int[] radii) {

 	//System.out.println(radii.length);

		if (Global.debug) {
			// Make sure the rings have increasing radii
			for (int i = 1; i < radii.length; i++) {
				if (radii[i - 1] > radii[i])
					throw new RuntimeException("Invalid ring radii");
			}
		}

		// Create radii + 1 rings, the plus one is for anything outside
		rings = new HostPairs[ radii.length + 1 ];
		for (int i = 0; i < rings.length; i++) {
			rings[i] = new HostPairs();
		}
	}

	/**
	 * Returns which ring this distance would belong in
	 * @param distance
	 * @return
	 */
	public int getRing(int distance, int[] radii) {

		for (int i = 0; i < radii.length; i++) {
			if (distance < radii[i])
				return i;
		}

		return radii.length;

	}

	/**
	 * Find which ring this host is in
	 * @param host
	 * @return Returns the ring, or -1 if the host is not in a ring
	 */
	public int getHostRing(int host) {
		HostPair hh = new HostPair(host);

		for (int i = 0; i < rings.length; i++) {
			if (rings[i].contains(hh))
				return i;
		}

		return -1;
	}

	/**
	 * Add a host into one of the rings
	 * @param h
	 * @param distance
	 * @return
	 */
	public boolean add(final int h, final int distance, int[] radii) {
		int ring = getRing(distance, radii);

		return rings[ring].add( new HostPair(h, distance) );
	}

	public boolean contains(final int h) {
		return getHostRing(h) != -1;
	}

	/**
	 *
	 */
	public void clear() {
		for (int i = 0; i < rings.length; i++) {
			rings[i].clear();
		}
		rings = null;
		radii = null;
	}

	public String toString() {
		StringBuilder ret = new StringBuilder("Ring (");

		for (int i = 0; i < rings.length; i++) {
			Iterator<HostPair> ii = rings[i].iterator();
			while (ii.hasNext()) {
				ret.append( Host.toString( ii.next().host ) );
				ret.append( ',' );
			}
			if (ret.charAt(ret.length() - 1) == ',')
				ret.setLength( ret.length() - 1 ); // Chop the last ,

			ret.append( ':' );
		}

		if (ret.charAt(ret.length() - 1) == ':')
			ret.setLength( ret.length() - 1 ); // Chop the last :

		ret.append(')');

		return ret.toString();
	}
}
