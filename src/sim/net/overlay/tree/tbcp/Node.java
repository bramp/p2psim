/*
 * Created on 22-Mar-2005
 * Based on TBCP
 * http://wiki.bramp.net:8080/buildinglowdelayapplicationlayermulticasttrees/files?get=paper05.pdf
 */
package sim.net.overlay.tree.tbcp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import sim.net.Packet;
import sim.net.PingPacket;
import sim.net.PongPacket;
import sim.net.RoutingException;
import sim.net.SimpleHost;
import sim.net.links.Link;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;


/**
 * @author Andrew Brampton
 */
public class Node extends SimpleHost {
	public final static int MaxOutDegree = 5;

	public class AddressPair implements Comparable<AddressPair> {
		int address;
		int delay = -1;

		public AddressPair(int address) {
			this.address = address;
		}

		public AddressPair(int address, int delay) {
			this.address = address;
			this.delay = delay;
		}

		public int compareTo(AddressPair o) {
			return address - o.address;
		}
	}

	/**
	 * Address of our parent
	 */
	int parent;

	int joinParent;

	/**
	 * Set containing all our children
	 */
	Set<AddressPair> children = new TreeSet<AddressPair>();

	/**
	 * Set containing all the possible parents
	 */
	Set<AddressPair> possibleParents = new TreeSet<AddressPair>();

	/**
	 * The number of pings we are waiting for
	 */
	int pingsSent = 0;

