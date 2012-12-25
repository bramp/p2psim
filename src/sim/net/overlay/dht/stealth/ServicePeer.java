/*
 * Created on 19-May-2005
 */
package sim.net.overlay.dht.stealth;

import java.util.Iterator;

import sim.main.Global;
import sim.net.HostSet;
import sim.net.overlay.dht.EncapTestMessage;
import sim.net.overlay.dht.RegTestMessage;
import sim.net.overlay.dht.TreeTestMessage;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.pastry.GetReplyMessage;
import sim.net.overlay.dht.pastry.JoinMessage;
//import sim.net.overlay.dht.pastry.PromoMessage;

public class ServicePeer extends Peer {

	/**
	 * @param address
	 */
	public ServicePeer(int address) {
		super(address);
	}

	public ServicePeer(int address, long nodeID) {
		super(address, nodeID);
	}

	private boolean hadTreeTestMsg = false;
	@Override
	public void fastJoin(HostSet peers) {
		peers = peers.getType(ServicePeer.class);
		peers.remove(this); // Remove myself

		fastJoinLeafSet(peers);
		fastJoinRoutingTable(peers);

		if (peers.isEmpty()) {
			join(INVALID_ADDRESS);
		} else {
			joinAddress = peers.getRandom().getAddress();
		}

		setJoined(true);
	}

	@Override
	public boolean recv(Message msg, boolean forUs) {
		boolean forward = true;

		// if the node is malicious and the message is not from
		// this node, tamper with it
		if (malicious && msg.fromID != this.nodeID && !(msg instanceof GetReplyMessage)) {
			msg.setTampered();
		}

		if (malicious && msg instanceof JoinMessage) {
			// evil peers drop joins
			return false;
		}

		forward = super.recv(msg, forUs);

		if (msg instanceof TreeTestMessage) {
			TreeTestMessage treeMsg = ((TreeTestMessage)msg);

			// if i'm the topic root
			if (forUs) {
				Iterator<Integer> i = treeMsg.getChildHops().iterator();

				while(i.hasNext()) {
					// record how many hops each child was from the root
					Global.writers.addItem("distance", msg.hop - i.next());
				}
			}

			if (treeMsg.hop == 0) {
				// this node has been a message origin
				Global.writers.addItem("origin-service",this.nodeID);
			}
			// if this node isn't the origin, and the previous hop was an orphan
			else if (msg.hop > 0 && treeMsg.isOrphan()) {
				// this node has gained a child
				Global.writers.addItem("children",this.nodeID + "\t" + msg.fromID);
				// if we haven't had a tree message before and we're not the root
				// then we need to join
				if (!forUs) {
					treeMsg.setOrphan(!hadTreeTestMsg);
				}
			}

			// make sure we don't unnecessarily rejoin
			hadTreeTestMsg = true;
		}
		else if (msg instanceof EncapTestMessage) {
			if (forUs) {
				Global.writers.addItem("encaphops",msg.hop);
			}

			if (msg.hop == 1) {
				Global.writers.addItem("encapfirsthops",this.nodeID);
			}

			if (msg.hop > 0) {
				Global.writers.addItem("encaphandled",this.nodeID);
			}
		}
		else if (msg instanceof RegTestMessage) {
			if (forUs) {
				// + 1 for the extra hop to the stealth node
				Global.writers.addItem("reghops",msg.hop + 1);
				Global.writers.addItem("reglasthop",this.nodeID);
			}

			if (msg.hop > 0) {
				Global.writers.addItem("reghandled",this.nodeID);
			}

		}
		/*else if (msg instanceof PromoMessage){
			if(forUs){
				promotedSet.put(fromID, fromAddress);
			}
		}*/

		return forward;
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.pastry.Peer#send(int, sim.net.overlay.dht.Message)
	 */
	@Override
	public void send(int toAddress, Message msg) {
		if (USE_RECOVERY_PIGGYBACK) {
			// Add some addition info
			msg.oob = new NodeAddressPairs();

			if (!allpairs.isEmpty())
				for (int i = 0; i < PIGGYBACK_COUNT; i++)
					((NodeAddressPairs)msg.oob).add( allpairs.random() );
		}

		super.send(toAddress, msg);
	}

}
