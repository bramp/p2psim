package sim.workload.puredht;

import sim.workload.Default;

public class ConstMsgGlobalTest extends Default {
	public ConstMsgGlobalTest(String[] arglist) throws Exception {
		super(arglist);
		setupNormal(Integer.parseInt(arglist[0]));
		sendRandomMessages(Integer.parseInt(arglist[1]), false);
	}
}
