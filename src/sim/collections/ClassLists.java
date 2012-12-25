/*
 * Created on 12-Feb-2005
 */
package sim.collections;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.Map.Entry;


/**
 *
 * A list collection, that allows the user to select all the elements of a certain type
 *
 * @author Andrew Brampton
 */
public class ClassLists<E> implements List<E> {
	// TODO change this, when a elementsTypes' list is returned, and it is altered
	// the change is not reflected in elements
	List<E> elements;
	Map<Class<? extends E>, List<E>> elementsTypes;

	public ClassLists() {
		elements = new ArrayList<E>();
		elementsTypes = new Hashtable<Class<? extends E>, List<E>>();
	}

	/**
	 * Returns all the elements of this Class in the ClassLists
	 * @param cl
	 * @return
	 */
	public List<? extends E> getType(final Class<?> cl) {
		List<E> l = elementsTypes.get(cl);

		if (l == null)
			l = new ArrayList<E>();

		return l;
	}

	/**
	 * Returns all the elements of this Class in the ClassLists including
	 * subclasses of this Class
	 * @param cl
	 * @return
	 */
	public List<? extends E> getTypes(final Class<?> cl) {
		List<E> v = new ArrayList<E>();

		Iterator<Map.Entry<Class<? extends E>, List<E>>> i = elementsTypes.entrySet().iterator();

		while (i.hasNext()) {
			Map.Entry<Class<? extends E>, List<E>> entry = i.next();
			Class<? extends E> key = entry.getKey();

			if (cl.isAssignableFrom(key)) {
				v.addAll(entry.getValue());
			}
		}

		return v;
	}

	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	public int size() {
		return elements.size();
	}

	/* (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(final Object o) {
		return elements.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#iterator()
	 */
	public Iterator<E> iterator() {
		return elements.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return elements.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	public <T> T[] toArray(final T[] a) {
		return elements.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(E)
	 */
	public boolean add(final E o) {
		add(size(), o);

		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(final Collection<?> c) {
		return elements.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		throw new RuntimeException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new RuntimeException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		throw new RuntimeException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		throw new RuntimeException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	public void clear() {
		elements.clear();
		elementsTypes.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public E get(int index) {
		return elements.get(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#set(int, E)
	 */
	public E set(int index, E element) {
		throw new RuntimeException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(int, E)
	 */
	@SuppressWarnings("unchecked")
	public void add(int index, E e) {
		Class<? extends E> cl = (Class<? extends E>) e.getClass();
		List<E> v = elementsTypes.get(cl);
		if (v == null) {
			v = new ArrayList<E>();
			elementsTypes.put(cl, v);
		}

		v.add(e);
		elements.add(e);
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public E remove(int index) {
		E e = elements.remove(index);
		List<E> v = elementsTypes.get(e.getClass());
		v.remove(e);
		return e;
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(final Object o) {
		int idx = indexOf(o);
		if (idx == -1)
			return false;

		return remove(idx) == o;
	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<E> listIterator() {
		return elements.listIterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator(int index) {
		return elements.listIterator(index);
	}

	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	public List<E> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	public String toString() {
		Iterator<Entry<Class<? extends E>, List<E>>> i = elementsTypes.entrySet().iterator();
		String ret = "Total: " + elements.size() + ", ";

		while (i.hasNext()) {
			Entry<Class<? extends E>, List<E>> e = i.next();
			ret += e.getKey().toString();
			ret += ": ";
			ret += e.getValue().size();
			ret += ", ";
		}

		return ret;
	}
}
