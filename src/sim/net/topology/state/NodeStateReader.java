/*
 * Created on 12-Feb-2005
 */
package sim.net.topology.state;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sim.main.Global;
import sim.net.HostSet;
import sim.net.topology.reader.NodeLoader;


/**
 * @author Andrew Brampton
 */
public class NodeStateReader implements NodeReader, NodeWriter {

	String filename;

	public NodeStateReader(String filename) {
		this.filename = filename;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.reader.NodeReader#load()
	 */
	public void load(NodeLoader loader) throws Exception {

		if (loader != null)
			throw new RuntimeException("Loader is not used!, don't give me one");

		ObjectInputStream file;

		// Open the file for saving
		GZIPInputStream zip = new GZIPInputStream(new FileInputStream(filename));
		file = new ObjectInputStream(zip);

		// Write the nodes to disk (including links etc)
		Global.hosts = (HostSet)file.readObject();
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.reader.NodeWriter#save(java.util.List)
	 */
	public void save(HostSet nodes) throws Exception {
		ObjectOutputStream file;
		try {

			// Open the file for saving wrapped in GZIP since these files can get big >100mb
			GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(filename));
			file = new ObjectOutputStream(zip);

			// Write the nodes to disk (including links etc)
			file.writeObject(nodes);
			file.flush();
			file.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
