package sim.workload.stealth;

public class ConstMsgGlobalTest2 extends Default {
	public ConstMsgGlobalTest2(String[] arglist) throws Exception {
		super(arglist);
		setupStealth(Integer.parseInt(arglist[0]),Integer.parseInt(arglist[1]));
		sendRandomMessages(100000, false);
	}
}
