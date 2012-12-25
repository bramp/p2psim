/**
 *
 */
package sim.net.overlay.meridian;

import sim.net.Packet;
import sim.net.RoutingException;
import sim.net.links.Link;

/**
 *
 * A Median client, that just issues request (for service)
 *
 * @author Andrew Brampton
 *
 */
public class Client extends Node {

	/**
	 * The server this client is currently using
	 */
	int newServer;

	/**
	 * @param address
	 */
	public Client(int address) {
		super(address);
	}

    public void makeRequest(int server) throws RoutingException {
    	newServer = server;
    	send (RequestPacket.newPacket(this.address, server, rings ) );
	}

//   @Override
	public void recv(Link link, Packet p ) {
		super.recv(link, p);

		if (p instanceof RedirectPacket){

			RedirectPacket rp = (RedirectPacket) p;

			try {
//				System.out.println("client sending to "+ rp.newServer  +" after redirect...."+ this.address);
				//send (RequestPacket.newPacket(this.address, rp.newServer, rings) );

				makeRequest(rp.newServer);

			} catch (RoutingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}
