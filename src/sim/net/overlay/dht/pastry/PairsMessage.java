/*
 * Created on 13-Feb-2005
 */
package sim.net.overlay.dht.pastry;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.main.Global;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.authentication.AuthData;


/**
 * @author Andrew Brampton
 */
public class PairsMessage extends Message {

	private List<NodeAddressPair> pairs = null;

	/**
	 * @param fromID
	 * @param toID
	 */
	public PairsMessage(int fromAddress, long fromID, long toID) {
		super(fromAddress, fromID, toID);

		pairs = new ArrayList<NodeAddressPair>(16);

		updateSize();
	}

	protected void updateSize() {

		int size = pairs.size() * (16 + 8);
		setSize( size );
	}

	public final List<NodeAddressPair> getPairs() {
		return pairs;
	}

	/**
	 * Copies the pair supplied into our own buffer
	 * @param pairs
	 */
	public void add(NodeAddressPair pairs) {
		this.pairs.add(pairs);
		updateSize();
	}

	/**
	 * Copies the pairs supplied into our own buffer
	 * @param pairs
	 */
	public void addAll(NodeAddressPairs pairs) {
		Iterator<NodeAddressPair> i = pairs.iterator();
		while (i.hasNext())
			this.pairs.add(i.next());

		updateSize();
	}

	/**
	 * Copies the pairs supplied into our own buffer
	 * @param pairs
	 */
	public void addAll(NodeAddressPairs[] pairs) {
		for (int i = 0; i < pairs.length; i++) {
			if (pairs[i] != null)
				addAll(pairs[i]);
		}
		updateSize();
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
		updateSize();
		pairs = null;
	}

	public void addAuth(AuthData auth) {
		if (Global.auth_check_join)
			super.addAuth(auth);
	}
}
