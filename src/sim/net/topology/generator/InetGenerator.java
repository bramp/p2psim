/*
 * Created on 12-Feb-2005
 */
package sim.net.topology.generator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * Java implementation of the "Inet Topology Generator" 3.0 Generator
 * Re-implemented so additional features could be added ;)
 * @author Andrew Brampton
 */
public class InetGenerator {

	/**
	 * Number of nodes in the generated graph
	 */
	public int node_num = 250;

	/**
	 * Plane dimension
	 */
	public int grid_size = 10000;

	/**
	 * Number of degree one nodes (or Peers)
	 */
	public int degree_one = 9 * node_num / 10; // 90% Nodes

	public int seed = 0;

	private Random rnd;

	private class node_type {
		  int nid; /* node id */
		  int degree; /* node degree */
		  int free_degree; /* unconnected node degree */
		  int x, y; /* x, y coordinate */
		  node_type[] nnp;
	}

	/********************************************************************************/
	/* complementary cumulative distribution function of degree frequency vs. degree*/
	/*(Eq. 1 in the TR)   \bar{F}(d) = e^c * d^(a*t + b)                            */
	/********************************************************************************/

	final static double a = 0.00324;
	final static double b = -1.223;
	final static double c = -0.711;

	/***********************************/
	/* degree vs. rank growth powerlaw */
	/* d = exp(p*t + q) * r^R          */
	/***********************************/
	final static double R = -0.87;
	final static double p = 0.0228;
	final static double q = 6.5285;
	final static double DR_Frac = 0.02; /* the fraction of nodes this law applies to */

	/*************************************/
	/* pairsize within h hops growth law */
	/* P(t,h) = exp(s(h)*t)*P(0,h)       */
	/*                                   */
	/* therefore,                        */
	/* P(t,0) = exp(s(0)*t)*P(0,0)       */
	/* t is computed as:                 */
	/*   log(P(t,0)/P(0,0))/s(0)         */
	/*************************************/
	final static int P00 =  3037;   /* P(0,0) */
	final static double s0 = 0.0281;   /* s(0) */

	public void generate_degrees(node_type[] node, int node_num, int degree_one) {
		/* calculate what month  (how many months since nov. '97) */
		/*  corresponds to this topology of size node_num.       */
		double t = Math.log((double)node_num/(double)P00)/s0;

		/* using t calculated above, compute the exponent of the ccdf power law */
		double ccdfSlope = (a * t) + b;

		int node_num_no1 = node_num - degree_one;
		int nodes = 0;
		int i;

		/***************************/
		/* generate degree 1 nodes */
		/***************************/
		for (i = node_num - 1; i >= node_num_no1; --i) {
			node[i] = new node_type();
			node[i].nid = i;
		    node[i].degree = 1;
		    node[i].free_degree = 1;
		}

		nodes += degree_one;

		// for each degree see if we need to add any of it to obtain the appropriate value on the ccdf
		int degree = 2;
		double ccdfIdeal;
		double ccdfActual;
		double diff;
		int numNodesToAdd;

		while (nodes <= node_num && degree < node_num)
		{
			// this is where we should be at
			ccdfIdeal = Math.exp(c) * Math.pow(degree,ccdfSlope);
			//this is were we are before adding nodes of this degree
			ccdfActual = (1.0 - ((double)nodes/(double)node_num));

			//calc how many nodes of this degree we need to add to get the ccdf right
			diff = ccdfActual - ccdfIdeal;
			if (diff * node_num > 1.0)   // we actually need to add a node at this degree
			{
				numNodesToAdd = (int)(diff * node_num);
				for (i = node_num - nodes-1; i >= node_num - nodes - numNodesToAdd; --i)
				{
					node[i] = new node_type();
					node[i].nid = i;
					node[i].degree = degree;
					node[i].free_degree = degree;
				}
				nodes += numNodesToAdd;
			}
			++degree;
		}

		int rank;
		/* use the degree-rank relationship to generate the top 3 degrees */
		for(i=0; i<3; ++i)
		{
			node[i] = new node_type();
			node[i].nid = i;
			rank = i + 1;
			node[i].degree = (int) (Math.pow((rank), R)*Math.exp(q) * Math.pow(((double)node_num/(double)P00), (p/s0)));
			node[i].free_degree = node[i].degree;
		}

		//qsort(node, node_num, sizeof(node_type), degree_compare);
		Arrays.sort(node, new Comparator<node_type>() {
			public int compare(node_type arg0, node_type arg1) {
				node_type n1 = arg0;
				node_type n2 = arg1;

				return n2.degree - n1.degree;
			}
		});

		/************************************/
		/* the sum of node degrees can't be */
		/* odd -- edges come in pairs       */
		/************************************/
		int edges;
		for(i=0,edges=0;i<node_num;++i)
			edges += node[i].degree;
		if ( edges % 2 == 1 )
		{
			node[0].degree++;
			node[0].free_degree++;
		}

		for (i=0; i<node_num; ++i)
			node[i].nid = i;
	}

