package sim.main;

import java.io.File;

import sim.events.Events;
import sim.net.overlay.dht.pastry.PeerBase;
import sim.net.topology.reader.GTITMReader;
import sim.net.topology.reader.nodeloader.TSRouterLoader;
import sim.net.topology.state.NodeReader;
import sim.stats.ResultWriter;
import sim.stats.StatsObject;
import sim.stats.trace.Trace;
import static sim.stats.StatsObject.SEPARATOR;

/**
 * @author macquire
 *
 */
public class Validate extends ResultWriter {
	public static void main(String[] cliargs) throws Exception {

		Global.stats.logValue("Sim" + StatsObject.SEPARATOR + "b",PeerBase.b);

		// [service nodes][stealth nodes][messages exchanged][quickjoin?][direct msgs?]
		String[] params = {"500","0","1000","false","false"};

		int seed = 0;

		Global.folder = "log/validation/";

		// create output directories for this simulation run
		File folder = new File(Global.folder);
		File traceFolder = new File(Global.folder + "traces/");
		File logFolder = new File(Global.folder + "log/");
		if (!folder.exists()) {
			if (!folder.mkdir()) {
				throw new Exception("Could not create main output dir!");
			}
		}
		if (!traceFolder.exists()) {
			if (!traceFolder.mkdir()) {
				throw new Exception("Could not create trace output dir!");
			}
		}
		if (!logFolder.exists()) {
			if (!logFolder.mkdir()) {
				throw new Exception("Could not create log output dir!");
			}
		}

		//Now do a silly junction thing :)
		try {
			Runtime.getRuntime().exec("junctionme.cmd " + Global.folder);
		} catch (Exception e) {} // Ignore any exception

		// begin a trace for this individual run
		Global.debug_use_gzip = false;
		Trace.openLog(Global.folder + "traces/output.txt");

		Global.logprefix = Global.folder + "log/";

		// seed the random number generator to allow for simulation recreation
		Global.rand.setSeed(seed);

		long startTime = System.currentTimeMillis();

		// reset topology
		NodeReader reader = new GTITMReader("topology/topo-0-big.alt");

		// load nodes appropriately
		reader.load(new TSRouterLoader());

		// initialise workload
		new sim.workload.stealth.ConstMsgGlobalTest(params);

		// simulate!
		try {
			while (Events.runNextEvent()) {}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Global.stats.logCount("Sim" + SEPARATOR + "FatalError");
		}

		// output
		Global.stats.logValue("Sim" + SEPARATOR + "Seed", seed);
		Global.stats.logValue("Sim" + SEPARATOR + "WallDuration", (System.currentTimeMillis() - startTime));
		Global.stats.logValue("Sim" + SEPARATOR + "SimDuration", Events.getTime());
		System.err.println("Completed in " + (System.currentTimeMillis() - startTime) / 1000.00 + "s");
		System.err.println("Simulation ended at " + Events.getTime());
		System.err.println("---STATS---");
		System.err.println(Global.stats);
		appendResults(Global.stats.getData());

		// make sure the trace files are fully written
		Global.writers.close();
		Trace.flush();
	}
}