package sim.workload.stealth;

public class ConstMsgPerPeer100Test extends Default {
	public ConstMsgPerPeer100Test(String[] arglist) throws Exception {
		super(arglist);
		setupStealth(Integer.parseInt(arglist[0]), Double.parseDouble(arglist[1]));
		sendRandomMessagesPerPeer(100, false);
	}
}
