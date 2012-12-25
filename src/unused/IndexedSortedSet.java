package sim.collections;


import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class IndexedSortedSet<E extends LinkListItem> implements SortedSet<E> {

	protected int size = 0;
	protected Comparator<? super E> comparator = null;
	protected TreeSet2<E> index;
	protected E first = null;
	protected E last = null;

	public IndexedSortedSet() {
		index = new TreeSet2<E>();
	}

	public IndexedSortedSet(Comparator<? super E> comparator) {
		this.comparator = comparator;
		index = new TreeSet2<E>(comparator);
	}

    /**
     * Compares two keys using the correct comparison method
     */
    private int compare(E k1, E k2) {
        return (comparator==null ? ((Comparable</*-*/E>)k1).compareTo(k2)
                                 : comparator.compare((E)k1, (E)k2));
    }

	/* (non-Javadoc)
	 * @see java.util.SortedSet#comparator()
	 */
	public Comparator<? super E> comparator() {
		return comparator;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#first()
	 */
	public E first() {
		return first;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#last()
	 */
	public E last() {
		return last;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	public int size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return index.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		size = 0;
		first = null;
		last = null;
		index.clear();
	}

	private class OurIterator implements Iterator<E> {

		LinkListItem current;

		public OurIterator(E first) {
			current = first;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return current != null;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public E next() {
			LinkListItem next = current;
			current = current.nextItem;
			return (E)next;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public String toString() {
		Iterator<E> i = iterator();
		String ret = "";

		ret = index.toString() + "\n";

		while (i.hasNext())
			ret+= i.next() + ", ";

		return ret;
	}

	private E after(E o) {
		/*
		SortedSet<E> tail;
		tail = index.tailSet(o);

		Iterator<E> i = tail.iterator();
		while (i.hasNext()) {
			E o2 = i.next();
			if (compare(o, o2) != 0)
				return o2;
		}

		return null;
		*/
		return index.after(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#add(E)
	 */
	public boolean add(E o) {

		// Find where in the linked list this item should go

		E after = after(o);

		// There is nothing greater than o, so put it at the back
		if (after == null) {
			o.nextItem = null;
			o.prevItem = last;

			if (last != null)
				last.nextItem = o;

			last = o;

			if (first == null)
				first = o;
		} else {
			o.prevItem = after.prevItem;

			if (o.prevItem != null)
				o.prevItem.nextItem = o;

			o.nextItem = after;
			after.prevItem = o;

			// If the guy after us was first, make us first
			if (after == first)
				first = o;

			if (last == null)
				last = o;
		}

		index.add(o);
		size++;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		if (o != first)
			throw new RuntimeException("Can only remove from the front");

		if ( !isEmpty() ) {
			first = (E) first.nextItem;
			size--;

			E indexFirst = index.first();

			// Check if we need to remove the index
			if (first == null || compare(first, indexFirst) > 0)
				index.remove(indexFirst);

			if ( isEmpty() )
				last = null;
		}

		return true;
	}

	private static class IntegerItem extends LinkListItem implements Comparable {
		public int i;

		public IntegerItem(int i) {
			this.i = i;
		}

		public String toString() {
			return "" + i;
		}

		public int compareTo(Object o) {
			return i - ((IntegerItem)o).i;
		}
	}

	public static void main(String args[]) {

		IndexedSortedSet<LinkListItem> e = new IndexedSortedSet<LinkListItem>();

		e.add(new IntegerItem(20400));
		e.remove(e.first());

		e.add(new IntegerItem(20400));
		e.add(new IntegerItem(20408));
		e.add(new IntegerItem(20408));
		e.add(new IntegerItem(30000));
		e.add(new IntegerItem(20403));

		System.out.println("---------------\n" + e.toString());

		e.remove(e.first());
		System.out.println("---------------\n" + e.toString());

		e.remove(e.first());
		System.out.println("---------------\n" + e.toString());
		e.remove(e.first());
		System.out.println("---------------\n" + e.toString());
		e.remove(e.first());
		System.out.println("---------------\n" + e.toString());
		e.remove(e.first());
		System.out.println("---------------\n" + e.toString());
		e.remove(e.first());
		System.out.println("---------------\n" + e.toString());
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#subSet(E, E)
	 */
	public SortedSet<E> subSet(E fromElement, E toElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#headSet(E)
	 */
	public SortedSet<E> headSet(E toElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#tailSet(E)
	 */
	public SortedSet<E> tailSet(E fromElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	public Iterator<E> iterator() {
		return new OurIterator(first);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		throw new RuntimeException("Not implemented");
	}

}
