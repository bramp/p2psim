/**
 *
 */
package sim.net.overlay.cdn;

import java.util.Map;

import sim.collections.Range;
import sim.math.Distribution;

/**
 * Indicates the length and the start of a hotspot within a piece of Media
 * @author Andrew Brampton
 *
 */
public class Hotspot extends Range implements Cloneable {

	/**
	 * The media this hotspot is within
	 */
	Media media;

	/**
	 * Optional name for this hotspot (generally the name of the bookmark
	 */
	public final String name;

	/**
	 * Optional list of occurences of travelling to another hotspot
	 */
	public Map<String, Integer> sequence; // = new TreeMap<String, Integer>()

	/**
	 * The total number of requests for this hotspot
	 */
	public int hits;

	/**
	 * A model that represents the length of this hotspot
	 */
	public Distribution length;

	/**
	 *
	 * @param m
	 * @param name
	 * @param start Start time in seconds
	 * @param length Length in seconds
	 */
	public Hotspot(Media m, String name, long start, long length) {

		super(start, start+length);

		this.media = m;
		this.name = name;
	}

	/**
	 *
	 * @param m
	 * @param start Start time in seconds
	 * @param length Length in seconds
	 */
	public Hotspot(Media m, long start, long length) {
		this(m, null, start, length);
	}

	/**
	 * Returns the media that this hotspot is in
	 * @return
	 */
	public Media getMedia() {
		return media;
	}

	public String toString() {
		return name + " " + start + "-" + end + (sequence == null ? "" : " (" + sequence.size() + " predictions)");
	}

	/**
	 * Creates a clone of this hotspot
	 */
	public Hotspot clone() {
		Hotspot h = new Hotspot(media, name, start, length());

		// Now copy all the addition data
		if (sequence != null) {
			//h.sequence = new TreeMap<String, Integer>( sequence );
			h.sequence = sequence; // We dont' actually need to copy this
		}

		h.hits = hits;

		return h;
	}

	/* (non-Javadoc)
	 * @see sim.collections.Range#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Hotspot) {
			return name.equals( ((Hotspot)obj).name );

		} else if (obj instanceof String) {

			return name.equals(obj);
		}

		return super.equals(obj);
	}

}