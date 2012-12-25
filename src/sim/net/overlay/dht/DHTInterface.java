/*
 * Created on Feb 28, 2005
 */
package sim.net.overlay.dht;

import sim.net.overlay.dht.authentication.AuthData;

/**
 * @author Andrew Brampton
 */
public interface DHTInterface {

	public static final long INVALID_ID = -1;

	/**
	 * Called to join the network
	 * @param joinAddress
	 */
	public void join(int joinAddress);

	public boolean hasJoined();

	/**
	 * Routes the message to the correct place
	 * @param msg
	 */
	public void route(Message msg);

	public int getRoute(long id);

	/**
	 * Sends a message directly to a peer
	 * @param toAddress
	 * @param msg
	 */
	public void send(int toAddress, Message msg);

	/**
	 * Receives a message at the peer
	 * @param msg The message that we have recved
	 */
	public void recv(Message msg);

	/**
	 * Returns the address of this node
	 */
	public long getID();

	/**
	 * Creates a PutMessage and send the data into the network
	 * @param data
	 */
	public void put(PeerData data);

	/**
	 * Stores data directly on this node
	 * @param data
	 */
	public void localPut(PeerData data);

	/**
	 * This gets the object associated with this key from the DHT
	 * @param key
	 * @return
	 */
	public void get(String key);
	public void get(long hash);

	/**
	 * @return The peer's AuthData
	 */
	public AuthData getAuth();

	/**
	 * Sets the node's auth
	 * @param auth
	 */
	public void setAuth(AuthData auth);

	/**
	 * Tells the node to aquire auth creditials from this address
	 * @param authAddress
	 */
	public void auth(int authAddress);
}
