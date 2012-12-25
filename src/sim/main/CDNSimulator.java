package sim.main;

import sim.net.topology.reader.InetFileReader;

/**
 * Simulator setup for the whatever the CDN needs
 * @author brampton
 *
 */
public class CDNSimulator {

	public static void main(String[] cliargs) throws Exception {

		Simulator.topology = new InetFileReader( "topology/inet-1" );
		Simulator.nodeloader = "sim.net.topology.reader.nodeloader.InetRouterLoader";

		Simulator.main( cliargs );
	}
}
