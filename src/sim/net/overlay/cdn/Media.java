package sim.net.overlay.cdn;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.math.Distribution;
import sim.math.Uniform;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * Represents some kind of Media
 * @author Andrew Brampton
 *
 */
public class Media {

	/**
	 * ArrayList of all the valid media
	 */
	protected static ArrayList<Media> medias = new ArrayList<Media>();

	/**
	 * The ID for this media
	 */
	protected final int ID;

	/**
	 * The length of this media (in seconds)
	 */
	protected final int length;

	/**
	 * The byterate of the media
	 */
	protected final int byterate;

	/**
	 * The hotspots for this media
	 */
	protected final List<Hotspot> hotspots = new ArrayList<Hotspot>();

	/**
	 * Creates a new piece of Media
	 * @param length The length in seconds of the media
	 * @param byterate The CBR byterate of the video
	 * @throws Exception
	 */
	public Media( final int length, final int byterate ) throws Exception {
		this(length, byterate, null);
	}

	public Media( final int length, final int byterate, List<Hotspot> hotspots ) throws Exception {
		if (length <= 0)
			throw new RuntimeException("Invalid length " + length);

		this.length = length;
		this.byterate = byterate;

		this.ID = medias.size();

		if (hotspots != null) {
			Iterator<Hotspot> i = hotspots.iterator();
			while (i.hasNext()) {
				addHotspot( i.next() );
			}
		}

		medias.add(this);

		Trace.println(LogLevel.LOG1, this + " added Hotspots:" + hotspots);
	}

	/**
	 * Creates a new piece of Media
	 * @param length In seconds
	 * @param byterate
	 * @param hotspots
	 * @param hotspotLengths
	 */
	public Media( final int length, final int byterate, int hotspots, Distribution hotspotStarts, Distribution hotspotLengths ) {

		this.length = length;
		this.byterate = byterate;

		this.ID = medias.size();

		medias.add(this);

		if (hotspotStarts == null)
			hotspotStarts = new Uniform(0, getLength());

		int exceptions = 0;
		while (hotspots > 0) {
			try {
				addHotspot(hotspotStarts.nextInt(), hotspotLengths.nextInt());
				hotspots--;
			} catch (Exception e) {
				exceptions++;
				if (exceptions >= 1000)
					throw new RuntimeException("Unable to create all the media!");
			}
		}


		Trace.println(LogLevel.LOG1, this + " added Hotspots:" + this.hotspots);
	}

	public void addHotspot(Hotspot h) throws Exception {
		int videolength = getLength();

		// Check this is within the ranges of the video
		if (h.start < 0 || h.length() < 0)
			throw new Exception("Out of range");

		if (h.start >= videolength)
			throw new Exception("Hotspot starts after the video");

		if (h.end > videolength || h.length() > videolength)
			throw new Exception("Hotspot end after the video");

		// TODO check it doesn't collide with any other hotspot
		h.media = this;

		hotspots.add(h);
	}

	public void addHotspot(String name, int start, int length) throws Exception {
		addHotspot( new Hotspot(this, name, start, length) );
	}

	/**
	 * Adds a hotspot to the video
	 * @param start (in seconds)
	 * @param length in seconds
	 * @throws Exception
	 */
	public void addHotspot(int start, int length) throws Exception {
		addHotspot( new Hotspot(this, start, length) );
	}

	/**
	 * Creates count new media, using media sizes from the distribution
	 * @param count The number of new media to create
	 * @param lengths The distriubtion of the media's length (in seconds)
	 * @throws Exception
	 */
	public static void generateMedia(int count, Distribution lengths, Distribution bitrate) throws Exception {
		for (int i = 0; i < count; ++i) {
			new Media ( lengths.nextInt(), bitrate.nextInt() );
		}
	}

	/**
	 * Creates count new media, using media sizes from the distribution
	 * as well as creating hotspots
	 * @param count
	 * @param lengths In seconds
	 * @param byterate
	 * @param hotspots
	 * @param hotspotStarts if Null, the hotspots are created uniformly across the size of the media
	 * @param hotspotLengths
	 */
	public static void generateMedia(int count, Distribution lengths, Distribution byterate, Distribution hotspots, Distribution hotspotStarts, Distribution hotspotLengths) {
		for (int i = 0; i < count; ++i) {
			new Media ( lengths.nextInt(), byterate.nextInt(), hotspots.nextInt(), hotspotStarts, hotspotLengths);
		}
	}

	/**
	 * Returns information about a specific media
	 * @param ID
	 * @return
	 */
	public static final Media getMedia(int ID) {
		return medias.get(ID);
	}

	public final int getID() {
		return ID;
	}

	/**
	 * Returns the length of the media (in seconds)
	 * @return
	 */
	public final int getLength() {
		return length;
	}

	/**
	 * Returns the length of the media (in bytes)
	 * @return
	 */
	public final long getByteLength() {
		return getByteOffset( getLength() );
	}

	/**
	 * Returns the number of Media items availabe
	 * @return
	 */
	public static final int count() {
		return medias.size();
	}

	public final int getBitrate() {
		return byterate * 8;
	}

	public final int getByterate() {
		return byterate;
	}

	/**
	 * Returns a set of hotspots with their ranges in seconds
	 * @return
	 */
	public final List<Hotspot> getHotspots() {
		return hotspots;
	}

	/**
	 * Returns a specific Hotspot
	 * @param hotspot
	 * @return
	 */
	public final Hotspot getHotspot(String hotspot) {

		Iterator<Hotspot> i = hotspots.iterator();

		while (i.hasNext()) {
			Hotspot h = i.next();
			if (h.equals(hotspot))
				return h;
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Media(" + ID + " length:" + length + ")";
	}

	/**
	 * Returns how many bytes from the beginning of the video this second is
	 * @param seconds
	 */
	public final long getByteOffset( int seconds ) {
		assert seconds < getLength();

		// TODO in future if this was a VBR media we could have a non linear mapping
		return (long)byterate * (long)seconds;
	}

	/**
	 * Returns how many seconds from the beginning of the video this byte is
	 * @param start
	 * @return
	 */
	public int getSecondOffset(long bytes) {
		assert bytes < getByteLength();
		return (int) ( bytes / byterate );
	}
}
