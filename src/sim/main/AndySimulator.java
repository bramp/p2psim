package sim.main;

import java.io.File;

import sim.events.Events;
import sim.net.overlay.dht.pastry.PeerBase;
import sim.net.topology.reader.GTITMReader;
import sim.net.topology.reader.NodeLoader;
import sim.net.topology.state.NodeReader;
import sim.stats.ResultWriter;
import sim.stats.StatsObject;
import sim.stats.trace.Trace;
import static sim.stats.StatsObject.SEPARATOR;

/**
 * @author macquire
 *
 */
public class AndySimulator extends ResultWriter {
	public static void main(String[] cliargs) throws Exception {
		// swap these around to control single sims
		//String[] args = cliargs;
		// -->																					                    -25-  475-  50000-  1000-  1-  3-  360000-  0.95-  3-0.txt.gz $seed $failType $service $stealth $churnTime $keys $k $gets $getTime $percentage $recoveryType
		//String[] args = {"sim.workload.sigcomm.KeysTest",new Long(System.currentTimeMillis()).toString(),"0","Service","25","475","50000","1000","1","3","360000","0.95","3"};
		//String[] args = {"sim.workload.sigcomm.JoinsTest",new Long(System.currentTimeMillis()).toString(),"0","2","0","100000"};
		//String[] args = {"sim.workload.stealth.UniformMsgTest","symmend","0","10","100","1152921504606846975"};
		//String[] args = {"sim.workload.stealth.RoutingTableTest", "quicksim", "0", "250", "true"};
		//String[] args = {"sim.workload.authentication.StealthAuthTest","test","0","10","10","10","0","true"};
		//String[] args = {"sim.workload.authentication.MaliciousJoinTest","malicious","0","500","250"};
		//String[] args = {"sim.workload.authentication.MaliciousTest","malicious","0","100","0","10000","1","10","360000","50","true"};

		                                                 // name , seed, proxyServers, proxyClients
		//String[] args = {"sim.workload.idris.ProxyTest", "proxy", "0", "20","100"};

		//String[] args = {"sim.workload.idris.KeysChurnTest", "churn", "3", "10", "190"};
		//String[] args = {"sim.workload.idris.ProxyChurnTest", "churn", "0", "10", "100"};
		//String[] args = {"sim.workload.idris.ProxyChurnTest", "churn", "0", "10", "100"};
		 //String[] args = {"sim.workload.idris.ProxyChurnTest", "churn", "2", "25", "500"};
		 //String[] args = {"sim.workload.idris.OffloadTest", "ktest", "6", "50", "950","1","0.9"};
//		 String[] args = {"sim.workload.MultiTest","multitest","0"};

		String[] args = {"sim.net.overlay.meridian.workload.Meridian", "meridian", "0", "50", "100"};


		Global.stats.logValue("Sim" + StatsObject.SEPARATOR + "b",PeerBase.b);
		Global.debug_use_gzip = false;

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
		String topology = "topology/topo-0-big.alt";
		String nodeloader = "sim.net.topology.reader.nodeloader.TSRouterLoader";

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
		Trace.openLog(Global.folder + "traces/output-n" + param + seed + ".txt");
		//Trace.openNullLog(Global.folder + "traces/output-n" + param + seed + ".txt");

		Global.logprefix = Global.folder + "log/" + param + seed + "-";

		// seed the random number generator to allow for simulation recreation
		Global.rand.setSeed(seed);

		long startTime = System.currentTimeMillis();

		// reset topology
		NodeReader reader = new GTITMReader(topology);

		// load nodes appropriately
		reader.load((NodeLoader)Class.forName(nodeloader).getConstructor().newInstance());

		// initialise workload
		Class.forName(workload).getConstructor(String[].class).newInstance(workloadParams);

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
		System.err.println("Completed " + workload + " in " + (System.currentTimeMillis() - startTime) / 1000.00 + "s");
		System.err.println("Simulation ended at " + Events.getTime());
		System.err.println("---STATS---");
		System.err.println(Global.stats);
		appendResults(Global.stats.getData());

		Global.writers.close();
		Trace.flush();
	}
}
