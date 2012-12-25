package sim.workload.sigcomm;

import sim.events.Events;
import sim.math.Constant;
import sim.net.overlay.dht.EncapTestMessage;
import sim.net.overlay.dht.events.repeatable.ConstantIDMessagePerStealthPeer;

public class EncapsulationTest extends Default {
	public EncapsulationTest(String[] arglist) throws Exception {
		int service = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);
		long address = Long.parseLong(arglist[2]);

		setupPeers(service, stealth);
		fastJoinAll();
		Events.addAfterLastEvent(ConstantIDMessagePerStealthPeer.newEvent(new Constant(1000),1,address,EncapTestMessage.class));
	}
}
