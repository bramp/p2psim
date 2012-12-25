package sim.stats.trace;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;

import sim.events.Events;
import sim.main.Global;
import sim.stats.StatsObject;

public abstract class Trace {
	protected static GZIPOutputStream gzip = null;
	protected static PrintStream out = null;

	// String builder used to make log lines
	protected static final StringBuilder sb = new StringBuilder();

	private static class FlushThread extends Thread {
		OutputStream out;

		public FlushThread(OutputStream out) {
			super("FlushThread");

			this.out = out;

			// Set this to a daemon, so it will exit when the rest of the program is finished
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				do {
					// Flush the stream
					out.flush();

					sleep(Global.debug_use_flush_thread_interval);
				} while (isAlive());

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// uses object in case a string is not passed
	public static void println(LogLevel logLevel, String obj) {

		if (out == null)
			return;

		// Clear the StringBuilder
		sb.setLength(0);

		// Now build the log line
		sb.append( Events.getTime() );
		sb.append( ": ");
		sb.append( obj );
		out.println( sb.toString() );

		switch (logLevel) {
			case ERR:
				Global.stats.logCount("Sim" + StatsObject.SEPARATOR + "Error");
				break;
			case WARN:
				Global.stats.logCount("Sim" + StatsObject.SEPARATOR + "Warning");
			default:
		}
	}

	public static void openLog(String logfile) throws IOException {

		if (out != null) {
			close();
		}

		OutputStream os;

		// Optionally add GZIP compression
		if (Global.debug_use_gzip) {
			os = new FileOutputStream(logfile + ".gz");
			gzip = new GZIPOutputStream(os);
			os = gzip;
		} else {
			//Open the file
			os = new FileOutputStream(logfile);
		}

		// Now create the buffered PrintStream
		// TODO Figure out if increasing buffersize helps
		if (Global.debug_use_bufferedlog) {
			out = new PrintStream(new BufferedOutputStream(os, 8192 * 4));

			if (Global.debug_use_flush_thread) {
				new FlushThread(out).start();
			}

		} else {
			out = new PrintStream(os);
		}

		//out = new PrintStream(os);
	}

	public static void close() throws IOException {
		if (out == null)
			return;

		println(LogLevel.INFO, "End of simulation");

		flush();
		out.close();
	}

	public static void flush() throws IOException {
		if (out == null)
			return;

		out.flush();

		if (gzip != null)
			gzip.finish();
	}

	/**
	 * Opens a empty log file, and closes it straight away
	 * This allows us to create 0byte files
	 * @param logfile
	 */
	public static void openNullLog(String logfile) throws IOException {
		if (out != null) {
			close();
		}

		// Optionally add GZIP compression
		if (Global.debug_use_gzip) {
			logfile += ".gz";
		}

		// Open and close the file
		OutputStream os = new FileOutputStream(logfile);
		os.close();
		out = null;
	}
}
