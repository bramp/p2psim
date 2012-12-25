package sim.net.topology.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

public class HierGenerator {
	// USER VARIABLES
    // number of nodes per level
	private int[] hier_node_num = {10,20,40};
	// percentage probability of links between nodes on each layer
	private int[] node_prob = {25,100,100};
	// seed for random number generator
	private int seed = 1024;

	// GLOBAL VARIABLES
	private int node_num;								//total number of nodes
	private int link_num;								//number of node connections
	private Node[] node_list; 							//all nodes in topology

	private HierGenerator() {
		// discover total node count and create node array
		for (int i=0;i<hier_node_num.length;i++) {
			node_num += hier_node_num[i];
		}
		node_list = new Node[node_num];

		Trace.println(LogLevel.INFO, "Started...");
		// create node objects in the node_list array
		InitialiseNodes();
		// give nodes co-ordinates according to the level on which they exist
		// (allows for scaling)
		PlaceNodes();
		// connect nodes to each other according to given probabilities
		ConnectNodes();
		// sanity check
		CheckNodes();
		// output the topology to a file
		GenerateOutput();
		Trace.println(LogLevel.INFO, "Finished!");
	}

	private void InitialiseNodes() {
		String name = null;
		int curr_hier = 0;						// current level
		int curr_node = 0;						// current node on current level
		int next_hier = hier_node_num[0] - 1;	// next level boundary node

		// give nodes names in the format <level>.<node # for level>
		for(int i=0;i<node_num;i++) {
			name = curr_hier + "." + curr_node;
			node_list[i] = new Node(name,i);

			// check to see if we've moved to the next level
			if (i == next_hier && curr_hier++ != (hier_node_num.length - 1)) {
				curr_node = 0;
				next_hier += hier_node_num[curr_hier];
			}
			else {
				curr_node++;
			}
		}
	}

	private void PlaceNodes() {
		// TODO: implement co-ordinate system
	}

	private void ConnectNodes() {
		link_num = 0;

		int curr_hier = 0;						// current level
		int curr_node = 0;						// current node on current level
		int next_hier = hier_node_num[0] - 1;	// next level boundary node
		int last_hier = 0;						// previous level boundary node
		Random r = new Random(seed);

		// create connections between nodes on a given level
		for(int i=0;i<node_num;i++) {
			for (int j=0;j<hier_node_num[curr_hier];j++){
				if (r.nextInt(100) < node_prob[curr_hier]) {
					node_list[i].addLink(node_list[last_hier + j],Node.INTRA_LEVEL);
				}
			}

			// check to see if we've moved to the next level
			if (i == next_hier && curr_hier++ != (hier_node_num.length - 1)) {
				curr_node = 0;
				last_hier = next_hier + 1;
				next_hier += hier_node_num[curr_hier];
			}
			else {
				curr_node++;
			}
		}

		// create at least one inter-level link at level boundaries
		curr_node = 0;
		for (int i=0;i<hier_node_num.length - 1;i++){
			curr_node += hier_node_num[i];
			node_list[curr_node-1].addLink(node_list[(curr_node + hier_node_num[i+1])-1],
					                       Node.INTER_LEVEL);
		}
	}

	private void CheckNodes() {
		for(int i=0;i<node_num;i++) {
			if(!node_list[i].isConnected()) {
				CriticalError("Generated unconnected node at ID " + node_list[i].getID());
			}
		}
	}

	private void GenerateOutput() {
		BufferedWriter out = null;
		// try {out = new BufferedWriter(new FileWriter("hier-" + node_num));}
		try {out = new BufferedWriter(new FileWriter("hier"));}
		catch(IOException e) {CriticalError("Could not open output file!",e);}

		try {
			out.write("# [# of nodes][# of links]\n");
			out.write(node_num + "\t" + link_num + "\n");

			out.write("# NODE DEFINITIONS\n");
			out.write("# [node #][node name]\n");
			for(int i=0;i<node_num;i++) {
				out.write(node_list[i].getID() + "\t" + node_list[i].getName() + "\n");
			}

			out.write("# CONNECTION DEFINITIONS\n");
			out.write("# [source node][destination node]\n");
			for(int i=0;i<node_num;i++) {
				for (int j=0;j<node_list[i].getLinkCount();j++){
					out.write(i + "\t" + node_list[i].getLink(j).getID() + "\n");
				}
			}
		}
		catch(IOException e) {CriticalError("Error in writing to output file!",e);}

		try {out.close();}
		catch(IOException e) {CriticalError("Could not close output file!",e);}
	}

	private static void CriticalError(String reason) {
		// give up
		System.err.println("ERROR: " + reason);
		System.exit(1);
	}

	private static void CriticalError(String reason, Exception e) {
		System.err.println("******");
		e.printStackTrace();
		System.err.println("******");
		CriticalError(reason);
	}

	public static void main(String[] args) {
		// TODO: allow for file/command-line arguments for generator
		new HierGenerator();
	}

	private class Node {
		private String name;
		private int id;
		private List<Node> links;

		// use to keep track of intra/inter level links per node
		public static final int INTRA_LEVEL = 0;
		public static final int INTER_LEVEL = 1;
		private int intra;
		private int inter;

		public Node(String name, int id) {
			this.name = name;
			this.id = id;
			links = new ArrayList<Node>();
			intra = 0;
			inter = 0;
		}

		public String getName() {return name;}
		public int getID() {return id;}

		public void addLink(Node dest, int type) {
			// create a link between this and dest
			// (unless it already exists)
			if (!links.contains(dest)) {
				links.add(dest);
				// increment global link counter
				link_num++;

				// increment appropriate local link type counter
				switch(type) {
				case INTRA_LEVEL:
					intra++;
					break;
				case INTER_LEVEL:
					inter++;
					break;
				default:
					// should never happen
					HierGenerator.CriticalError("Unknown link type!");
				}
			}
		}

		public int getLinkCount() {
			return links.size();
		}

		public Node getLink(int i) {
			return links.get(i);
		}

		public boolean isConnected() {
			// use for sanity check (unconnected nodes are useless)
			return (inter != 0 || intra != 0);
		}
	}
}
