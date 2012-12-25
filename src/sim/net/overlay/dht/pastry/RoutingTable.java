/*
 * Created on 21-Feb-2005
 */
package sim.net.overlay.dht.pastry;

import java.util.Iterator;

import sim.main.Global;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.stealth.StealthPeer;


/**
 * @author Andrew Brampton
 * Routing table
 * log2^b(N) rows with
 * (2^b) coloumns0
 *
 * But since N can't be greater than 2^64 then we can use
 * 16 Rows, with 16 coloumns
 *
 */
public class RoutingTable implements Iterable<NodeAddressPair> {

	/**
	 * Set this to true to make use of locality
	 */
	public final static boolean USELOCALITY = true;

	/**
	 * Pick entries in a routing table box completely randomly
	 */
	public final static boolean USERANDOM = false;

	protected final long nodeID;
	protected final int b;
	protected final int idBits;
	protected final int routingRows;
	protected final int routingCols;

	// This is worked out a lot, so here is a cache
	protected final long bmask;

	protected NodeAddressPairs[][] routingTable;

	protected Class<?> parentType;

	int size = 0;

	protected String longToHexString(long number) {
		String ret = "";

		// Now output the numbers one at a time
		int digits = (idBits / b);
		while (digits > 0) {
			// Print out the last b bits
			ret = Long.toHexString(number & bmask) + ret;
			// Move the number along b bits
			number >>= b;
			digits--;
		}

		return ret.toUpperCase();
	}

	/**
	 * Returns how many base.b digits are in common between the two numbers
	 * 0x123456 and 0x123567 would have 3 common digits
	 * @param number1
	 * @param number2
	 * @return The number of common digits from the left
	 */
	public int commonDigits(long number1, long number2) {
		long mask = 0x8000000000000000L >> (b - 1);

		int i = 0;
		while (i<(idBits / b) && ((number1 & mask) == (number2 & mask))) {
			i++;
			mask = (mask >>> b);
		}

		return i;
	}

	protected int valueAtCommon(long id, int common) {
		int shift = idBits - ((common + 1) * b);
		int i = (int)( (id >> shift) & bmask);

		return i;
	}

	protected static int hexToInt(char hex) {
		if (hex >= '0' && hex <= '9')
			return hex - '0';
		else if (hex >= 'A' && hex <= 'F')
			return (hex - 'A') + 10;
		else {
			return -1;
		}
	}

	/**
	 * Constructs a RoutingTable
	 * @param nodeID The ID of this node
	 * @param b The magic b value
	 * @param idBits The number of bits in a node ID
	 * @param parent The node that owns this routing table
	 */
	public RoutingTable(long nodeID, int b, int idBits, Class<?> parent) {

		this.nodeID = nodeID;
		this.b = b;
		this.idBits = idBits;
		this.parentType = parent;

		if (sim.net.overlay.dht.stealth.Peer.USE_FIRST_ROW_ONLY && parentType == StealthPeer.class) {
			this.routingRows = 1;
		} else {
			this.routingRows = (int)(Math.log(Math.pow(2,idBits)) / Math.log(Math.pow(2,b))); //log2^b( 2^64 )
		}

		this.routingCols = (int)Math.pow(2,b);

		routingTable = new NodeAddressPairs[routingRows][];
		for (int i = 0; i < routingTable.length; i++) {
			routingTable[i] = new NodeAddressPairs[routingCols];
			for (int ii=0; ii < routingTable[i].length; ii++ )
				routingTable[i][ii] = null;
		}

		// Work out the bmask
		long mask = 1L; int i = 1;
		while ( i < b ) {
			mask = (mask << 1) | 1L;
			i++;
		}

		bmask = mask;
	}

	protected class XY {
		public int col;
		public int row;

		public XY(int col, int row) {
			this.col = col;
			this.row = row;
		}

		@Override
		public String toString() {
			return "col = " + this.col + ", row = " + this.row;
		}
	}

	/**
	 * Gets the col (x) and row (y) that a ID should be at
	 * @param id The idea being looked at
	 * @param maxcommon The max common digits to use for this ID
	 * @return
	 */
	protected XY getRowCol(long id, int maxcommon) {
		int common = commonDigits(id, nodeID);

		// don't use anything but the first row for stealth peers
		if (sim.net.overlay.dht.stealth.Peer.USE_FIRST_ROW_ONLY && parentType == StealthPeer.class) {
			common = 0;
		}

		if (common > maxcommon)
			common = maxcommon;

		// Check if we are a match
		if (common >= idBits / b)
			return null;

		int col = valueAtCommon(id, common); // X
		int row = common;                    // Y

		return new XY(col, row);
	}

