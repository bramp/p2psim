package sim.workload.puredht;

import sim.workload.Default;

public class ConstMsgPerPeer50Test extends Default {
	public ConstMsgPerPeer50Test(String[] arglist) throws Exception {
		super(arglist);
		setupNormal(Integer.parseInt(arglist[0]));
		sendRandomMessagesPerPeer(50, false);
	}
}
