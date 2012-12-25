package sim.net.overlay.dht;

public interface DHTListener {

	/**
	 * Called whenever the leafSet changes. The newPair is the
	 * newPair that was added, or removed
	 * @param newPair The changed Pair
	 * @param addition Added or Removed
	 */
	void newLeafSet(NodeAddressPair newPair, boolean addition);

	/**
	 * A Get Message has returned
	 */
	void getHasArrived(PeerData pd);


}
