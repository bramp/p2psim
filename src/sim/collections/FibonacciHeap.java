package sim.collections;

/*  This file is part of CivQuest.
 *
 *  CivQuest is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  CivQuest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CivQuest; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  $Id: FibonacciHeap.java,v 1.3 2004/10/21 17:23:40 wpausch Exp $
 */

import java.util.*;

/** Implements a fibonacci-heap. */
public class FibonacciHeap<E> implements SortedSet<E> {

	/** One node of the heap. */
	class FibonacciHeapNode<T> {
		FibonacciHeapNode<T> rightNeighbor = this;
		FibonacciHeapNode<T> child = null;
		T value;

		/** Number of children */
		int rank = 0;

		FibonacciHeapNode(T newValue) {
			value = newValue;
		}
	}

	/** The node that stores the smallest value of the whole heap */
	private FibonacciHeapNode<E> minNode = null;

	/** The comparator that forces the order of the values */
	private Comparator<? super E> comparator;

	/** Size of the heap */
	private int size = 0;

	@SuppressWarnings("unchecked")
	final int compare(E o1, E o2) {
		if (comparator == null)
			return ((Comparable) o1).compareTo(o2);
		else
			return comparator.compare(o1, o2);
	}

	/**
	 * Constructs a FibonacciHeap
	 *
	 * @param comparator
	 *            will be used for comparing the values
	 */
	public FibonacciHeap(Comparator<? super E> comparator) {
		this.comparator = comparator;
	}

	public FibonacciHeap() {}

	/**
	 * Returns the number of values in the heap
	 *
	 * @return the number of values in the heap
	 */
	public int size() {
		return size;
	}

	/**
	 * Helper-function for deleteMinValue. Updates the heap so that no two nodes
	 * of the root-list can have the same rank.
	 *
	 * @param start
	 *            first node to examine (inclusive)
	 * @param end
	 *            first node we don't want to examine
	 */
	@SuppressWarnings("unchecked")
	private void updateHeap(FibonacciHeapNode<E> start, FibonacciHeapNode<E> end) {
		// According to the book I have this heap from, no rank > this number is
		// possible
		int maxPossibleRank = (int) (Math.ceil(1.45 * Math.log(size) / Math.log(2)));
		FibonacciHeapNode<E>[] ranks = new FibonacciHeapNode[maxPossibleRank];
		for (int n = 0; n < ranks.length; n++) {
			ranks[n] = null;
		}

		// Insert nodes into ranks-array, combine them if necessary
		int maxRank = 0;
		FibonacciHeapNode<E> currNode = start;
		do {
			FibonacciHeapNode<E> nextNode = currNode.rightNeighbor;
			int currRank = insertNodeIntoRanksArray(currNode, ranks);
			if (currRank > maxRank) {
				maxRank = currRank;
			}
			currNode = nextNode;
		} while (currNode != end);

		int minRank;
		for (minRank = 0; minRank <= maxRank; minRank++) {
			if (ranks[minRank] != null) {
				break;
			}
		}

		ranks[maxRank].rightNeighbor = ranks[minRank];
		minNode = ranks[maxRank];

		// Set minNode and right neighbors
		int n = minRank;
		while (n < maxRank) {
			if (compare(ranks[n].value, minNode.value) < 0) {
				minNode = ranks[n];
			}
			for (int next = n + 1; next <= maxRank; next++) {
				if (ranks[next] != null) {
					ranks[n].rightNeighbor = ranks[next];
					n = next;
					break;
				}
			}
		}
	}

	/**
	 * Helper-function for updateHeap. Inserts a node into the ranks-array and
	 * combines it with other heap(s) if necessary.
	 *
	 * @param node
	 *            the node we want to insert
	 * @param ranks
	 *            the ranks-array as constructed in updateHeaps
	 */
	private int insertNodeIntoRanksArray(FibonacciHeapNode<E> node, FibonacciHeapNode<E>[] ranks) {
		while (true) {
			if (ranks[node.rank] == null) {
				// No other node with that rank exists, so we can insert it into
				// the ranks-array
				ranks[node.rank] = node;
				return node.rank;
			} else {
				// Another node with the same rank exists, so we combine them
				// and try to save them at the
				// appropriate index.
				if (compare(ranks[node.rank].value, node.value) < 0) {
					if (node.rank > 0) {
						node.rightNeighbor = ranks[node.rank].child.rightNeighbor;
						ranks[node.rank].child.rightNeighbor = node;
					} else {
						node.rightNeighbor = node;
						ranks[node.rank].child = node;
					}
					node = ranks[node.rank];
					ranks[node.rank] = null;
					node.rank++;
				} else {
					if (node.rank > 0) {
						ranks[node.rank].rightNeighbor = node.child.rightNeighbor;
						node.child.rightNeighbor = ranks[node.rank];
					} else {
						node.child = ranks[node.rank];
						node.child.rightNeighbor = node.child;
					}
					ranks[node.rank] = null;
					node.rank++;
				}
			}
		}
	}

