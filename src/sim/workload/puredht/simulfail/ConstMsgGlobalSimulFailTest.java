package sim.workload.puredht.simulfail;

import sim.workload.Default;

public class ConstMsgGlobalSimulFailTest extends Default {
	public ConstMsgGlobalSimulFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double fail = Double.parseDouble(arglist[1]);

		setupSimulFail((int)(fail*count),count);
		sendRandomMessages(100000, false);
	}
}
