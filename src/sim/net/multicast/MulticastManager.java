package sim.net.multicast;

import java.util.Hashtable;
import java.util.Map;

public class MulticastManager {
	private static final int MULTICAST_BEGIN = 2146483647;	// 1m potential groups (bit generous)
	private static MulticastManager instance = null;
	private Map<Integer,Integer> Groups;
	private int groupCount = 0;

	private MulticastManager() {
		Groups = new Hashtable<Integer,Integer>();
	}

	public static MulticastManager getInstance() {
		if (instance == null) {instance = new MulticastManager();}
		return instance;
	}

	public int getRootAddress(int address) throws Exception {
		int result = -1;
		// check the address is a valid multicast address
		if (address >= MULTICAST_BEGIN) {
			// return the appropriate network address
			Integer resultObj = Groups.get(address);
			if (resultObj != null) {
				result = resultObj.intValue();
			}
		}
		else {
			throw new Exception("Invalid multicast address " + address);
		}

		return result;
	}

	public int addGroup(int root) throws Exception {
		if (isMulticast(root)) {
			throw new Exception("Invalid address " + root + " (is reserved for multicast)");
		}

		// create a new group for root
		int groupNumber = MULTICAST_BEGIN + groupCount;

		Groups.put(groupNumber, root);
		groupCount++;

		if (groupCount == Integer.MAX_VALUE) {
			// this should never realistically happen
			throw new Exception("No free multicast group addresses");
		}

		// return the allocated address
		return groupNumber;
	}

	public static boolean isMulticast(int address) {
		return address >= MULTICAST_BEGIN;
	}

	public static String getGroupString(int address) {
		return "M" + (address - MULTICAST_BEGIN);
	}

	// test method
	public static void main(String[] args) throws Exception {
		MulticastManager m = MulticastManager.getInstance();
		int root = 1000;
		int group = m.addGroup(root);
		System.out.println("Created group " + group + " rooted at " + root);
		System.out.println("Group " + group + " resolves to " + m.getRootAddress(group));
	}
}
