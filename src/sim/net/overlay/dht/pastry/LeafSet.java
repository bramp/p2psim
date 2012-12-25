/*
 * Created on 15-Feb-2005
 */
package sim.net.overlay.dht.pastry;

import java.util.Comparator;
import java.util.Iterator;

import sim.collections.NumberSet;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;


/*
 * TODO Speed this up
 */

public class LeafSet implements Iterable<NodeAddressPair> {

	final long centerID;
	final int LestSet;

	final int ll; // The size of the leftset
	//final int lr; // The size of the rightset

	final NumberSet<NodeAddressPair> leftset;
	final NumberSet<NodeAddressPair> rightset;

	final NodeAddressPairs fullset;

	final Comparator<NodeAddressPair> compareL;
	final Comparator<NodeAddressPair> compareR;

	public LeafSet(final long centerID, int l) {
		this.centerID = centerID;
		this.LestSet = l;

		if (l == 1)
			throw new RuntimeException("I don't work too good when l=1");

		this.ll = (l % 2 == 0) ? l/2 : l/2 + 1;
		//this.lr = l/2;

		// Two comparator objects that sort in a special way
		compareL = new Comparator<NodeAddressPair>() {
			public int compare(NodeAddressPair o1, NodeAddressPair o2) {
				long o1Diff = o1.longValue() - centerID;
				long o2Diff = o2.longValue() - centerID;
				long o1DiffA = (o1Diff >> 32) & 0xFFFFFFFFL;
				long o2DiffA = (o2Diff >> 32) & 0xFFFFFFFFL;

				if (o1DiffA > o2DiffA) {
					return -1;
				} else if (o1DiffA < o2DiffA) {
					return 1;
				} else {
					long o1DiffB = o1Diff & 0xFFFFFFFFL;
					long o2DiffB = o2Diff & 0xFFFFFFFFL;

					if (o1DiffB > o2DiffB) {
						return -1;
					} else if (o1DiffB < o2DiffB) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		};

		compareR = new Comparator<NodeAddressPair>() {
			public int compare(NodeAddressPair o1, NodeAddressPair o2) {
				long o1Diff = centerID - o1.longValue();
				long o2Diff = centerID - o2.longValue();
				long o1DiffA = (o1Diff >> 32) & 0xFFFFFFFFL;
				long o2DiffA = (o2Diff >> 32) & 0xFFFFFFFFL;

				if (o1DiffA > o2DiffA) {
					return -1;
				} else if (o1DiffA < o2DiffA) {
					return 1;
				} else {
					long o1DiffB = o1Diff & 0xFFFFFFFFL;
					long o2DiffB = o2Diff & 0xFFFFFFFFL;

					if (o1DiffB > o2DiffB) {
						return -1;
					} else if (o1DiffB < o2DiffB) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		};

		leftset = new NumberSet<NodeAddressPair>(compareL);
		rightset = new NumberSet<NodeAddressPair>(compareR);
		fullset = new NodeAddressPairs();
	}

	protected boolean change() {
		NodeAddressPair pair = null;
		boolean changed = false;

		// 2) If leftset is bigger than rightset, move something
		if (leftset.size() > rightset.size()) {
			pair = leftset.last();
			leftset.remove(pair);
			changed |= rightset.add(pair);
		}

		// 3) If rightset is bigger than leftset, move something
		if (rightset.size() > leftset.size()) {
			pair = rightset.last();
			rightset.remove(pair);
			changed |= leftset.add(pair);
		}

		return changed;
	}

	/**
	 * Adds this entry to the LeafSet
	 * @param entry
	 * @return True if the entry wasn't previously in the Set
	 */
	public boolean add(NodeAddressPair entry) {

		if (entry.longValue() == centerID)
			return false;

		if (fullset.contains(entry))
			return false;

		/*// This check actually slows things down :(
		// Check if this node will actually fit in
		if (fullset.size() >= l) {
			//System.out.println(this);
			//System.out.println("Entry: " + entry + " left " + leftset.last() + " right " + rightset.last());
			//System.out.println("compareL: " + compareL.compare(entry, leftset.last()) + " compareR: " + compareR.compare(entry, rightset.last()));

			// If entry is nearer on the left
			if (compareL.compare(entry, leftset.last()) == -1) {
				// All is good
		    // If entry is nearer on the right
			} else if (compareR.compare(entry, rightset.last()) == -1) {
				// All is good
			} else {
				return false;
			}
		}
		*/

		fullset.add(entry);

		boolean changed = false;

		// 1) Add to leftset
		changed = changed | leftset.add(entry);
		changed |= change();

		NodeAddressPair pair = null;

		// 4) Make sure the left and right aren't too big
		if (changed) {
			// If we are too big, chop one
			if (leftset.size() > ll) {
				pair = leftset.last();
				leftset.remove(pair);
				fullset.remove(pair);
			}
		}

		// If we just removed what was added
		// We made no change to the leafSet
		if (pair == entry)
			changed = false;

		return changed;
	}

	/**
	 * Adds the entries to the leafSet and returns the list
	 * of entries that got added (if any)
	 * @param entries
	 * @return
	 */
	public NodeAddressPairs addAll(NodeAddressPairs entries) {
		NodeAddressPairs newentries = new NodeAddressPairs();

		// Add add all entries normally
		Iterator<NodeAddressPair> i = entries.iterator();
		while (i.hasNext()) {
			NodeAddressPair p = i.next();

			// If we change, then add this pair
			if ( add(p) ) {
				newentries.add(p);
			}
		}

		// Now find which new ones actually got added
		i = newentries.iterator();
		while  (i.hasNext()) {
			NodeAddressPair p = i.next();
			if (!fullset.contains(p))
				i.remove();
		}

		// If some got added return the new entries list
		return newentries;
	}

	public NodeAddressPair first() {
		return leftset.last();
	}

	public NodeAddressPair last() {
		return rightset.last();
	}

	public boolean inRange(long ID) {

		NodeAddressPair p = new NodeAddressPair(ID);

		// Check if its within the lefthand side
		if (!leftset.isEmpty())
			if (compareL.compare(first(), p) >= 0)
				return true;

		// Check if its within the righthand side
		if (!rightset.isEmpty())
			if ( compareL.compare(last(), p) <= 0)
				return true;

		return false;
	}

	/**
	 * Returns the closest node in the routing table to route this packet to
	 * If we are the closest node null is returned
	 * @param ID
	 * @return The network address of the closest node or null if we are the closest match
	 */
	public NodeAddressPair getRoute(long ID) {
		NodeAddressPair closest = fullset.findNumClosest(ID);

		if (closest == null)
			return null;

		// Now check if we are closer
		long myDiff = Math.abs(this.centerID - ID);
		long leafDiff = Math.abs(closest.nodeID - ID);

		if (leafDiff < myDiff) {
			return closest;
		} else if (leafDiff == myDiff) { // Special case, return lowest ID
			if (closest.nodeID < ID) {
				return closest;
			}
		}

		return null;
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		String ret = new String();
		Iterator<NodeAddressPair> i;

		i = leftset.iterator();
		while( i.hasNext() ) {
			long val = i.next().longValue();
			ret = Peer.toString(val, true) + " | " + ret;
		}

		ret += "(" + Peer.toString(centerID, true) + ") | ";

		i = rightset.iterator();
		while( i.hasNext() ) {
			long val = i.next().longValue();
			ret += Peer.toString(val, true) + " | ";
		}

		return ret;
	}

	public boolean contains(NodeAddressPair entry) {
		return fullset.contains(entry);
	}

	public NodeAddressPairs getSet() {
		return fullset;
	}

	public Iterator<NodeAddressPair> iterator() {
		return fullset.iterator();
	}

	public boolean remove(NodeAddressPair entry) {

		boolean changed = leftset.remove(entry) | rightset.remove(entry) | fullset.remove(entry);

		if (changed)
			changed |= change();

		return changed;
	}

	public NodeAddressPair find(long nodeID) {
		return fullset.find(nodeID);
	}

	/**
	 * @return
	 */
	public int size() {
		return fullset.size();
	}


	/**
	 * Used to find the closest NodeAddressPair to the owner of the leafset (NOT including the owner)
	 * (Useful for replication)
	 * @param k the number of close NodeAddressPair to return
	 * @return the k closest NodeAddressPair
	 */
	public NodeAddressPairs getClosestPairs2(int k) {
		NodeAddressPairs result = new NodeAddressPairs();
		Iterator<NodeAddressPair> left = leftset.iterator();
		Iterator<NodeAddressPair> right = rightset.iterator();

		for(int i=0; i<k; i++) {
			// remove from left and right sets alternately
			Iterator<NodeAddressPair> ii = (i & 1) == 0 ? left : right;
			// check that there are enough nodes to add
			if (ii.hasNext()) {result.add(ii.next());}
			// if not, return what we have
			else {return result;}
		}

		return result;
	}

	/**
	 * Used to find the closest NodeAddressPair to the owner of the leafset (including the owner)
	 * (Useful for replication)
	 * @param k the number of close NodeAddressPair to return
	 * @return the k closest NodeAddressPair
	 */
	public NodeAddressPairs getClosestPairs(int k) {
		k--;
		NodeAddressPairs result = getClosestPairs2(k);

		// Add ourselfs first
		result.add(new NodeAddressPair(centerID));

		return result;
	}

	public NodeAddressPairs getClosestPairs(long hash, int k) {

		// Special case of k==1
		if (k == 1) {
			// Add all the peers
			NodeAddressPairs p = new NodeAddressPairs();
			p.add(new NodeAddressPair(centerID)); // Add myself
			p.addAll(getSet());

			// Now find the closest, and create a new NodeAddressPairs to hold one item
			NodeAddressPair closest = p.findNumClosest(hash);
			p = new NodeAddressPairs();
			p.add(closest);

			return p;
		}

		LeafSet leafset = new LeafSet(hash, k);
		leafset.addAll(getSet()); // Add my leafset
		leafset.add(new NodeAddressPair(centerID)); // Add myself
		return leafset.getClosestPairs2(k);
	}


	/**
	 * Test method
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		LeafSet l = new LeafSet(0, 16);

		for (int i = 0; i <= 100; i++) {
			System.out.println(l);
			l.add(new NodeAddressPair(i));
		}
		*/

		LeafSet l = new LeafSet(0xC988057B8C355BB8L, 16);

		/*
		l.add(new NodeAddressPair(0xC574C40AF2BC3C2DL));
		l.add(new NodeAddressPair(0x45746E349864B469L));
		l.add(new NodeAddressPair(0x45ACA69E2C751BB8L));

		l.add(new NodeAddressPair(0xD13A61A003474BDCL));
		l.add(new NodeAddressPair(0xD15DCC46F84F5C2AL));
		l.add(new NodeAddressPair(0xD1D76FF4BED5DBD2L));

		*/

		l.add(new NodeAddressPair(0x805B93B0A398202DL));
		l.add(new NodeAddressPair(0x815F08B6005EB55FL));
		l.add(new NodeAddressPair(0x81B732087DCB45C4L));
		l.add(new NodeAddressPair(0x82C0DF150A85B16EL));
		l.add(new NodeAddressPair(0x85597631CD94E5E2L));
		l.add(new NodeAddressPair(0x86B8B14BA132125FL));
		l.add(new NodeAddressPair(0x86D5C620B9CC619DL));
		l.add(new NodeAddressPair(0x878F75FB05A9B889L));
		l.add(new NodeAddressPair(0x87B0F1B0D17C536AL));
		l.add(new NodeAddressPair(0x8806B87BB99EEC27L));
		l.add(new NodeAddressPair(0x88DE15E4F0ECA3B9L));
		l.add(new NodeAddressPair(0x89B98C903B1D23BFL));
		l.add(new NodeAddressPair(0x8A4C79449D03E7F1L));
		l.add(new NodeAddressPair(0x8BC528A076ECD5D7L));
		l.add(new NodeAddressPair(0x8BF72308025C8F5FL));
		l.add(new NodeAddressPair(0x91992C3387500B6EL));
		l.add(new NodeAddressPair(0x936DB1950A6998CFL));
		l.add(new NodeAddressPair(0x95262A4B2BA39E1DL));
		l.add(new NodeAddressPair(0x959B83CCDFCA4F95L));
		l.add(new NodeAddressPair(0x959FC024FF8D877FL));
		l.add(new NodeAddressPair(0x98F8BA3DC812A76DL));
		l.add(new NodeAddressPair(0x9A88DB53AD38536BL));
		l.add(new NodeAddressPair(0x9AC1C2EBD576E254L));
		l.add(new NodeAddressPair(0x9CB6933746AD89F4L));
		l.add(new NodeAddressPair(0x9CD21D830B817840L));
		l.add(new NodeAddressPair(0x9D175AB169CD3BCFL));
		l.add(new NodeAddressPair(0xA1A566F569D0F53CL));
		l.add(new NodeAddressPair(0xA1D8F99EB61C6359L));
		l.add(new NodeAddressPair(0xA32DC9F64F1DF03AL));
		l.add(new NodeAddressPair(0xA58FB2AA5203A836L));
		l.add(new NodeAddressPair(0xA70CE18896339064L));
		l.add(new NodeAddressPair(0xABDA3126B06490B6L));
		l.add(new NodeAddressPair(0xABF85EA458DF8F3CL));
		l.add(new NodeAddressPair(0xAD6715144982C968L));
		l.add(new NodeAddressPair(0xB06373AEAD4E1949L));
		l.add(new NodeAddressPair(0xB0839F8DF9E7FC15L));
		l.add(new NodeAddressPair(0xB0FD6503DC1C125BL));
		l.add(new NodeAddressPair(0xB169AEE05D7854FAL));
		l.add(new NodeAddressPair(0xB1F5C32C22493AFCL));
		l.add(new NodeAddressPair(0xB3C229F740E63718L));
		l.add(new NodeAddressPair(0xB4375BDC374915C4L));
		l.add(new NodeAddressPair(0xB62D6969B5F4BBCBL));
		l.add(new NodeAddressPair(0xB70C45C110743C2BL));
		l.add(new NodeAddressPair(0xB8B5A7D19CC26D9EL));
		l.add(new NodeAddressPair(0xB8BBDDE18C31E83FL));
		l.add(new NodeAddressPair(0xBB20B45FD4D95138L));
		l.add(new NodeAddressPair(0xBDBE135233AC5AFEL));
		l.add(new NodeAddressPair(0xBE3EFDF574DF44ABL));
		l.add(new NodeAddressPair(0xBECF71CBBD727291L));
		l.add(new NodeAddressPair(0xBF09AD1BFB1F38D6L));
		l.add(new NodeAddressPair(0xC0170BAE05E4F2FDL));
		l.add(new NodeAddressPair(0xC22B25EA2180C33AL));
		l.add(new NodeAddressPair(0xC574C40AF2BC3C2DL));
		l.add(new NodeAddressPair(0xC93A13E4EE1094AAL));
		l.add(new NodeAddressPair(0xC988057B8C355BB8L));
		l.add(new NodeAddressPair(0xCFA337658B7BE602L));
		l.add(new NodeAddressPair(0xD13A61A003474BDCL));
		l.add(new NodeAddressPair(0xD15DCC46F84F5C2AL));
		l.add(new NodeAddressPair(0xD1D76FF4BED5DBD2L));
		l.add(new NodeAddressPair(0xD45D7162C1D07BA9L));
		l.add(new NodeAddressPair(0xD4603C0631C4083EL));
		l.add(new NodeAddressPair(0xD663710B8A4D4428L));
		l.add(new NodeAddressPair(0xD66AF990B2E25C89L));
		l.add(new NodeAddressPair(0xD70B9EF158E2526CL));
		l.add(new NodeAddressPair(0xD9AE03FCD5E3BC4AL));
		l.add(new NodeAddressPair(0xDA29F5BD6124E9CFL));
		l.add(new NodeAddressPair(0xDA52E6B278B36BEBL));
		l.add(new NodeAddressPair(0xDAC011602C4FD599L));
		l.add(new NodeAddressPair(0xDFCFA5CFC52E1336L));
		l.add(new NodeAddressPair(0xE014266B0BAAB10CL));
		l.add(new NodeAddressPair(0xE1121B0805E8B107L));
		l.add(new NodeAddressPair(0xE213217D7A936A02L));
		l.add(new NodeAddressPair(0xE48FA6ECC1333FFDL));
		l.add(new NodeAddressPair(0xE4F9B4F5D694A55EL));
		l.add(new NodeAddressPair(0xE89C9468260B066BL));
		l.add(new NodeAddressPair(0xE8EA13C5611511AFL));
		l.add(new NodeAddressPair(0xE955FD8255D138C9L));
		l.add(new NodeAddressPair(0xEAAD07EA78187579L));
		l.add(new NodeAddressPair(0xEB37685A4FFCF182L));
		l.add(new NodeAddressPair(0xEB9D836A36476587L));
		l.add(new NodeAddressPair(0xEEDBEA3DA89EB5FCL));
		l.add(new NodeAddressPair(0xEFE55AD31F93FDCDL));
		l.add(new NodeAddressPair(0xF093B6E2A7723712L));
		l.add(new NodeAddressPair(0xF0CBC4D341B04049L));
		l.add(new NodeAddressPair(0xF1DA7F5B9EC894ABL));
		l.add(new NodeAddressPair(0xF2015BE6C49D0A56L));
		l.add(new NodeAddressPair(0xF30947DD0FCA26BBL));
		l.add(new NodeAddressPair(0xF64A260E30CC43CDL));
		l.add(new NodeAddressPair(0xF7093DA326A3416AL));
		l.add(new NodeAddressPair(0xF8B74DA6BE94DF5AL));
		l.add(new NodeAddressPair(0xFA83D9074E8F1BA9L));
		l.add(new NodeAddressPair(0xFD36C823598F593BL));
		l.add(new NodeAddressPair(0xFDA00200B030232EL));
		l.add(new NodeAddressPair(0xFDEFF999916F420DL));
		l.add(new NodeAddressPair(0x005DFE9859E8F0F4L));
		l.add(new NodeAddressPair(0x01007A0F980D2AC0L));
		l.add(new NodeAddressPair(0x0279F6486A2EB974L));
		l.add(new NodeAddressPair(0x02B225712EA7D179L));
		l.add(new NodeAddressPair(0x02FBCFB9F4F3A715L));
		l.add(new NodeAddressPair(0x0403D1808E3D3838L));
		l.add(new NodeAddressPair(0x045E93AD2B54DEDBL));
		l.add(new NodeAddressPair(0x07A38B43C21EC35CL));
		l.add(new NodeAddressPair(0x08DBF616BFB4A567L));
		l.add(new NodeAddressPair(0x0929B72D39EB9F4CL));
		l.add(new NodeAddressPair(0x0A1C7555B0848C94L));
		l.add(new NodeAddressPair(0x0A7D55A9F97C6591L));
		l.add(new NodeAddressPair(0x0BEBBA43A8D2F0A6L));
		l.add(new NodeAddressPair(0x0CB9A107C783F1C2L));
		l.add(new NodeAddressPair(0x0D5569B730568CF6L));
		l.add(new NodeAddressPair(0x0EE198B21DBE779FL));
		l.add(new NodeAddressPair(0x10524F51C9730F19L));
		l.add(new NodeAddressPair(0x11BA240DF6FC1BE4L));
		l.add(new NodeAddressPair(0x124DB6A1B083556EL));
		l.add(new NodeAddressPair(0x1255ED2300CD48F4L));
		l.add(new NodeAddressPair(0x12883851F08DFF3EL));
		l.add(new NodeAddressPair(0x15B2D76CB32DAD79L));
		l.add(new NodeAddressPair(0x160896C2D158DA1FL));
		l.add(new NodeAddressPair(0x1754544C576C15A3L));
		l.add(new NodeAddressPair(0x175561933A7572D5L));
		l.add(new NodeAddressPair(0x17B93C980A0C2D86L));
		l.add(new NodeAddressPair(0x1ABFE6CB40BCF2E7L));
		l.add(new NodeAddressPair(0x1BEC41C62442D1DAL));
		l.add(new NodeAddressPair(0x1D275F544DAF228CL));
		l.add(new NodeAddressPair(0x1F5C3F1FED65E192L));
		l.add(new NodeAddressPair(0x2249E1908BCF01F3L));
		l.add(new NodeAddressPair(0x23609C32689EAFD6L));
		l.add(new NodeAddressPair(0x25398A999F74762BL));
		l.add(new NodeAddressPair(0x2587AF97B4865538L));
		l.add(new NodeAddressPair(0x2610CAA1A4C1C7A5L));
		l.add(new NodeAddressPair(0x274A710A7B973DA7L));
		l.add(new NodeAddressPair(0x2ACC8DD83529771AL));
		l.add(new NodeAddressPair(0x2B203BF54F76AB38L));
		l.add(new NodeAddressPair(0x2D40BC66F747C5C8L));
		l.add(new NodeAddressPair(0x301E6DDB2E1ACE54L));
		l.add(new NodeAddressPair(0x304C901F73A79FB5L));
		l.add(new NodeAddressPair(0x31F5DBE63CEC0271L));
		l.add(new NodeAddressPair(0x34C728AA05847924L));
		l.add(new NodeAddressPair(0x35B53AF39E7C8A16L));
		l.add(new NodeAddressPair(0x361385B10C542CFAL));
		l.add(new NodeAddressPair(0x36D576BE9EF3448CL));
		l.add(new NodeAddressPair(0x37B2B1CB341244C2L));
		l.add(new NodeAddressPair(0x37F7328731AA60A1L));
		l.add(new NodeAddressPair(0x38B27F4E79445AF3L));
		l.add(new NodeAddressPair(0x39EC5033EC2A7F99L));
		l.add(new NodeAddressPair(0x3C11EDE9D280795EL));
		l.add(new NodeAddressPair(0x3C2E50DD91E533E4L));
		l.add(new NodeAddressPair(0x3DC43BAACCA41113L));
		l.add(new NodeAddressPair(0x3EB5D7725F2DA7E7L));
		l.add(new NodeAddressPair(0x4023239E31EF3380L));
		l.add(new NodeAddressPair(0x4291A4A91618538AL));
		l.add(new NodeAddressPair(0x42D38EE890168D58L));
		l.add(new NodeAddressPair(0x44F70E6F8F07ECA7L));
		l.add(new NodeAddressPair(0x45746E349864B469L));
		l.add(new NodeAddressPair(0x45ACA69E2C751BB8L));
		l.add(new NodeAddressPair(0x45EA72823D2A00C3L));
		l.add(new NodeAddressPair(0x463295EAA65F542DL));
		l.add(new NodeAddressPair(0x4663620F501C9270L));
		l.add(new NodeAddressPair(0x49B24B09E14DCBEDL));
		l.add(new NodeAddressPair(0x4A8A8D029274C9A5L));
		l.add(new NodeAddressPair(0x4AEB8C88BA29B787L));
		l.add(new NodeAddressPair(0x4AFFBA54C9F8685FL));
		l.add(new NodeAddressPair(0x4B235E887CC5FDCEL));
		l.add(new NodeAddressPair(0x4DCA087078898728L));
		l.add(new NodeAddressPair(0x4ED21626B69B8D5FL));
		l.add(new NodeAddressPair(0x4F24FDB6BE16210BL));
		l.add(new NodeAddressPair(0x56B99BB29B40D50CL));
		l.add(new NodeAddressPair(0x59B5F73DEE3965EDL));
		l.add(new NodeAddressPair(0x5A6528DB601D021EL));
		l.add(new NodeAddressPair(0x5A961B81D1F730BDL));
		l.add(new NodeAddressPair(0x5ADDA055E3491767L));
		l.add(new NodeAddressPair(0x5C82722BF5EB0783L));
		l.add(new NodeAddressPair(0x5D24B399DB5BBCD5L));
		l.add(new NodeAddressPair(0x5DC9D16660E73E8CL));
		l.add(new NodeAddressPair(0x5FD7927FB26CB172L));
		l.add(new NodeAddressPair(0x6044F02AC45A6F71L));
		l.add(new NodeAddressPair(0x61168793D1836350L));
		l.add(new NodeAddressPair(0x629BC2229CEFE94DL));
		l.add(new NodeAddressPair(0x6307A773A2A18CA2L));
		l.add(new NodeAddressPair(0x63BA7A9EABB152E9L));
		l.add(new NodeAddressPair(0x64E8023648D1F706L));
		l.add(new NodeAddressPair(0x64EA862F9C952497L));
		l.add(new NodeAddressPair(0x64F6F3945B8436DAL));
		l.add(new NodeAddressPair(0x671E6A44626E1845L));
		l.add(new NodeAddressPair(0x680AA1D538A7E135L));
		l.add(new NodeAddressPair(0x68862528BA922814L));
		l.add(new NodeAddressPair(0x689338C1AE4B4531L));
		l.add(new NodeAddressPair(0x6929F09B03D242CAL));
		l.add(new NodeAddressPair(0x69982B0D628F7CACL));
		l.add(new NodeAddressPair(0x6ACF25F56C3241C8L));
		l.add(new NodeAddressPair(0x6B6413273094A1B3L));
		l.add(new NodeAddressPair(0x6DCB97623EA6FB9AL));
		l.add(new NodeAddressPair(0x6DCCAF105F5107A3L));
		l.add(new NodeAddressPair(0x6F23ED231C3D2C66L));
		l.add(new NodeAddressPair(0x7000ABA2830BF3E7L));
		l.add(new NodeAddressPair(0x710158705210B615L));
		l.add(new NodeAddressPair(0x7337A44574A07FC4L));
		l.add(new NodeAddressPair(0x7684676C46350E7DL));
		l.add(new NodeAddressPair(0x7BD1CF570234BE8BL));
		l.add(new NodeAddressPair(0x7D9FF7E5E3CC0B75L));
		l.add(new NodeAddressPair(0x7F3F426EA5CC5123L));

		System.out.println(l);

		System.out.println(l.inRange(0xBB20B45FD4D95138L) );

		System.out.println(l.inRange(0xBDBE135233AC5AFEL) ); //edge
		System.out.println(l.inRange(0xC574C40AF2BC3C2DL) ); //inside
		System.out.println(l.inRange(0xC988057B8C355BB8L) ); //middle
		System.out.println(l.inRange(0xCFA337658B7BE602L) ); //inside
		System.out.println(l.inRange(0xD66AF990B2E25C89L) ); // edge

		System.out.println(l.inRange(0xDA29F5BD6124E9CFL) );






	}

	/**
	 *
	 */
	public void clear() {
		leftset.clear();
		rightset.clear();
		fullset.clear();
	}
}