/*
 * Created on 13-Feb-2005
 */
package sim.net.overlay.dht.pastry;

import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;


/**
 * @author Andrew Brampton
 */
public class OffloadAnnouceMessage extends Message {

	public NodeAddressPairs pairs = new NodeAddressPairs();
	public long key ;

	/**
	 * @param fromID
	 * @param toID
	 */
	public OffloadAnnouceMessage(int fromAddress, long fromID, long toID, long key) {
		super(fromAddress, fromID, toID);
        this.key = key;
	}

	public final NodeAddressPairs getPairs() {
		return pairs;
	}

	/**
	 * Copies the pair supplied into our own buffer
	 * @param pairs
	 */
	public void add(NodeAddressPair pairs) {
		this.pairs.add(pairs);
	}

	/**
	 * Copies the pairs supplied into our own buffer
	 * @param pairs
	 */
	public void addAll(NodeAddressPairs pairs) {
		this.pairs.addAll( pairs );
	}

	@Override
	protected String _toString() {
		if (pairs == null)
			return "0 pairs";
		else
			return pairs.size() + " pairs";
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.Message#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();

		pairs.clear();
		pairs = null;
	}
}
