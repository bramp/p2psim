package sim.main;

import sim.events.Events;
import sim.net.overlay.dht.pastry.PeerBase;
import sim.net.topology.reader.GTITMReader;
import sim.net.topology.reader.NodeLoader;
import sim.net.topology.state.NodeReader;
import sim.stats.ResultWriter;
import sim.stats.StatsObject;
import sim.stats.trace.Trace;
import sim.workload.Workload;
import static sim.stats.StatsObject.SEPARATOR;

/**
 * @author macquire
 * @author brampton
 *
 */
public class Simulator extends ResultWriter {

	protected static NodeReader topology = null;
	protected static String nodeloader = "sim.net.topology.reader.nodeloader.TSRouterLoader";

	public static void main(String[] cliargs) throws Exception {
		String[] args = cliargs;

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
		String order = args[0]; // An indentifier so we can order the results

		// Log the indentify for this simulation (used to order them later)
		Global.stats.logValue("Sim" + SEPARATOR + "Order", Integer.parseInt(order) );

		String workload = args[1];
		String name = args[2];

		int seed = Integer.parseInt(args[3]);

		// placeholders for when these are eventually read in somewhere
		if (topology == null)
			topology = new GTITMReader( "topology/topo-0-big.alt" );

		if ( nodeloader == null)
			nodeloader = "sim.net.topology.reader.nodeloader.TSRouterLoader";

		Global.stats.logValue("Sim" + SEPARATOR + "Workload", workload.hashCode());
		Global.stats.logValue("Sim" + SEPARATOR + "Name", name.hashCode());
		Global.stats.logValue("Sim" + SEPARATOR + "Topology", topology.hashCode());

		// convert workload params into a form which can be passed to a constructor
		Object[] params = new String[args.length - 4];
		System.arraycopy(args, 4, params, 0, params.length);

		String param = "";
		for (int i = 0; i < params.length; i++) {
			double paramvalue;

			try {
				paramvalue = Double.parseDouble(params[i].toString());

			} catch(NumberFormatException e) {

				if ( "true".equalsIgnoreCase( params[i].toString() ) ) {
					paramvalue = 1;

				} else if ( "false".equalsIgnoreCase( params[i].toString() ) ) {
					paramvalue = 0;

				} else {
					// If non-numerical then log the hashcode of the string
					paramvalue = params[i].hashCode();
				}
			}

			param += paramvalue + "-";

			Global.stats.logValue("Sim" + SEPARATOR + "Param" + i, paramvalue);
		}

		Global.folder = "log/" + name + "-" + workload +  "/";

		// create output directories for this simulation run
		createDirectory( Global.folder );
		createDirectory( Global.folder + "traces/" );
		createDirectory( Global.folder + "log/" );

		//Now do a silly junction thing :)
		try {
			Runtime.getRuntime().exec("junctionme.cmd " + Global.folder);
		} catch (Exception e) {} // Ignore any exception

		// begin a trace for this individual run
		Trace.openLog(Global.folder + "traces/output-" + param + seed + ".txt");
		//Trace.openNullLog(Global.folder + "traces/output-" + param + seed + ".txt");

		Global.logprefix = Global.folder + "log/" + param + seed + "-";

		// seed the random number generator to allow for simulation recreation
		Global.rand.setSeed(seed);

		long startTime = System.currentTimeMillis();

		// load nodes appropriately
		topology.load((NodeLoader)Class.forName(nodeloader).getConstructor().newInstance());

		// initialise workload
		Object[] workloadParams = { params };
		Workload w = (Workload) Class.forName(workload).getConstructor(String[].class).newInstance(workloadParams);

		w.simulationStart();

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
		writeResults(order, Global.stats.getData());

		// make sure the trace files are fully written
		Global.writers.close();
		Trace.flush();
	}
}