	/* lower and upper bounds, inclusively */
	public int random_uniform_int(int a, int b)
	{
		double b2;

		b2 = (double)b - a + 1.0;
		b2 = b2 * rnd.nextDouble() - 0.5;

		return(a + (int)Math.rint(b2));
	}

	/***************************************/
	/* randomly placed nodes inside a grid */
	/***************************************/
	public void place_nodes(node_type[] node, int node_num, int grid_size, int placement) {
		int i;

		for (i=0; i<node_num; ++i)
		{
			node[i].x = random_uniform_int(0, grid_size);
			node[i].y = random_uniform_int(0, grid_size);
		}
	}

	/***************************************************************************/
	/* function that tests the connectability of the given degree distribution */
	/***************************************************************************/
	boolean is_graph_connectable(node_type[] node, int node_num)
	{
	  int i;
	  int F_star, F = 0, degree_one = 0;

	  for (i=0; i<node_num; ++i)
	  {
	    if (node[i].degree == 1)
	      ++degree_one;
	    else
	      F += node[i].degree;
	  }

	  F_star = F - 2*(node_num - degree_one - 1);
	  if (F_star < degree_one)
	    return false;

	  return true;
	}

	public int posOf(node_type[] nodes, node_type node) {
		int i;
		for (i = 0; i < nodes.length; i++) {
			if (nodes[i] == node)
				return i;
		}
		return -1;
	}