	protected XY getRowCol(long id) {
		return getRowCol(id, Integer.MAX_VALUE);
	}

	/**
	 * Adds a entry into the routing table
	 * @return True if the entry wasn't previously in the Set
	 * @param entry
	 */
	public boolean add(NodeAddressPair entry) {
		XY xy = getRowCol(entry.nodeID);

		if (xy == null)
			return false;

		if (routingTable[xy.row][xy.col] == null)
			routingTable[xy.row][xy.col] = new NodeAddressPairs();

		// Put a hard limit on the amount of entries per "cell"
		if ( routingTable[xy.row][xy.col].size() > 15 )
			return false;

		boolean changed = routingTable[xy.row][xy.col].add(entry);

		if (changed)
			size++;

		return changed;
	}

	public boolean addAll(NodeAddressPairs entries) {
		boolean changed = false;

		Iterator<NodeAddressPair> i = entries.iterator();
		while (i.hasNext()) {
			changed |= add(i.next());
		}

		return changed;
	}

	/**
	 * Remove a entry from the routing table
	 * @param entry
	 */
	public boolean remove(NodeAddressPair entry) {
		XY xy = getRowCol(entry.nodeID);

		if (xy == null)
			return false;

		NodeAddressPairs pairs = routingTable[xy.row][xy.col];
		if (pairs == null)
			return false;

		boolean changed = pairs.remove(entry);

		// Check if this NodeAddressPairs is now empty
		if (changed) {

			size--;

			if ( pairs.isEmpty())
				routingTable[xy.row][xy.col] = null;
		}

		return changed;
	}

	/**
	 * HACK, This flag indicates if the first spot in the routing table
	 * did not contain an entry
	 */
	public boolean badLookup = false;

	/**
	 * Returns the closest node in the routing table to route this packet to
	 * @param ID The ID we are routing to
	 * @return The network address of the closest node or
	 * 			 null if we are the closest match
	 */
	public NodeAddressPair getRoute(long ID) {

		// Find which row and col this is
		XY xy = getRowCol(ID);

		if (xy == null)
			return null;

		NodeAddressPair pair = null;
		NodeAddressPairs pairs = null;

		badLookup = false;

		while(true) {

			int firstCol = xy.col;

			// From the best match, scan left looking for a match
			do {
				//Trace.println(LogLevel.DEBUG, Helper.getShortName(parentType) + "Access(" + xy.row + "," + xy.col + ")");

				pairs = routingTable[xy.row][xy.col];

				if (pairs != null) {

					//Global.stats.logCount(Helper.getShortName(parentType) + "Access(" + xy.row + "," + xy.col + ")");
					//Global.stats.log(Helper.getShortName(parentType) + "Size(" + xy.row + "," + xy.col + ")", pairs.size());


					// HACK
					// We have a 1/10 chance of a node "not existing"
					//List<NodeAddressPair> tempRemoved = new ArrayList<NodeAddressPair>();

					while (!pairs.isEmpty()) {

						//First check for a prefect match, otherwise pick any
						pair = pairs.find(ID);

						//If there is no prefect match, find the best
						if (pair == null) {
							pair = findClosest(ID, pairs);
						}

						if (pair != null) {

							// We have a one in ten chance of deleting a Node
							// and we store it so we can later put it back!
							//if (Global.rand.nextDouble() < 0.9) {
							//	tempRemoved.add(pair);
							//	pairs.remove(pair);
							//	continue;
							//}

							//for (NodeAddressPair temp : tempRemoved)
							//	pairs.add(temp);

							//Check if we are the closest match
							//Global.stats.logAverage("RT_Jumps", (firstCol - xy.col) >= 0 ? (firstCol - xy.col) : (firstCol - xy.col + routingCols) );
							return pair;
						}
					}

					//for (NodeAddressPair temp : tempRemoved)
					//	pairs.add(temp);
				}

				badLookup = true;

				// We didn't find it in this slow, so move back a col
				xy.col--;
				if (xy.col < 0)
					xy.col += routingCols;

			} while (firstCol != xy.col);

			// Now start looking on the row above (if there is one)
			if (xy.row == 0)
				break;

			xy = getRowCol(ID, xy.row - 1);
		}

		Global.stats.logAverage("RT_Jumps", routingCols );

		return null;
	}
	/*
	public NodeAddressPair getRoute(long ID) {

		final XY xy = getRowCol(ID);

		if (xy == null)
			return null;

		NodeAddressPair pair = null;
		NodeAddressPairs pairs = null;

		// From the best match, scan left looking for a match
		while (xy.col >= 0 && pair == null) {
			Trace.println(LogLevel.DEBUG, Helper.getShortName(parentType) + "Access(" + xy.row + "," + xy.col + ")");

			pairs = routingTable[xy.row][xy.col];

			if (pairs != null) {

				//Global.stats.logCount(Helper.getShortName(parentType) + "Access(" + xy.row + "," + xy.col + ")");
				//Global.stats.log(Helper.getShortName(parentType) + "Size(" + xy.row + "," + xy.col + ")", pairs.size());

				//First check for a prefect match, otherwise pick any
				pair = pairs.find(ID);

				//If there is no prefect match, find the best
				if (pair == null) {
					pair = findClosest(ID, pairs);
				}

				if (pair != null) {
					//Check if we are the closest match
					return pair;
				}
			}

			//TODO Why go backwards?
			xy.col--;
		}

		return null;
	}
	*/

