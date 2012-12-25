package sim.workload.puredht.simulfail;

import sim.workload.Default;

public class ConstMsgPerPeer100SimulFailTest extends Default {
	public ConstMsgPerPeer100SimulFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double fail = Double.parseDouble(arglist[1]);

		setupSimulFail((int)(fail*count),count);
		sendRandomMessagesPerPeer(100, false);
	}
}