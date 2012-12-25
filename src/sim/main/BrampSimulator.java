package sim.main;

import sim.net.overlay.cdn.Common;
import sim.net.overlay.cdn.prefetch.PrefetchScheme;
import sim.net.topology.reader.InetFileReader;

/**
 * A simulator to help test things
 * @author brampton
 *
 */
public class BrampSimulator {

	public static void main(String[] cliargs) throws Exception {

		//String[] args = {"0", "sim.workload.authentication.StealthNoAuthTest", "auth", "1", "1000", "500"};
		//String[] args = {"0", "sim.workload.authentication.StealthAuthTest", "auth", "0", "1000", "1000", "100", "1", "false"};

		String[] args = {"0", "sim.net.overlay.cdn.workload.WorldCupFromLogs", "cdn", "0", "1", "eurovision.actions", "C:\\Projects\\P2PSim\\trunk\\models", PrefetchScheme.BEFORESTARTALL.toString(), "1", "60", "0.9"};
		//String[] args = {"0", "sim.net.overlay.cdn.workload.WorldCupFromLogs", "cdn", "0", "1", "eurovision.actions", "E:\\My Projects\\Uni\\PhD\\Work\\WorldCup06\\parser\\output", "7", "60", "0.5"};

		Simulator.topology = new InetFileReader( "topology/inet-1" );
		Simulator.nodeloader = "sim.net.topology.reader.nodeloader.InetRouterLoader";

		Global.debug_use_gzip = false;

		Common.logMissHit = true;
		
		Simulator.main( args );
	}
}
