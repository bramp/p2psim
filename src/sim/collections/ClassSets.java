/*
 * Created on 12-Feb-2005
 */
package sim.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 *
 * @author Andrew Brampton
 */
public class ClassSets<E> implements SortedSet<E> {
	SortedSet<E> elements;
	Map<Class<? extends E>, Set<E>> elementsTypes;

	public ClassSets() {
		elements = new TreeSet<E>();
		elementsTypes = new Hashtable<Class<? extends E>, Set<E>>();
	}

	public ClassSets(Comparator<? super E> compare) {
		elements = new TreeSet<E>(compare);
		elementsTypes = new Hashtable<Class<? extends E>, Set<E>>();
	}

	/**
	 * Returns all the elements of this Class in the ClassLists
	 * @param cl
	 * @return
	 */
	public Set<? extends E> getType(final Class<?> cl) {
		Set<? extends E> s = elementsTypes.get(cl);

		if (s == null)
			s = new TreeSet<E>();

		return s;
	}

	/**
	 * Returns all the elements of this Class in the ClassLists including
	 * subclasses of this Class
	 * @param cl
	 * @return
	 */
	public Set<? extends E> getTypes(final Class<?> cl) {
		Set<E> v = new TreeSet<E>();

		Iterator<Map.Entry<Class<? extends E>, Set<E>>> i = elementsTypes.entrySet().iterator();

		while (i.hasNext()) {
			Map.Entry<Class<? extends E>, Set<E>> entry = i.next();
			Class<? extends E> key = entry.getKey();

			if (cl.isAssignableFrom(key))
				v.addAll(entry.getValue());
		}

		return v;
	}

	/**
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean add(final E o) {
		Class<? extends E> cl = (Class<? extends E>)o.getClass();
		Set<E> v = elementsTypes.get(cl);
		if (v == null) {
			v = new TreeSet<E>();
			elementsTypes.put(cl, v);
		}

		return elements.add(o) && v.add(o);
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean addAll(final Collection<? extends E> c) {
		boolean changed = false;
		Iterator<? extends E>i = c.iterator();

		while(i.hasNext()) {
			changed |= add(i.next());
		}

		return changed;
	}

	/**
	 *
	 */
	public void clear() {
		elements.clear();
		elementsTypes.clear();
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean contains(final Object o) {
		return elements.contains(o);
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean containsAll(final Collection<?> c) {
		return elements.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return elements.equals(obj);
	}

	/**
	 * @return
	 */
	public E first() {
		return elements.first();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 * @return
	 */
	public Iterator<E> iterator() {
		return elements.iterator();
	}

	/**
	 * @return
	 */
	public E last() {
		return elements.last();
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean remove(final Object o) {
		// remove from type listings
		Collection<E> v = elementsTypes.get(o.getClass());
		if (v == null) {return false;}

		return elements.remove(o) && v.remove(o);
	}

	/**
	 * @param c
	 * @return
	 */
	@SuppressWarnings(value={"unchecked"})
	public boolean removeAll(final Collection c) {
		boolean changed = false;
		Iterator<E> i = c.iterator();
		while(i.hasNext()) {
			E h = i.next();
			Collection<E> v = elementsTypes.get(h.getClass());
			changed |= v.remove(h);
		}

		return elements.removeAll(c) && changed;
	}

	/**
	 *
	 */
	public void removeAllElements() {
		elementsTypes.clear();
		elements.clear();
	}

	/**
	 * @param c
	 * @return
	 */
	public boolean retainAll(final Collection<?> c) {
		Iterator<E> i = elements.iterator();
		boolean changed = false;

		while (i.hasNext()) {
			E h = i.next();
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
		return elements.size();
	}

	/**
	 * @return
	 */
	public Object[] toArray() {
		return elements.toArray();
	}

	/**
	 * @param a
	 * @return
	 */
	@SuppressWarnings("hiding")
	public <Object> Object[] toArray(final Object[] a) {
		return elements.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return elements.toString();
	}

	public Comparator<? super E> comparator() {
		throw new RuntimeException("Not implemented yet");
	}

	public SortedSet<E> subSet(final E fromElement, final E toElement) {
		throw new RuntimeException("Not implemented yet");
	}

	public SortedSet<E> headSet(final E toElement) {
		throw new RuntimeException("Not implemented yet");
	}

	public SortedSet<E> tailSet(final E fromElement) {
		throw new RuntimeException("Not implemented yet");
	}
}