	public void connect_nodes(node_type[] node, int node_num)
	{

		int i, j, k, degree_g2;
		int G[], L[], G_num, L_num, g, l;
		int p_array[], p_array_num, id[];
		boolean flag[];


		int added_node;
		int weighted_degree_array[][];
		double distance;
		int freqArray[];

		/************************/
		/* satisfaction testing */
		/************************/
		if (!is_graph_connectable(node, node_num))
		{
			System.err.println("Warning: not possible to generate a connected graph with this degree distribution!");
		}

		p_array_num = 0;
		degree_g2 = 0;
		for (i=0; i<node_num; ++i)
		{
			node[i].nnp = new node_type[node[i].degree];

			for (j=0; j<node[i].degree; ++j)
				node[i].nnp[j] = null;

			/* the probability array needs be of size = the sum of all degrees of nodes that have degrees >= 2 */
			if (node[i].degree > 1)
				p_array_num += node[i].degree;

			/* set the position of the first node of degree 1 */
			if (node[i].degree == 1 && node[i-1].degree != 1)
				degree_g2 = i;
		}

		G = new int[degree_g2];
		L = new int[degree_g2];

		/* we need to allocate more memory than just p_array_num because of our added weights       */
		/* 40 is an arbitrary number, probably much higher than it needs to be, but just being safe */
		p_array = new int[p_array_num*40];

		id = new int[degree_g2];
		flag = new boolean[degree_g2];

		/* weighted_degree_array is a matrix corresponding to the weight that we multiply  */
		/* the standard proportional probability by. so weighted_degree_array[i][j] is the */
		/* value of the weight in P(i,j). the matrix is topDegree x topDegree in size,     */
		/* where topDegree is just the degree of the node withh the highest outdegree.     */
		weighted_degree_array = new int[node[0].degree + 1][];

		for (i = 0; i <= node[0].degree; ++i)
		{
			weighted_degree_array[i] = new int[node[0].degree + 1];
		}

		/* determine how many nodes of a given degree there are.  */
		freqArray = new int[node[0].degree +1];

		// fill the freq array
		for (i = 0; i <= node[0].degree; ++i)
			freqArray[i] = 0;

		for (i = 0; i < node_num; ++i)
			freqArray[node[i].degree]++;


		/* using the frequency-degree relationship, calculate weighted_degree_array[i][j] for a all i,j */
		for (i = 1; i <= node[0].degree; ++i)
		{
			for (j = 1; j <=  node[0].degree; ++j)
			{
				if ((freqArray[i] > 0) && (freqArray[j] > 0)) // if these degrees are present in the graph
				{
					distance = Math.pow(( Math.pow( Math.log((double)i/(double)j), 2.0) + Math.pow( Math.log((double)freqArray[i]/(double)freqArray[j]), 2.0)), .5);
					if (distance < 1)
						distance = 1;

					weighted_degree_array[i][j] = (int)( distance/2 * j);
				}
			}
		}

		/********************************/
		/* randomize the order of nodes */
		/********************************/
		for (i=0; i<degree_g2; ++i)
			id[i] = i;

		i = degree_g2;
		while (i>0)
		{
			j = random_uniform_int(0, i-1); /* j is the index to the id array! */
			L[degree_g2 - i] = id[j];

			for (; j<i-1; ++j)
				id[j] = id[j+1];
			--i;
		}

		/* do not randomize the order of nodes as was done in Inet-2.2. */
		/* we just want to build the spanning tree by adding nodes in   */
		/* in monotonically decreasing order of node degree             */
		for(i=0;i<degree_g2;++i)
			L[i] = i;

		/************************************************/
		/* Phase 1: building a connected spanning graph */
		/************************************************/
		G_num = 1;
		G[0] = L[0];
		L_num = degree_g2 - 1;

		while (L_num > 0)
		{
			j = L[1];
			added_node = j;

			/******************************/
			/* fill the probability array */
			/******************************/
			l = 0;
			for (i=0; i<G_num; ++i)
			{
				if (node[G[i]].free_degree > 0)
				{
					if (node[G[i]].free_degree > node[G[i]].degree)
					{
						System.err.println("connect_nodes: problem, node " + G[i] + "(nid=" +  node[G[i]].nid + "), free_degree = " + node[G[i]].free_degree + ", degree = " + node[G[i]].degree + ", exiting...");
						System.exit(-1);
					}

					for(j=0; j < weighted_degree_array[ node[added_node].degree ][ node[G[i]].degree ]; ++j, ++l)
						p_array[l] = G[i];
				}
			}

			/**************************************************************/
			/* select a node randomly according to its degree proportions */
			/**************************************************************/
			g = random_uniform_int(0, l-1); /* g is the index to the p_array */

			/*****************************************************/
			/* redirect -- i and j are indices to the node array */
			/*****************************************************/
			i = p_array[g];
			j = added_node;

			/*if (node[i].nid == 0)
			 fprintf(stderr, "phase I: added node %d\n", node[j].nid);*/

			node[i].nnp[node[i].degree - node[i].free_degree] = node[j];
			node[j].nnp[node[j].degree - node[j].free_degree] = node[i];

			/* add l to G and remove from L */
			G[G_num] = j;
			for (l=1; l < L_num; ++l)
				L[l] = L[l+1];

			--(node[i].free_degree);
			--(node[j].free_degree);
			++G_num;
			--L_num;
		}

		// fprintf(stderr, "DONE!!\n");
		/*************************************************************************/
		/* Phase II: connect degree 1 nodes proportionally to the spanning graph */
		/*************************************************************************/
		for (i=degree_g2; i<node_num; ++i)
		{
			/******************************/
			/* fill the probability array */
			/******************************/
			l = 0;
			for (j=0; j<degree_g2; ++j)
			{
				if (node[j].free_degree > 0)
				{
					for (k = 0; k < weighted_degree_array[ 1 ][ node[j].degree ]; ++k, ++l)
						p_array[l] = j;
				}
			}

			g = random_uniform_int( 0, l-1); /* g is the index to the p_array */
			j = p_array[g];

			node[i].nnp[node[i].degree - node[i].free_degree] = node[j];
			node[j].nnp[node[j].degree - node[j].free_degree] = node[i];

			--(node[i].free_degree);
			--(node[j].free_degree);
		}

		// fprintf(stderr, "DONE!!\n");
		/**********************************************************/
		/* Phase III: garbage collection to fill all free degrees */
		/**********************************************************/
		for (i=0; i<degree_g2; ++i)
		{
			for (j=i+1; j<degree_g2; ++j)
				flag[j] = true;
			flag[i] = false; /* no connection to itself */

			/********************************************************************/
			/* well, we also must eliminate all nodes that i is already         */
			/* connected to. bug reported by shi.zhou@elec.qmul.ac.uk on 8/6/01 */
			/********************************************************************/
			for (j = 0; j < (node[i].degree - node[i].free_degree); ++j)
				if ( posOf(node, node[i].nnp[j]) < degree_g2 )
					flag[ posOf(node, node[i].nnp[j]) ] = false;

			if ( node[i].nnp[0] == null )
			{
				System.err.println("i = " + i + ", degree = " +  node[i].degree + ", free_degree = " + node[i].free_degree + ", node[i].npp[0] null!");
				System.exit(-1);
			}

			flag[posOf(node, node[i].nnp[0])] = false; /* no connection to its first neighbor */

			if (node[i].free_degree < 0)
			{
				System.err.println("we have a problem, node " + i + " free_degree " + node[i].free_degree + "!");
				System.exit(-1);
			}

			while (node[i].free_degree > 0)
			{
				/******************************/
				/* fill the probability array */
				/******************************/
				l = 0;
				for (j=i+1; j<degree_g2; ++j)
				{
					if (node[j].free_degree > 0 && flag[j])
					{
						for (k = 0; k < weighted_degree_array[ node[i].degree ][ node[j].degree ]; ++k, ++l)
							p_array[l] = j;
					}
				}

				if (l == 0)
					break;

				g = random_uniform_int(0, l-1); /* g is the index to the p_array */
				j = p_array[g];

				/*if ( node[i].nid == 0 )
				 fprintf(stderr, "phase III: added node %d!\n", node[j].nid);*/

				node[i].nnp[node[i].degree - node[i].free_degree] = node[j];
				node[j].nnp[node[j].degree - node[j].free_degree] = node[i];

				--(node[i].free_degree);
				--(node[j].free_degree);

				flag[j] = false;
			}

			if (node[i].free_degree > 0)
			{
				System.err.println("connect_nodes: node " + node[i].nid + " has degree of " + node[i].degree + " with " + node[i].free_degree + " unfilled!");
			}
		}
	}

