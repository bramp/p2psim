package sim.net.multicast;

import java.util.Hashtable;

import sim.collections.IntVector;
import sim.events.Events;

public class LinkMulticastRecords {
	public static final long DEFAULT_TIMEOUT = 60000;
	private static final long UNUSED_LINK = -1;
	private int linkCount = 0;
	private Hashtable<Integer,long[]> groups;

	// initialise
	public LinkMulticastRecords(int linkCount) {
		this.linkCount = linkCount;
		groups = new Hashtable<Integer,long[]>();
	}

	// add a link number to a multicast group
	public void addRecord(int groupAddress, int lnumber) throws Exception {
		if (!MulticastManager.isMulticast(groupAddress)) {
			throw new Exception("Invalid multicast address " + groupAddress);
		}
		if (lnumber >= linkCount) {
			throw new Exception("Invalid link number " + lnumber);
		}

		long[] records = groups.get(groupAddress);

		if (records == null) {
			records = new long[linkCount];

			for(int i=0;i<linkCount;i++) {
				records[i] = UNUSED_LINK;
			}

			groups.put(groupAddress,records);
		}
		else {
			records = groups.get(groupAddress);
		}

		records[lnumber] = Events.getTime();
	}

	// return all the links for a particular multicast group
	public IntVector getLinkNumbers(int groupAddress) {
		// find which links are valid
		long[] records = groups.get(groupAddress);

		if (records == null) {return null;}

		long currentTime = Events.getTime();
		IntVector result = new IntVector();

		// add valid links to returned vector
		for(int i = 0;i < records.length;i++) {
			// check if the link is included for this group, and that it hasn't timed out
			if (records[i] != UNUSED_LINK && (currentTime - records[i]) < DEFAULT_TIMEOUT) {
				result.add(i);
			}
		}

		return result;
	}

	// test method
	public static void main(String[] args) throws Exception {
		LinkMulticastRecords r = new LinkMulticastRecords(3);
		int group = MulticastManager.getInstance().addGroup(0);
		Events.setTime(0);
		r.addRecord(group,0);
		r.addRecord(group,2);
		Events.setTime(10000);

		IntVector iv = r.getLinkNumbers(group);

		if (iv != null) {
			for(int i=0;i < iv.size();i++) {
				System.out.println(iv.elementAt(i));
			}
		}
	}
}
