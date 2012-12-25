package sim.main;

/**
 * A simulator to help test things
 * @author brampton
 *
 */
public class MilesSimulator {

	public static void main(String[] cliargs) throws Exception {

		//              ID of run, workload name,     name to save logs, seed, # of nodes
		String[] args = {"0", "sim.net.overlay.dht.ddos.KeysTest", "test", "0", "100"};

		Global.debug_use_gzip = false;

		Simulator.main( args );
	}
}