	/** Links two (one-way)-cylcic lists, given by one node from every list. */
	private void linkCyclicLists(FibonacciHeapNode<E> node1, FibonacciHeapNode<E> node2) {
		FibonacciHeapNode<E> oldNode1RightNeighbor = node1.rightNeighbor;
		node1.rightNeighbor = node2.rightNeighbor;
		node2.rightNeighbor = oldNode1RightNeighbor;
	}

	/**
	 * Melds this Fibonacci-heap with another fibonacci-heap, given by its
	 * min-node. For a value in this heap, no value in the other heap with
	 * comparator.compare(thisHeapValue, otherHeapValue) == 0 is allowed. The
	 * resulting heap is stored in this heap. The other heap is DESTROYED. NOTE
	 * that size is NOT updated.
	 */
	private void meldWith(FibonacciHeapNode<E> otherMinNode) {
		linkCyclicLists(minNode, otherMinNode);

		if (compare(minNode.value, otherMinNode.value) > 0) {
			minNode = otherMinNode;
		}
	}

	public static void main(String[] args) {
		FibonacciHeap<Integer> heap = new FibonacciHeap<Integer>(null);

		Random r = new Random(0);

		for (int i = 0; i < 100000; i++) {
			Integer ii = new Integer( r.nextInt(1000) );
			//Integer ii = new Integer( i );
			heap.add(ii);
			//System.err.println("Added " + ii + " size:" + heap.size());
		}

		for (int i = 0; i < 100000; i++) {
			System.err.println(heap.removeFirst() + " size:" + heap.size());
		}

	}

	public Comparator<? super E> comparator() {
		return comparator;
	}

	public E first() {
		return minNode.value;
	}

	/**
	 * Removes the smallest value from the heap.
	 *
	 * @return the removed value
	 */
	public E removeFirst() {
		E retValue = minNode.value;

		if (minNode.rightNeighbor == minNode) {
			if (minNode.child == null) {
				// We delete the last node of the heap
				minNode = null;
			} else {
				// Heap has exactly one root, which now gets deleted
				minNode = minNode.child;
				// Now update the heap in order to have no rank twice (at
				// root-level)
				updateHeap(minNode, minNode);
			}
		} else {
			// Root-list has more than one element
			if (minNode.rank > 0) {
				// If we delete a element that has at least one child, include
				// all children into the
				// root-list
				FibonacciHeapNode<E> oldChildRightNeighbor = minNode.child.rightNeighbor;
				minNode.child.rightNeighbor = minNode.rightNeighbor.rightNeighbor;
				minNode.rightNeighbor.rightNeighbor = oldChildRightNeighbor;
			}
			// ... and update the heap in order to have to ranks twice at
			// root-level
			updateHeap(minNode.rightNeighbor, minNode);
		}

		size--;
		return retValue;
	}

	public boolean add(E arg0) {
		FibonacciHeapNode<E> newNode = new FibonacciHeapNode<E>(arg0);
		if (minNode == null) {
			minNode = newNode;
		} else {
			meldWith(newNode);
		}
		size++;

		return true;
	}

	public void clear() {
		minNode = null;
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public SortedSet<E> headSet(E arg0) {
		throw new RuntimeException("Not implemented");
	}

	public E last() {
		throw new RuntimeException("Not implemented");
	}

	public SortedSet<E> subSet(E arg0, E arg1) {
		throw new RuntimeException("Not implemented");
	}

	public SortedSet<E> tailSet(E arg0) {
		throw new RuntimeException("Not implemented");
	}

	public boolean addAll(Collection<? extends E> arg0) {
		throw new RuntimeException("Not implemented");
	}

	public boolean contains(Object arg0) {
		throw new RuntimeException("Not implemented");
	}

	public boolean containsAll(Collection<?> arg0) {
		throw new RuntimeException("Not implemented");
	}

	public Iterator<E> iterator() {
		throw new RuntimeException("Not implemented");
	}

	public boolean remove(Object arg0) {
		throw new RuntimeException("Not implemented");
	}

	public boolean removeAll(Collection<?> arg0) {
		throw new RuntimeException("Not implemented");
	}

	public boolean retainAll(Collection<?> arg0) {
		throw new RuntimeException("Not implemented");
	}

	public Object[] toArray() {
		throw new RuntimeException("Not implemented");
	}

	public <T> T[] toArray(T[] arg0) {
		throw new RuntimeException("Not implemented");
	}
}
