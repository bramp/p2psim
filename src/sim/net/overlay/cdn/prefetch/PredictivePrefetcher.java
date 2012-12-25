package sim.net.overlay.cdn.prefetch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import sim.net.overlay.cdn.Hotspot;
import sim.net.overlay.cdn.Media;
import sim.net.overlay.cdn.Request;
import sim.net.overlay.cdn.workload.WorkloadReader;

/**
 * Request the bookmarks based on their popularity
 * @author Andrew Brampton
 *
 */
public class PredictivePrefetcher extends Prefetcher {

	/**
	 * How many levels ahead should we prefetch?
	 */
	final int depth;

	public PredictivePrefetcher( int depth ) {
		assert depth >= 1;
		this.depth = depth;
	}
	
	public PredictivePrefetcher() {
		this ( 1 );
	}

	/**
	 * Caches some predictive data stuff
	 */
	static Map< String, List<Entry<String, Double>> > probCache = new TreeMap< String, List<Entry<String, Double>> >();

	/**
	 * Sort the <String, Double> based on the Doubles
	 */
	Comparator<Entry<String, Double>> listCompartor = new Comparator<Entry<String, Double>>() {

		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
			if ( o2.getValue() == o1.getValue() )
				return 0;

			return o2.getValue() > o1.getValue() ? 1 : -1;
		}
	};

	/**
	 * This updates the prefetch list based on moving to a new bookmark
	 * @param newBookmark
	 */
	private void updatePredictivePrefetchList(String newBookmark) {

		List<Entry<String, Double>> list = probCache.get( media.toString() + newBookmark );

		if ( list == null) {
			list = new ArrayList<Entry<String, Double>>();

			// Tranverse the probability tree working out the probalities at a certain depth
			getPredictivePrefetchList(media, newBookmark, depth, list, 1);

			probCache.put( media.toString() + newBookmark, list);
		}

		// Now combine the probabilities in list
		Map<String, Double> temp = new TreeMap<String, Double>();

		for ( Entry<String, Double> e : list ) {
			// If the value already contains sum it
			if ( temp.containsKey( e.getKey() ) ) {
				temp.put ( e.getKey(), temp.get(e.getKey()) + e.getValue() );

			// otherwise insert a fresh
			} else {
				temp.put ( e.getKey(), e.getValue() );
			}
		}

		List<Entry<String, Double>> sequences = new ArrayList<Entry<String, Double>>( temp.entrySet() );

		// TODO move this into hotspot, so we don't have to do it many times
		// Now sort these bookmarks based on their "popularity"
		Collections.sort(sequences, listCompartor);

		clearPrefetchRange();
		for (Entry<String, Double> e : sequences) {
			addPrefetchRange(media.getHotspot( e.getKey() ) );
		}
	}

	/**
	 * Returns a list of bookmark names with probability of going there
	 * @param m
	 * @param newBookmark
	 * @return
	 */
	private List<Entry<String, Double>> getPredictivePrefetchList(final Media m, String newBookmark) {

		// Get the hotspot we just clicked on
		Hotspot h = m.getHotspot( newBookmark );

		if (h == null) {
			System.out.println( m.getHotspots() );
			throw new RuntimeException("Can't find hotspot (" + newBookmark + ")!");
		}

		if (h.sequence == null) {
			throw new RuntimeException("Can't do prediction without predicition data!");
		}

		List<Entry<String, Double>> sequences = new ArrayList<Entry<String, Double>>( h.sequence.size() );

		int total = 0;

		for ( Entry<String, Integer> e : h.sequence.entrySet() ) {
			sequences.add( new SimpleEntry<String, Double> ( e.getKey(), (double)e.getValue() ) );
			total += e.getValue();
		}

		// Now normalise
		for ( Entry<String, Double> e : sequences ) {
			e.setValue( e.getValue() / (double)total);
		}

		//Collections.sort(sequences, listCompartor);

		return sequences;
	}

	/**
	 * Works out a probability tree
	 * @param m
	 * @param newBookmark
	 * @param depth
	 * @param list
	 * @param value
	 */
	private void getPredictivePrefetchList(final Media m, String newBookmark, int depth, List<Entry<String, Double>> list, double value) {
		depth--;

		List<Entry<String, Double>> next = getPredictivePrefetchList(m, newBookmark);

		// Are we at the bottom?
		if (depth == 0) {
			for ( Entry<String, Double> e : next ) {
				e.setValue( e.getValue() * value );
				list.add( e );
			}
		} else {
			for ( Entry<String, Double> e : next ) {
				getPredictivePrefetchList(m, e.getKey(), depth, list, e.getValue() * value );
			}
		}
	}

	@Override
	public void newRequest( final Request r, final String why ) {

		boolean mediaChanged = r != null && setMedia ( r.getMedia() );

		String bookmark = null;

		// Figure out which bookmark we are currently on
		if ( why != null && why.startsWith("shortcut ") )
			bookmark = why.substring(9);
		else if ( mediaChanged )
			bookmark = WorkloadReader.START;

		if ( bookmark != null) {
			// Create the prefetch list in the order of probability of occuring
			updatePredictivePrefetchList(bookmark);
		}
	}
}