	int euclidean(node_type n1p, node_type n2p)
	{
		float x1, x2, y1, y2, dist;

		x1 = n1p.x; x2 = n2p.x; y1 = n1p.y; y2 = n2p.y;
		dist = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);

		return (int) (Math.pow(dist, .5));
	}

	void generate_output(node_type np[], int node_num)
	{

		int i, j, dist;
		int link_num = 0;

		for (i=0; i<node_num; ++i)
		{
			link_num += np[i].degree - np[i].free_degree;
			np[i].degree -= np[i].free_degree;
		}

		if ((link_num % 2) != 0)
		{
			System.err.println("generate_output: error, total outdegree is odd!");
			System.exit(-1);
		}

		Trace.println(LogLevel.INFO, node_num + " " + link_num/2);

		for (i=0; i<node_num; ++i) {
			if (np[i].degree == 1)
				System.out.print("N\t"); //N for Node
			else
				System.out.print("R\t"); //R for Router

			Trace.println(LogLevel.INFO, np[i].nid + "\t" + np[i].x + "\t" + np[i].y);
		}

		for (i=0; i<node_num; ++i)
		{
			for(j=0; j<np[i].degree; ++j)
			{
				if (np[i].nnp[j] != null && np[i].nnp[j].nid > np[i].nid)
					/* output one of the two links assuming symmetry */
					{

					dist = euclidean(np[i], np[i].nnp[j]);
					Trace.println(LogLevel.INFO, np[i].nid + "\t" + np[i].nnp[j].nid + "\t" + dist);

					}
			}
		}
	}

	public InetGenerator() {
		node_type[] node = new node_type[node_num];
		int placement = 0;

		rnd = new Random(seed);

		generate_degrees(node, node_num, degree_one);
		place_nodes(node, node_num, grid_size, placement);
		connect_nodes(node, node_num);
		generate_output(node, node_num);

	}

	public static void main(String[] args) {
		new InetGenerator();
	}
}
