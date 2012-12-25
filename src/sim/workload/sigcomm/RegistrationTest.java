package sim.workload.sigcomm;

import sim.events.Events;
import sim.net.overlay.dht.events.repeatable.RandomIDMessagesSingleService;

public class RegistrationTest extends Default {
	public RegistrationTest(String[] arglist) throws Exception {
		int service = Integer.parseInt(arglist[0]);
		int msgcount = Integer.parseInt(arglist[1]);
		long address = Long.parseLong(arglist[2]);

		setupPeers(service,0);
		fastJoinAll();

		Events.addAfterLastEvent(RandomIDMessagesSingleService.newEvent(msgcount,address));
	}
}
