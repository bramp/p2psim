package sim.stats.trace;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Iterator;

import sim.main.Global;

public class WriterInstances {
	private static WriterInstances instance = null;
	private Hashtable<String,Writer> writers;

	protected WriterInstances() {
		writers = new Hashtable<String,Writer>();
	}

	public static WriterInstances getInstance() {
		if (instance == null) {
			instance = new WriterInstances();
		}

		return instance;
	}

	public Writer getWriter(String name) throws IOException {
		Writer result = null;

		if (!writers.containsKey(name)) {
			result = new BufferedWriter(new FileWriter(Global.logprefix + name + ".log"));
			writers.put(name,result);
		}
		else {
			result = writers.get(name);
		}

		return result;
	}

	public void addItem(String name,Object item) {
		try {
			getWriter(name).write(item.toString() + "\n");
		}
		catch(IOException e) {
			System.err.println("Error writing to " + name);
			Global.fatalExit();
		}
	}

	public void close() {
		Iterator<Writer> i = writers.values().iterator();

		try {
			while(i.hasNext()) {
				i.next().close();
			}
		}
		catch (IOException e) {
			System.err.println("Error closing writers!");
			Global.fatalExit();
		}
	}
}
