package sim.workload.sigcomm;

import sim.events.Events;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;

public class SandboxTest extends Default {
	public SandboxTest(String[] arglist) throws Exception {
		setupPeers(1,1);
		fastJoinAll();
		addPeerData(100,1);
		Events.addNow(GetAndPassEvent.newEvent(1));
	}
}
