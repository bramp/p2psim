/*
 * Created on 12-Feb-2005
 */
package sim.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import sim.main.Global;
import sim.net.links.Link;


/**
 * A special class to store all the nodes, so that it can be serialised correctly
 * If a plain ArrayList is used then the JVM will crash when it tries to serialize the
 * complex graph of the network
 *
 * @author Andrew Brampton
 */
public class HostSet implements Set<Host>, Serializable, Disposable {
	SortedSet<Host> nodes;
	Map<Class<? extends Host>, Set<Host>> nodeTypes;

	public HostSet() {
		nodes = new TreeSet<Host>();
		nodeTypes = new Hashtable<Class<? extends Host>, Set<Host>>();
	}

	public HostSet(final Comparator<Host> c) {
		nodes = new TreeSet<Host>(c);
		nodeTypes = new Hashtable<Class<? extends Host>, Set<Host>>();
	}

	public HostSet getType(final Class<?> cl) {
		HostSet v = new HostSet();

		// return an empty set for a null class
		if (cl == null) {return v;}

		Iterator<Map.Entry<Class<? extends Host>, Set<Host>>> i = nodeTypes.entrySet().iterator();

		while (i.hasNext()) {
			Map.Entry<Class<? extends Host>, Set<Host>> entry = i.next();
			Class<? extends Host> key = entry.getKey();

			if (cl.isAssignableFrom(key))
				v.addAll(entry.getValue());
		}

		return v;
	}

	/**
	 * Returns the lowest addressed host
	 * @return
	 */
	public Host first() {
		return nodes.first();
	}

	/**
	 * Returns the highest addressed host
	 * @return
	 */
	public Host last() {
		return nodes.last();
	}

	/**
	 * Returns a random host from the HostList
	 * @return
	 */
	public Host getRandom() {
		if (isEmpty())
			return null;

		Iterator<Host> i = iterator();

		int rand = Global.rand.nextInt( size() );

		while (rand > 0) {
			rand--;
			i.next();
		}

		return i.next();
	}

	/*
	 * readObject and writeObject are implemented so we can force the writing of
	 * Nodes to disk first, and then read the links. Otherwise the jvm crashs because of the
	 * complex graph.
	 */
	@SuppressWarnings(value={"unchecked"})
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		nodes = (SortedSet<Host>) stream.readObject();

		Iterator<Host> i = nodes.iterator();
		while (i.hasNext()) {
			Host host = i.next();
			host.links = (List<Link>)stream.readObject();
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {

		stream.writeObject(nodes);

		Iterator<Host> i = nodes.iterator();
		while (i.hasNext()) {
			Host host = i.next();
			stream.writeObject(host.links);
		}
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean add(final Host o) {
		Class<? extends Host> cl = o.getClass();
		Set<Host> v = nodeTypes.get(cl);
		if (v == null) {
			v = new TreeSet<Host>();
			nodeTypes.put(cl, v);
		}

		return nodes.add(o) && v.add(o);
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean addAll(final Collection<? extends Host> c) {
		boolean changed = false;
		Iterator<? extends Host>i = c.iterator();

		while(i.hasNext()) {
			changed |= add(i.next());
		}

		return changed;
	}

	/**
	 *
	 */
	public void clear() {
		nodes.clear();
		nodeTypes.clear();
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean contains(final Object o) {
		return nodes.contains(o);
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean containsAll(final Collection<?> c) {
		return nodes.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return nodes.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return nodes.hashCode();
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	/**
	 * @return
	 */
	public Iterator<Host> iterator() {
		return nodes.iterator();
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean remove(final Object o) {
		// remove from type listings
		Collection<Host> v = nodeTypes.get(o.getClass());
		if (v == null)
			return false;

		return nodes.remove(o) && v.remove(o);
	}

	/**
	 * @param c
	 * @return
	 */
	@SuppressWarnings(value={"unchecked"})
	public boolean removeAll(final Collection c) {
		boolean changed = false;
		Iterator<Host> i = c.iterator();
		while(i.hasNext()) {
			Host h = i.next();
			Collection<Host> v = nodeTypes.get(h.getClass());
			changed |= v.remove(h);
		}

		return nodes.removeAll(c) && changed;
	}

	/**
	 *
	 */
	public void removeAllElements() {
		nodeTypes.clear();
		nodes.clear();
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean retainAll(final Collection<?> c) {
		Iterator<Host> i = nodes.iterator();
		boolean changed = false;

		while (i.hasNext()) {
			Host h = i.next();
			if (!c.contains(h)) {
				changed |= remove(h);
			}
		}

		return changed;
	}

	/**
	 * @return
	 */
	public int size() {
		return nodes.size();
	}

	/**
	 * @return
	 */
	public Object[] toArray() {
		return nodes.toArray();
	}

	/**
	 * @param a
	 * @return
	 */
	@SuppressWarnings("hiding")
	public <Host> Host[] toArray(final Host[] a) {
		return nodes.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return nodes.toString();
	}

	public Comparator<? super Host> comparator() {
		throw new RuntimeException("Not implemented yet");
	}

	public SortedSet<Host> subSet(final Host fromElement, final Host toElement) {
		throw new RuntimeException("Not implemented yet");
	}

	public SortedSet<Host> headSet(final Host toElement) {
		throw new RuntimeException("Not implemented yet");
	}

	public SortedSet<Host> tailSet(final Host fromElement) {
		throw new RuntimeException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see sim.net.Disposable#dispose()
	 */
	public void dispose() {
		Iterator<Host> i = nodes.iterator();
		while (i.hasNext()) {
			i.next().dispose();
		}
		clear();
	}

	public Host get(int address) {

		Iterator<Host> i = nodes.iterator();

		while (i.hasNext()) {
			Host h = i.next();
			if (address == h.getAddress()) {
				return h;
			}

			// If we have gone past the address
			//if (h.address > address)
			//	break;
		}

		return null;
	}

	public static void main(final String[] cliargs) throws Exception {
		HostSet h = new HostSet();

		h.add(new Host(0) {

			@Override
			public void recv(Link link, Packet p) {}

			@Override
			public void send(Packet p) throws RoutingException {}

		});

		h.add(new Host(1) {

			@Override
			public void recv(Link link, Packet p) {}

			@Override
			public void send(Packet p) throws RoutingException {}

		});
	}
}
