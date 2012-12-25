package sim.workload;

/**
 * @author macquire
 *
 */
public class ScaleTest extends Default {
	public ScaleTest(String arglist[]) throws Exception {
		super(arglist);
		setupPeers(Integer.parseInt(arglist[0]));
		fastJoinAllNodes();
		sendRandomMessagesPerPeer(100, false);
	}
}
