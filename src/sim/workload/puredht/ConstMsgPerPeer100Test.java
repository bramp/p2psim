package sim.workload.puredht;

import sim.workload.Default;

public class ConstMsgPerPeer100Test extends Default {
	public ConstMsgPerPeer100Test(String[] arglist) throws Exception {
		super(arglist);
		setupNormal(Integer.parseInt(arglist[0]));
		sendRandomMessagesPerPeer(100, false);
	}
}
