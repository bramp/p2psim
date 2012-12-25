package sim.workload.routing;

public class RoutingTest extends Default {
	public RoutingTest(String[] arglist) throws Exception {
		super(arglist);

		setupStealth(Integer.parseInt(arglist[0]),500);
		sendRandomMessagesPerPeer(200,false);
	}
}