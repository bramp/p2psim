package sim.workload.stealth;

import sim.events.Events;
import sim.math.Constant;
import sim.net.overlay.dht.EncapTestMessage;
import sim.net.overlay.dht.events.repeatable.ConstantIDMessagePerStealthPeer;

public class UniformMsgTest extends Default {
	public UniformMsgTest(String[] arglist) throws Exception {
		super(arglist);
		setupStealth(Integer.parseInt(arglist[0]),Integer.parseInt(arglist[1]));
		Events.addAfterLastEvent(ConstantIDMessagePerStealthPeer.newEvent(new Constant(1000),1,Long.parseLong(arglist[2]),EncapTestMessage.class));
		//Events.addAfterLastEvent(RandomIDMessagesSingleService.newEvent(Integer.parseInt(arglist[1]),Long.parseLong(arglist[2])));
	}
}
