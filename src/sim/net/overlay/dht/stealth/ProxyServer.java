/*
 * Created on 19-May-2005
 */
package sim.net.overlay.dht.stealth;

public class ProxyServer extends ServicePeer {

	//Map<Long , NodeAddressPair> mapTable = new TreeMap<Long , NodeAddressPair>();


	public ProxyServer(int address) {
		super(address);
		// TODO Auto-generated constructor stub
	}

	public ProxyServer(int address, long nodeID) {
		super(address,nodeID);
		// TODO Auto-generated constructor stub
	}

	/*
	@Override
	public boolean recv(Message msg, boolean forUs) {

		if(msg.fromproxyclient == true){
			if (!forUs) {
				mapTable.put(msg.objectID, new NodeAddressPair(msg.fromID,  msg.fromAddress));
				msg.fromID = this.nodeID;
				msg.fromAddress = this.address;
				msg.fromproxyclient = false;

				//Iterator<Entry<Long, Integer>> i = mapTable.entrySet().iterator();
				//while (i.hasNext() ) {
				//	Entry e = i.next();
				//	System.out.println ( e.getKey() + " " + e.getValue() );
				//}
				//System.out.println ( mapTable );
			}
		} else {
			if(msg instanceof GetReplyMessage){
				GetReplyMessage reply = (GetReplyMessage)msg;

				if ( forUs && mapTable.containsKey(reply.requestID) ) {
					NodeAddressPair p = mapTable.remove( reply.requestID );

					GetReplyProxyMessage reply2 = new GetReplyProxyMessage(
							p.address,
							reply.fromID,
							p.nodeID,
							reply.getData(),
							reply.objectID);
					reply2.hop = reply.hop;
					reply2.startTime = reply.startTime;

					send(p.address, reply2);

					return false;
				}
			}
		}

		return super.recv(msg, forUs);
	}
  */

}