	/**
	 * @param address
	 */
	public Node(int address) {
		super(address);
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.Host#send(net.bramp.p2psim.Packet)
	 */
	@Override
	public void send(Packet p) {
		try {
			super.send(p);

			if (p.data instanceof Message)
				Trace.println(LogLevel.LOG1, "Sent: " + p.from + " > " + p.to + " " + p.data);
			else
				Trace.println(LogLevel.LOG1, "Sent: " + p.from + " > " + p.to + " " + p);

		} catch (RoutingException e) {
			Trace.println(LogLevel.WARN, this + ": WARNING " + e + " (" + p + ")");
		}
	}
	/* (non-Javadoc)
	 * @see net.bramp.p2psim.SimpleHost#recv(net.bramp.p2psim.Link, net.bramp.p2psim.Packet)
	 */
	@Override
	public void recv(Link link, Packet p) {
		if (p.data instanceof Message)
			Trace.println(LogLevel.LOG1, "Recv: " + p.from + " > " + p.to + " " + p.data);
		else
			Trace.println(LogLevel.LOG1, "Recv: " + p.from + " > " + p.to + " " + p);

		super.recv(link, p);

		// Someone is asking for our children
		if (p.data instanceof GetChildrenMessage) {

			ChildrenMessage c = new ChildrenMessage();
			c.children = new int[children.size()];

			//Place all our children into a array
			int child = 0;
			Iterator<AddressPair> i = children.iterator();
			while (i.hasNext()) {
				c.children[child] = i.next().address;
				child++;
			}

			p = Packet.newPacket(address, p.from, c);
			send(p);
		} else if (p.data instanceof ChildrenMessage) {
			ChildrenMessage msg = (ChildrenMessage)p.data;

			possibleParents.clear();
			possibleParents.add(new AddressPair(p.from));

			for (int child = 0; child < msg.children.length; child++) {
				//Add each possible parent
				possibleParents.add(new AddressPair(msg.children[child]));
			}

			//Loop sending pings to each parent
			for (Iterator<AddressPair> i=possibleParents.iterator(); i.hasNext(); ) {
				AddressPair pair = i.next();
				p = PingPacket.newPacket(address, pair.address);

				send(p);
				pingsSent++;
			}

		} else if (p instanceof PongPacket) {

			//Find the person who sent this in our possibleParents, and update the delay
			AddressPair pair = null;
			Iterator<AddressPair> i  = possibleParents.iterator();
			while (i!= null && i.hasNext()) {
				pair = i.next();
				if (pair.address == p.from) {
					pair.delay = ((PongPacket)p).getRoundTripTime();
					pingsSent--;
					i = null;
					break;
				}
			}

			// We didn't find out person :/
			if (i != null) {
				throw new RuntimeException("Received pong from " + p.from + " without sending ping");
			}

			// Check if we have received all our pongs
			if (pingsSent == 0) {
				//Send the results to the parent
				PingResultMessage results = new PingResultMessage();
				results.pairs = possibleParents;

				p = Packet.newPacket(address, joinParent, results);
				send(p);
			}
		} else if (p.data instanceof PingResultMessage) {
			PingResultMessage result = (PingResultMessage)p.data;

			//Find us in his results, so we can use his delay
			AddressPair us = getAddressFromSet(result.pairs, address);

			// Now we have some ping data we decide what to do
			if (children.size() < MaxOutDegree) {

				//If we have room, he can join us
				sendWelcome(p.from, us.delay);

			} else {
				//Otherwise do some scoring
				ScoreResult score = score(result.pairs);

				//If the address is us, welcome him
				if (score.joinUs) {
					sendWelcome(p.from, us.delay);

					//Tell our old child to move (to be under this new guy)
					sendGo(score.goAddy, p.from);

					//Remove the old child
					children.remove(new AddressPair(score.goAddy));
				} else {
					sendGo(p.from, score.goAddy);
				}
			}
		} else if (p.data instanceof WelcomeMessage) {
			//We have been invited, join them
			possibleParents.clear();
			parent = p.from;
		}  else if (p.data instanceof GoMessage) {
			// Try joining this new address
			join( ((GoMessage)p.data).gotoaddress );
		}
	}

	private class ScoreResult implements Comparable<ScoreResult> {
		public int goAddy = -1; /* Address that this node will join (if me, goAddy is filled) */
		public boolean joinUs = false;
		public int score;

		public ScoreResult(int goAddy, boolean joinUs, int score) {
			this.goAddy = goAddy;
			this.joinUs = joinUs;
			this.score = score;
		}

		public int compareTo(ScoreResult o) {
			return score - o.score;
		}
	}

	/* Returns the address of the node of who this joining peer should join
	 * It may return our own address,
	 * */
	protected ScoreResult score(Set<AddressPair> delays) {
		SortedSet<ScoreResult> results = new TreeSet<ScoreResult>();
		AddressPair p;
		int total = 0;
		int childrenTotal = 0;
		HashMap<Integer, Integer> delay = new HashMap<Integer, Integer>();

		//Make a TreeMap of delays, for easy address->delay access
		Iterator<AddressPair> i = delays.iterator();
		while (i.hasNext()) {
			AddressPair pair = i.next();
			delay.put(pair.address, pair.delay);
		}

		//Work out the total delay to just our children
		i = children.iterator();
		while (i.hasNext()) {
			childrenTotal += i.next().delay;
		}

		//First check the (new node joins our children)
		i = children.iterator();
		while (i.hasNext()) {
			p = i.next();
			total = childrenTotal;

			total += (delay.get(p.address)).intValue();

			results.add( new ScoreResult(p.address, false, total) );
		}

		//Check the new node replaces our children (and move child below new node)
		i = children.iterator();
		while (i.hasNext()) {
			p = i.next();

			//Distance from root to newcomer
			total = (delay.get( address )).intValue();

			Iterator<AddressPair> ii = children.iterator();
			while (ii.hasNext()) {
				AddressPair p2 = ii.next();
				//Distance from root to all other children but p.address
				if (p2.address != p.address) {
					total += p2.delay;
				}
			}

			//Now add the replaced node to new code distance
			total += (delay.get( p.address )).intValue();

			results.add( new ScoreResult(p.address, false, total) );
		}

		//TODO Add the last check - I think we are missing atleast one delay (maybe cache needed?)
		//Check the new node replace our children (and move child to another child)

		return results.first();
	}

	void sendGo(int address, int gotoaddress) {
		GoMessage go = new GoMessage();
		go.gotoaddress = gotoaddress;
		send(Packet.newPacket(this.address, address, go));
	}

	void sendWelcome(int address, int delay) {
		children.add(new AddressPair(address, delay));
		send(Packet.newPacket(this.address, address, new WelcomeMessage()));
	}

	AddressPair getAddressFromSet(Set<AddressPair> set, int address) {
		Iterator<AddressPair> i = set.iterator();
		while (i.hasNext()) {
			AddressPair pair = i.next();
			if (pair.address == address)
				return pair;
		}
		return null;
	}

	/**
	 * Joins the tree starting at rendezvousNode
	 * @param rendezvousAddress
	 * @throws RoutingException
	 */
	public void join(int rendezvousAddress) {
		Packet p = Packet.newPacket(address, rendezvousAddress, new GetChildrenMessage());
		joinParent = rendezvousAddress;
		send(p);
	}
	/**
	 * @return Returns the parent.
	 */
	public int getParent() {
		return parent;
	}
}
