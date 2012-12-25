package sim.workload.stealth;

public class ConstMsgPerPeer50Test extends Default {
	public ConstMsgPerPeer50Test(String[] arglist) throws Exception {
		super(arglist);
		setupStealth(Integer.parseInt(arglist[0]), Double.parseDouble(arglist[1]));
		sendRandomMessagesPerPeer(50, false);
	}
}
