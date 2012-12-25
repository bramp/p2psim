/*
 * Created on Feb 11, 2005
 */
package sim.net.topology.reader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import sim.net.Host;
import sim.net.links.NormalLink;
import sim.net.topology.state.NodeReader;


/**
 * Reads input from a inet file
 * @author Andrew Brampton
 */
public class InetFileReader implements NodeReader {

    private String filename;

    public InetFileReader(String filename) {
        this.filename = filename;
    }

    public void load(NodeLoader loader) {

        LineNumberReader r;
        String line;
        String parts[];
        List<Host> nodes = null;

        try {
            r = new LineNumberReader(new FileReader(filename));

	        //The first two values are, Number of nodes, Number of links
	        line = r.readLine();
	        parts = line.split("\\s");
        	int nCount = Integer.parseInt(parts[0]);
        	int lCount = Integer.parseInt(parts[1]);

        	nodes = new ArrayList<Host>(nCount);

        	//Loop past all the node info
        	while (nCount > 0) {
        		line = r.readLine();
        		parts = line.split("\\s");
        		String type = parts[0].substring(0, 1);
        		int ID = Integer.parseInt(parts[1]);
        		nCount--;
       			nodes.add(ID, loader.createHost(type, ID));
        	}

        	while (lCount > 0) {
        		line = r.readLine();
        		parts = line.split("\\s");
            	int from = Integer.parseInt(parts[0]);
            	int to = Integer.parseInt(parts[1]);
            	int delay = Integer.parseInt(parts[2]) / 100;
        		lCount--;

        		new NormalLink(nodes.get(from), nodes.get(to), NormalLink.BANDWIDTH_DEFAULT, delay);

        	}

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
    	return filename.hashCode();
    }
}
