package sim.net.overlay.cdn.workload;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sim.net.overlay.cdn.workload.Action.ActionType;


/**
 * Loads a workload of actions from the Worldcup traces
 *
 * @author Andrew Brampton
 *
 */
public class WorkloadReader {

	public final static String START = "Start";

	/**
	 * The list of all actions
	 */
	List<Action> actions = new ArrayList<Action>();

	/**
	 * The action we are currently on
	 */
	int actionIndex = 0;

	/**
	 *  Maps the IDs in the logs, to client numbers
	 */
	Map<String, Integer> userMap = new TreeMap<String, Integer>();

	/**
	 * Maps the Object names in the logs, to numbers
	 */
	Map<String, Integer> objectMap = new TreeMap<String, Integer>();

	/**
	 * Works out how long (in seconds) the object is based on the highest request
	 */
	List<Integer> objectLength = new ArrayList<Integer>();


	/**
	 * Works out where the bookmarks are based on their first requests
	 */
	List< Map<String, Integer> > objectBookmarks = new ArrayList<Map<String, Integer>>();

	/**
	 * Works out the order bookmarks are viewed in
	 */
	List< Map<String, Integer > > objectBookmarkSequence = new ArrayList<Map<String, Integer>>();

	/**
	 * How popular is this bookmark
	 */
	List< Map<String, Integer > > objectBookmarkPopularity = new ArrayList<Map<String, Integer>>();

	/**
	 * The last bookmark each user viewed
	 */
	List< String > userLastBookmark = new ArrayList< String >();

	/**
	 * Adds this string to the map, and returns its "index"
	 * @param m
	 * @param s
	 * @return
	 */
	static private int addToMap(Map<String, Integer> m, String s) {
		if (!m.containsKey(s))
			m.put(s, m.size());

		return m.get(s);
	}

	/**
	 * Adds a value to a list only if value is greater than what already exists
	 */
	static private void addToListIfGreater(List<Integer> l, int index, int value) {

		while (l.size() <= index)
			l.add(null);

		if (l.get(index) == null || l.get(index) < value)
			l.set(index, value);
	}

	static private String getFromList(List< String > l, int index) {
		while (l.size() <= index)
			l.add(null);

		return l.get(index);
	}

	static private Map<String, Integer> getFromListOrCreate(List< Map<String, Integer> > l, int index) {
		while (l.size() <= index)
			l.add(null);

		// Get the innermap, and if it doesn't exist create one
		Map<String, Integer> innerMap = l.get(index);

		if (l.get(index) == null) {
			innerMap = new TreeMap<String, Integer>();
			l.set(index, innerMap);
		}

		return innerMap;
	}

	static private void addToListIfNotExists(List< Map<String, Integer> > l, int index, String s, int value) {

		Map<String, Integer> innerMap = getFromListOrCreate (l, index);

		// Now check if s2 exists, if it already does then do nothing
		if (!innerMap.containsKey(s))
			innerMap.put(s, value);
	}

	static private void replaceInListIfSmaller(List< Map<String, Integer> > l, int index, String s, int value) {
		Map<String, Integer> innerMap = getFromListOrCreate (l, index);

		Integer oldVal = innerMap.get(s);
		if ( oldVal == null || oldVal > value)
			innerMap.put(s, value);
	}

	static private void incrementInMap(Map<String, Integer> m, String s) {
		Integer i = m.get( s );
		if (i == null)
			i = 1;
		else
			i++;

		m.put( s , i);
	}