	public NodeAddressPair findClosest(long ID, NodeAddressPairs pairs) {
		if (USELOCALITY) {
			return pairs.findProxClosest();
		} else if (USERANDOM) {
			return pairs.random();
		} else {
			return pairs.findNumClosest(ID);
		}
	}

	/**
	 * Returns the number of rows in the routing table
	 * @return
	 */

	public int getRows() {
		return routingTable.length;
	}

	/**
	 * Gets a row of the routing table
	 * @param n
	 * @return
	 */
	public NodeAddressPairs[] getRow(int n) {
		return routingTable[n];
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String sID = longToHexString(nodeID);

		for (int x = 0; x < routingTable.length; x++) {
			for (int y = 0; y < routingTable[x].length; y++) {
				if (routingTable[x][y] != null) {
					sb.append( Peer.toString(routingTable[x][y].first().nodeID, true) );
				} else {

					int digit = hexToInt(sID.charAt(x));

					//Check if this is our coloumn, if so display our digit
					if (digit == y) {
						sb.append( "         " + Long.toHexString(digit) + "         " );
					} else {
						sb.append( "         X         " );
					}
				}

				sb.append( '|' );
			}
			sb.append( '\n' );
		}

		return sb.toString();
	}

	public int size() {
		return size;
	}

	public Iterator<NodeAddressPair> iterator() {
		return new RoutingTableIterator();
	}

	private class RoutingTableIterator implements Iterator<NodeAddressPair> {

		int x, y;
		Iterator<NodeAddressPair> z;
		NodeAddressPair next = null;

		public RoutingTableIterator() {
			x = 0;
			y = 0;
			z = null;
			next();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return (next != null);
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public NodeAddressPair next() {
			NodeAddressPair oldNext = next;

			next = null;

			//Find the next item
			while (next == null) {
				if (z != null && z.hasNext()) {
					next = z.next();
				} else {
					if (x >= routingTable.length)
						break;

					if (routingTable[x][y] != null) {
						z = routingTable[x][y].iterator();
					}

					y++;
					if (y >= routingTable[x].length) {
						y = 0;
						x++;
					}
				}
			}

			return oldNext;
		}

	}

	public NodeAddressPair find(long nodeID) {
		XY xy = getRowCol(nodeID);

		if (xy == null)
			return null;

		if (routingTable[xy.row][xy.col] == null)
			return null;

		return routingTable[xy.row][xy.col].find(nodeID);
	}

	public NodeAddressPairs[][] getTable() {
		return routingTable;
	}

	/**
	 * Test method
	 * @param args
	 */
	public static void main(String[] args) {
		RoutingTable r = new RoutingTable(0x1234, 4, 64, null);
		NodeAddressPair pair;
		int count = 0;

		for (int i = 1; i <= 100; i++) {
			pair = new NodeAddressPair(i, i);
			r.add(pair);
			count++;
		}
		System.out.println(count);

		count = 0;
		Iterator<NodeAddressPair> ii = r.iterator();
		while (ii.hasNext()) {
			pair = ii.next();
			System.out.println(pair);
			count++;
		}
		System.out.println(count);

		System.out.println ( r.getRowCol(0) );
		System.out.println ( r.getRowCol(0x0001) );
		System.out.println ( r.getRowCol(0x0002) );
		System.out.println ( r.getRowCol(0x0003) );
		System.out.println ( r.getRowCol(0x1234) );
		System.out.println ( r.getRowCol(0x2234) );
	}

	/**
	 *
	 */
	public void clear() {
		for (int i = 0; i < routingTable.length; i++) {
			for (int ii=0; ii < routingTable[i].length; ii++ ) {
				if (routingTable[i][ii] != null) {
					routingTable[i][ii].clear();
					routingTable[i][ii] = null;
				}
			}
		}
	}
}
