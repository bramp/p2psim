/*
 * Created on 12-Feb-2005
 */
package sim.net;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import sim.main.Global;
import sim.net.overlay.dht.pastry.Peer;

/**
 * A special class to store all the nodes, so that it can be serialised correctly
 * If a plain Vector is used then the JVM will crash when it tries to serialize the
 * complex graph of the network
 *
 * @author Andrew Brampton
 */
public class HostList extends ArrayList<Host> implements List<Host>, Serializable, Disposable {
	Map<Class<? extends Host>, List<Host>> nodeTypes;

	private class ClassComparator implements Comparator<Class<? extends Host>> {
		public int compare(Class<? extends Host> o1, Class<? extends Host> o2) {
			return o1.hashCode() - o2.hashCode();
		}
	}

	public HostList() {
		super();
		nodeTypes = new TreeMap<Class<? extends Host>, List<Host>>(new ClassComparator());
	}

	/* (non-Javadoc)
	 * @see sim.net.Disposable#dispose()
	 */
	public void dispose() {
		clear();
	}

	public HostList getType(Class<?> cl) {
		HostList v = new HostList();

		Iterator<Map.Entry<Class<? extends Host>, List<Host>>> i = nodeTypes.entrySet().iterator();

		while (i.hasNext()) {
			Map.Entry<Class<? extends Host>, List<Host>> entry = i.next();
			Class<? extends Host> key = entry.getKey();

			if (cl.isAssignableFrom(key))
				v.addAll(entry.getValue());
		}

		return v;
	}

	/**
	 * @param o
	 * @return True if the Host was added
	 */
	@Override
	public boolean add(Host o) {
		Class<? extends Host> cl = o.getClass();
		List<Host> v = nodeTypes.get(cl);
		if (v == null) {
			v = new ArrayList<Host>();
			nodeTypes.put(cl, v);
		}

		return super.add(o) && v.add(o);
	}

	/**
	 * @param c
	 * @return True if atleast one host was added
	 */
	@Override
	public boolean addAll(Collection<? extends Host> c) {
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
	@Override
	public void clear() {
		super.clear();
		nodeTypes.clear();
	}

	/**
	 * @param o
	 * @return
	 */
	@Override
	public boolean remove(Object o) {
		// remove from type listings
		Collection<Host> v = nodeTypes.get(o.getClass());
		if (v == null) {
			return false;
		}

		return super.remove(o) && v.remove(o);
	}

	/**
	 * @return  a random host from the HostList or Null if the set is empty
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

	public static void main(String[] cliargs) throws Exception {
		HostList h = new HostList();

		Host h1 = new Peer(0);
		Host h2 = new Peer(1);

		h.add(h1);
		h.add(h2);
	}
}