	/**
	 * Reads the next (non blank/commented line)
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String nextLine(final BufferedReader in) throws IOException {
		String line;

		while ((line = in.readLine()) != null) {
			line = line.trim();

			if ( line.isEmpty() )
				continue;

			// Skip lines starting with #
			if (line.charAt(0) == '#')
				continue;

			return line;
		}

		return null;
	}

	public WorkloadReader ( Reader r ) throws IOException {

		String line;
		final BufferedReader in = new BufferedReader ( r );

		// Read # of clients
		final int clients = Integer.parseInt( nextLine(in) );

		// Read # of objects
		final int objects = Integer.parseInt( nextLine(in) );

		// Read each object
		for ( int i = 0; i < objects; i++ ) {
			line = nextLine(in);
			String s[] = line.split("\t", 2);

			int object = addToMap(objectMap, s[0]);
			addToListIfGreater(objectLength, object, Integer.parseInt(s[1]));
		}


		while ((line = nextLine(in)) != null) {

			String[] s = line.split("\t", 6);

			if (s.length >= 4 ) {

				Action a = new Action();

				a.realTime = (long) (Double.parseDouble( s[0] ) * 1000);
				a.user = addToMap(userMap, s[1]);

				if ( !objectMap.containsKey(s[2]) )
					throw new RuntimeException("Unknown object " + s[2]);
				a.object = objectMap.get(s[2]);

				String action = s[3];

				if (action.equals( "seek" )) {

					if (s.length < 5)
						throw new RuntimeException ( "Invalid action '" + action + "' no mediaTime");

					a.type = ActionType.seek;

					// If this happened, convert a into a SeekAction
					SeekAction sa = new SeekAction(a);
					a = sa;

					sa.mediaTime = Integer.parseInt( s[4] );

					// Check if this is beyond the current media length, if so increase it
					// HACK +1000 because this is the start of the seek, we need the end
					//      later we should change this to figure out how far people actually watch
					//addToListIfGreater(objectLength, a.object, (sa.mediaTime + 1000));

					if (s.length > 5 && SeekAction.STORE_WHY) {
						sa.why = s[5];

						// If this is a shortcut, make note
						if ( sa.why.startsWith("shortcut ") ) {
							String bookmark = sa.why.substring(9);

							Map<String, Integer> popularity = getFromListOrCreate(objectBookmarkPopularity, a.object);
							incrementInMap(popularity, bookmark);

							String oldBookmark = getFromList(userLastBookmark, a.user);

							// If we haven't visited a bookmark previously, then we assume we are at the start state
							if (oldBookmark == null) {
								oldBookmark = START;
								addToListIfNotExists(objectBookmarks, a.object, START, 0 );
							}

							String key = oldBookmark + "\t" + bookmark;

							// Add the time to the list of bookmarks so we know where it starts
							//replaceInList(objectBookmarks, a.object, bookmark, sa.mediaTime );
							// Always store the earliest bookmark time
							replaceInListIfSmaller(objectBookmarks, a.object, bookmark, sa.mediaTime );

							// Increment the number of times we have "travelled" from one bookmark to another
							Map<String, Integer> sequences = getFromListOrCreate(objectBookmarkSequence, a.object);
							incrementInMap ( sequences, key );

							userLastBookmark.set(a.user, bookmark);

						} else if ( sa.why.equals("restart") ) {

							Map<String, Integer> popularity = getFromListOrCreate(objectBookmarkPopularity, a.object);
							incrementInMap(popularity, "Start");
						}
					}

				} else if (action.equals( "pause" )) {
					a.type = ActionType.pause;
				} else if (action.equals( "stop" )) {
					a.type = ActionType.stop;
				} else {
					throw new RuntimeException("Invalid action '" + action + "'");
				}

				actions.add( a );

			} else {
				throw new RuntimeException("Invalid line '" + line + "'");
			}
		}

		if ( clients != userCount() )
			throw new RuntimeException("Clients defined (" + clients + ") does not match clients read (" + userCount() + ")");

		if ( objects != objectCount() )
			throw new RuntimeException("Objects defined (" + objects + ") does not match objects read (" + objectCount() + ")");
	}

	public int getNumberOfActions() {
		return actions.size();
	}

	public long getEstimatedRunTime() {
		return actions.get( actions.size() - 1 ).realTime - actions.get( 0 ).realTime;
	}

	public Action getNextAction() {
		if ( actionIndex >= actions.size() )
			return null;

		return actions.get( actionIndex++ );
	}

	/**
	 * Returns the number of actions left
	 * @return
	 */
	public int actionsLeft() {
		return actions.size() - actionIndex;
	}

	/**
	 * Returns which action we are currently on
	 * @return
	 */
	public int actionsIn() {
		return actionIndex;
	}

	/**
	 * Returns a specific action
	 * @param index
	 * @return
	 */
	public Action getAction( int index ) {
		return actions.get(index);
	}

	public int userCount() {
		return userMap.size();
	}

	public int objectCount() {
		return objectMap.size();
	}

	/**
	 * Loads a workload and parse it
	 * @param actions The file containing all the actions to load
	 * @param metadatadir A directory containing some files describe metadata about certain aspects of the media
	 * @throws IOException
	 */
	public WorkloadReader ( String actions, String metadatadir ) throws IOException {
		this( new FileReader(actions) );
	}

	public static void main(String[] args) throws IOException {
		WorkloadReader r = new WorkloadReader ( "eurovision.actions", "C:\\Projects\\P2PSim\\trunk" );

		Action a;
		while ((a = r.getNextAction()) != null) {
			System.out.println(a);
		}

		// Print out the "guessed" stuff
		System.out.println( r.objectLength );
		System.out.println( r.objectBookmarks );
		System.out.println( r.objectBookmarkSequence );
		System.out.println( r.objectBookmarkPopularity );
	}

}
