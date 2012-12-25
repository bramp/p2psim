/**
 *
 */
package sim.net.overlay.dht.ddos;

import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.pastry.GetMessage;
import sim.net.overlay.dht.pastry.GetReplyMessage;

/**
 * @author brampton
 *
 */
public class Peer extends sim.net.overlay.dht.pastry.Peer {

	// Place your table of popularity here

	/**
	 * @param address
	 */
	public Peer(int address) {
		super(address);
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.pastry.Peer#send(int, sim.net.overlay.dht.Message)
	 */
	@Override
	public void send(int toAddress, Message msg) {

		// The message is about to be sent here

		// msg.objectID is a unique ID for each message

		super.send(toAddress, msg);
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.pastry.Peer#recv(sim.net.overlay.dht.Message, boolean)
	 */
	@Override
	protected boolean recv(Message msg, boolean forUs) {

		if ( msg instanceof GetMessage ) {
			// Do something
			//msg.fromID = this.nodeID

		} else if ( msg instanceof GetReplyMessage ) {
			// Do something else

		}


		return super.recv(msg, forUs);
	}

}
