/**
 *
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
import sim.net.router.Router;
import sim.net.topology.state.NodeReader;


/**
 * @author Andrew Brampton
 * @author Andy MacQuire
 *
 */
public class GTITMReader implements NodeReader {

    private String filename;

    public GTITMReader(String filename) {
        this.filename = filename;
    }

    public void load(NodeLoader loader) {
        LineNumberReader r;
        String line;
        String parts[];
		List<Host> nodes = new ArrayList<Host>();

        try {
            r = new LineNumberReader(new FileReader(filename));

			// useless definition line
			line = r.readLine();
	        //The first two values are: Number of nodes, Number of links
	        line = r.readLine();
	        parts = line.split("\\s");
        	int nCount = Integer.parseInt(parts[0]);
			// link count refers to simplex links, so divide by 2
        	int lCount = Integer.parseInt(parts[1]) / 2;

			// another definition line, and a blank line
			line = r.readLine();
			line = r.readLine();

        	//Loop past all the node info
        	while (nCount > 0) {
        		line = r.readLine();
        		parts = line.split("\\s");
        		String type = parts[1].substring(0, 1);
        		int address = Integer.parseInt(parts[0]);
        		nCount--;

				nodes.add(address, loader.createHost(type, address));
        	}

			// another blank line, and a definition line
			line = r.readLine();
			line = r.readLine();

        	while (lCount > 0) {
        		line = r.readLine();
        		parts = line.split("\\s");
            	int from = Integer.parseInt(parts[0]);
            	int to = Integer.parseInt(parts[1]);
            	int delay = Integer.parseInt(parts[2]);

        		lCount--;

				// make transit-transit links higher bandwidth
				if ((nodes.get(from) instanceof Router) && (nodes.get(to) instanceof Router)) {
					new NormalLink(nodes.get(from), nodes.get(to), NormalLink.BANDWIDTH_2048k, delay);
				}
				else {
					new NormalLink(nodes.get(from), nodes.get(to), NormalLink.BANDWIDTH_DEFAULT, delay);
				}

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
