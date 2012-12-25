/*
 * Created on 22-Mar-2005
 */
package sim.net.overlay.tree.tbcp;

import java.util.Set;

import sim.net.overlay.tree.tbcp.Node.AddressPair;

class PingResultMessage extends Message {
	/**
	 * Set of AddressPair, containing address + delays
	 */
	Set<AddressPair> pairs;
}