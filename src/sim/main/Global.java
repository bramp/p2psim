package sim.main;

import java.text.DecimalFormat;
import java.util.Random;

import sim.events.Events;
import sim.net.HostComparator;
import sim.net.HostSet;
import sim.net.multicast.MulticastManager;
import sim.stats.StatsObject;
import sim.stats.trace.WriterInstances;

public abstract class Global {

	// master host list
	public static HostSet hosts = new HostSet(new HostComparator());

	// The last address added to the network
	public static int lastAddress = 0;

	// single stats object per simulation
	public static StatsObject stats = StatsObject.getInstance();

	// auxiliary writers for miscellaneous logging
	public static WriterInstances writers = WriterInstances.getInstance();

	// multicast address -> network address mappings & group management
	public static MulticastManager groups = MulticastManager.getInstance();

	// TODO add finals before each line (aids in removing dead code)

	// Does some addition checks to make debugging easier
	public static final boolean debug = false;

	// Generates nodes with sequentially generated IDs
	public static final boolean debug_sequential_ids = false;

	// Logs recv/fwd/sent messages to the log file
	public static final boolean debug_log_messages = false; //TODO Implement this

	// Logs recv/fwd/sent packets to the log file
	public static final boolean debug_log_packets = false;

	// Logs when DHT's nodes routing tables are changed
	public static final boolean debug_log_dht_state = false;

	// Logs each event that is run
	public static final boolean debug_log_events = false;

	// Record "good"/"bad" messages
	public static final boolean record_good = false;

	// Sends keep alive messages to the leafsets
	public static final boolean debug_keep_alive = false;

	// Extra checks are done to ensure everythign is running OK. This will slow things down!
	public static final boolean debug_extra_sanity = false;

	// Speed hack to allow stats periods or not
	public static final boolean debug_stats_periods = true;

	// Compresses the log files with GZIP
	public static boolean debug_use_gzip = true;
	public static boolean debug_use_bufferedlog = true;

	// Spawns a thread that flushs the logs every X seconds (useful when buffering a slow simulation)
	public static boolean debug_use_flush_thread = true & debug_use_bufferedlog;
	public static int debug_use_flush_thread_interval = 5000;

	public static boolean auth_on = false;
	public static final boolean auth_check_join = true;
	public static final boolean auth_check_get = true;
	public static final boolean auth_check_put = true;

	/**
	 * These are mutally exclusive (TODO enforce it)
	 */
	public static boolean auth_per_hop = false;
	public static boolean auth_per_session = false;

	public static boolean auth_add_chain = false & auth_on;

	public static final int auth_key_size = 1024;

	// folder name for results to be saved into
	public static String folder;

	// name for traces
	public static String logprefix;

	// output format for all decimals
	public final static DecimalFormat decimal = new DecimalFormat("#.######");

	// random number generator
	public static long SEED = 0;

	public final static Random rand = new Random(SEED);

	/*
	 * Kills the current simulation run due to error
	 */
	public static void fatalExit() {
		Global.stats.logCount("Sim" + StatsObject.SEPARATOR + "FatalExit");
		Global.stats.logCount("Sim" + StatsObject.SEPARATOR + "FatalError");
		Events.stop();
	}
}
