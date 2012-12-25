package sim.main;

import java.io.File;

import sim.events.Events;
import sim.net.overlay.dht.pastry.PeerBase;
import sim.net.topology.reader.InetFileReader;
import sim.net.topology.reader.NodeLoader;
import sim.net.topology.reader.nodeloader.InetRouterLoader;
import sim.net.topology.state.NodeReader;
import sim.stats.ResultWriter;
import sim.stats.StatsObject;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;
import sim.workload.Workload;
import static sim.stats.StatsObject.SEPARATOR;

/**
 * @author macquire
 *
 */
public class StealthSimulator extends ResultWriter {
	public static void main(String[] cliargs) throws Exception {
		// swap these around to control single sims
		//String[] args = cliargs;
		//String[] args = {"sim.workload.mobile.HardMovingPastryTest", "350", "39", "0.9"};
		//{wifistealth} {fixedstealth} {wifiservice} {fixedservice}   //950
		//String[] args = {"sim.workload.swarm.KeysChurnTest", "100", "100", "0", "0"};
		//String[] args = {"sim.workload.stealth.KeysChurnTypeTest", "sim.net.overlay.dht.stealth.ServicePeer", "0", "sim.net.overlay.dht.stealth.ServicePeer", "111", "0.01"};
		//String[] args = {"sim.workload.stealth.KeysChurnTypeTest", "sim.net.overlay.dht.stealth.StealthPeer", "0", "sim.net.overlay.dht.stealth.StealthPeer", "111", "0.01"};
		//String[] args = {"sim.workload.swarm.mobile.MovingSwarmTest", "justin", "0", "50", "50"};
		//String[] args = {"sim.workload.stealth.KeysChurnTypeTest", "Service","0","Service","1000","0.05","10000"};
		//String[] args = {"sim.workload.stealth.KeysTest2", "test", "0", "10", "100"};
		//String[] args = {"sim.workload.puredht.KeysTest", "hoptests", "0", "100"};

		//                                                          {seed} {servers} {clients} {media}
		//String[] args = {"sim.net.overlay.cdn.workload.WorldCup", "CDN", "0", "1", "10", "1"};
		//String[] args = {"sim.workload.puredht.ConstMsgGlobalTest", "tests", "1", "100", "10000"};

		//String[] args = cliargs;                                               Seed, Service, Stealth
		//String[] args = {"sim.workload.authentication.StealthNoAuthTest", "auth", "0", "100",     "50"};

		String[] args = {"sim.net.overlay.meridian.workload.Meridian", "meridian", "6", "10","30", "5"};

  //	String[] args = cliargs;

		Global.debug_use_gzip = false;
		//Global.debug_log_packets = true;
		Global.stats.logValue("Sim" + StatsObject.SEPARATOR + "b", PeerBase.b);

		if (args.length < 3) {
			System.err.println("Usage: java -classpath ./bin sim.main.Simulator <workload> <seed> <# of peers> <params>");
			System.err.println(" - results are placed in the folder \"<workload>\"");
			System.err.println(" - traces are placed in the folder \"<workload>\\traces\"");

			System.exit(0);
		}

		// remove any rogue characters
		for(int i=0;i<args.length;i++) {
			args[i] = args[i].trim();
		}

		// command line arguments
		String workload = args[0];
		String name = args[1];
		int seed = Integer.parseInt(args[2]);

		// placeholders for when these are eventually read in somewhere
		// NodeReader reader = new GTITMReader("topology/topo-0-big.alt");
		//NodeReader reader = new GTITMReader("topology/topo-0.alt");
		//NodeLoader nodeLoader = new TSRouterLoader();

		NodeReader reader = new InetFileReader("topology/inet-100");
		NodeLoader nodeLoader = new InetRouterLoader();

		// convert workload params into a form which can be passed to a constructor
		Object[] params = new String[args.length - 3];
		System.arraycopy(args,3,params,0,params.length);
		Object[] workloadParams = new Object[1];
		workloadParams[0] = params;


		String param = "";
		for (int i = 0; i < params.length; i++) {
			param += params[i] + "-";

			try {
				double paramvalue = Double.parseDouble(params[i].toString());
				Global.stats.logValue("Sim" + SEPARATOR + "Param" + i,paramvalue);
			}
			catch(Exception e) {
				// don't log non-numerical params at the moment
			}
		}


		Global.folder = "log/" + name + "-" + workload +  "/";

		// create output directories for this simulation run
		File folder = new File(Global.folder);
		File traceFolder = new File(Global.folder + "traces/");
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

		//Now do a silly junction thing :)
		try {
			Runtime.getRuntime().exec("junctionme.cmd " + Global.folder);
		} catch (Exception e) {} // Ignore any exception

		// begin a trace for this individual run
		Trace.openLog(Global.folder + "traces/output-n" + param + seed + ".txt");
		//Trace.openNullLog(Global.folder + "traces/output-n" + param + seed + ".txt");

		// seed the random number generator to allow for simulation recreation
		Global.rand.setSeed(seed);

		long startTime = System.currentTimeMillis();

		// reset topology

		// load nodes appropriately
		reader.load(nodeLoader);

		// initialise workload
		Workload w = (Workload) Class.forName(workload).getConstructor(String[].class).newInstance(workloadParams);
		w.simulationStart();

		//Class.forName(workload).getConstructor(String[].class).newInstance(workloadParams);

		// simulate!
		try {
			while (Events.runNextEvent()) {}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Global.stats.logCount("Sim" + SEPARATOR + "FatalError");
		}

		w.simulationFinished();

		// output
		Global.stats.logValue("Sim" + SEPARATOR + "Seed", seed);
		Global.stats.logValue("Sim" + SEPARATOR + "WallDuration", (System.currentTimeMillis() - startTime));
		Global.stats.logValue("Sim" + SEPARATOR + "SimDuration", Events.getTime());
		System.err.println("Completed " + workload + " in " + (System.currentTimeMillis() - startTime) / 1000.00 + "s");
		System.err.println("Simulation ended at " + Events.getTime());
		System.err.println("---STATS---");
		System.err.println(Global.stats);
		appendResults(Global.stats.getData());

		String end = "Simulation run completed in " + (System.currentTimeMillis() - startTime) / 1000.00 + "s";

		Trace.println(LogLevel.INFO, end);
		Trace.flush();

		// report total run time
		System.err.println();
		System.err.println(end);
	}
}
