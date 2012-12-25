/*
 * Created on 07-Mar-2005
 */
package sim.net.overlay.dht;
/**
 * @author Andrew Brampton
 */
public class TestMessage extends Message {

	/**
	 * @param fromAddress
	 * @param fromID
	 * @param toID
	 */
	public TestMessage(int fromAddress, long fromID, long toID) {
		super(fromAddress, fromID, toID);
	}

}
