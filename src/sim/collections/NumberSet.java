/*
 * Created on 07-Mar-2005
 */
package sim.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import sim.main.Global;
import sim.net.overlay.dht.NodeAddressPair;


/**
 * @author Andrew Brampton
 */
public class NumberSet<T extends Number> implements Iterable<T>, Collection<T> {

	// TODO Why is this a SortedMap, not a SortedSet?
	protected SortedMap<T, T> set;

	public NumberSet() {
		set = new TreeMap<T, T>();
	}

	public NumberSet(final Comparator<T> compare) {
		set = new TreeMap<T, T>(compare);
	}

	/**
	 * Finds the T that represents the number ID
	 * If found T is returned, otherwise null
	 * @param ID
	 * @return
	 */
	public T find(final long ID) {
		return set.get(new NodeAddressPair(ID));
	}

	/**
	 * Finds the numerically closest peer
	 * @param ID
	 * @return
	 */
	public T findNumClosest(final long ID) {
		long diff = Long.MAX_VALUE;
		long bestDiff = Long.MAX_VALUE;

		T pair = null;
		T bestPair = null;

		Iterator<T> i = iterator();

		while (i.hasNext()) {
			pair = i.next();

			diff = Math.abs(pair.longValue() - ID);

			if (diff < bestDiff) {
				bestDiff = diff;
				bestPair = pair;
			}

		}

		return bestPair;
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean add(final T o) {

		if (Global.debug_extra_sanity) {
			if (o == null)
				throw new RuntimeException("Nulls not allowed");
		}

		if (set.put(o, o) == o)
			return false;

		return true;
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean addAll(final Collection<? extends T> c) {

		boolean ret = false;
		Iterator<? extends T> i = c.iterator();
		while (i.hasNext()) {
			ret |= add(i.next());
		}

		return ret;
	}


	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(final Collection<?> c) {
		boolean ret = false;
		Iterator<?> i = c.iterator();
		while (i.hasNext()) {
			ret |= remove(i.next());
		}

		return ret;
	}

	/**
	 *
	 */
	public void clear() {
		set.clear();
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean contains(final T o) {
		return set.containsKey(o);
	}

	/**
	 * @return
	 */
	public T first() {
		return set.firstKey();
	}

	/**
	 * @return
	 */
	public T last() {
		return set.lastKey();
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	/**
	 * @return
	 */
	public Iterator<T> iterator() {
		return set.keySet().iterator();
	}


	/**
	 * @param o
	 * @return
	 */
	public boolean remove(final T o) {
		return remove((Object)o);
	}


	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(final Object o) {
		return set.remove(o) != null;
	}

	/**
	 * @return
	 */
	public int size() {
		return set.size();
	}

	/**
	 * @return
	 */
	public T random() {

		if (isEmpty())
			return null;

		int idx = Global.rand.nextInt(set.size());

		Iterator<T> i = iterator();
		while (idx > 0) {
			i.next();
			idx--;
		}

		return i.next();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(final Object o) {
		return set.containsKey(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@SuppressWarnings("hiding")
	public <T> T[] toArray(final T[] a) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(final Collection<?> c) {
		throw new RuntimeException("Not implemented");
	}


	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(final Collection<?> c) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String toString() {
		return set.toString();
	}

}
