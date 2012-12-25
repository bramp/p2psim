package sim.workload.stealth;

public class ConstMsgPerPeer100Test2 extends Default {
	public ConstMsgPerPeer100Test2(String[] arglist) throws Exception {
		super(arglist);
		setupStealth(Integer.parseInt(arglist[0]), Integer.parseInt(arglist[1]));
		sendRandomMessagesPerPeer(100, false);
	}
}
