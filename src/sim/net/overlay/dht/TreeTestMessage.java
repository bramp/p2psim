/*
 * Created on 07-Mar-2005
 */
package sim.net.overlay.dht;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Andrew Brampton
 */
public class TreeTestMessage extends Message {
	private boolean orphan;
	private List<Integer> childHops;

	public TreeTestMessage(int fromAddress, long fromID, long toID) {
		super(fromAddress, fromID, toID);
		childHops = new ArrayList<Integer>();
		setOrphan(true);
	}

	public void setOrphan(boolean state) {
		orphan = state;

		if (orphan) {
			childHops.add(this.hop);
		}
	}

	public boolean isOrphan() {
		return orphan;
	}

	public List<Integer> getChildHops() {
		return childHops;
	}
}
