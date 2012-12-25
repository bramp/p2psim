package sim.collections;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public class BinaryTree <T> implements SortedSet <T> {

	private TreeNode rootHolder = new TreeNode(null);
	private int size = 0;
	private TreeNode reserveNodes = null;
	private int reserveNodeCount = 0;
	private Comparator<? super T> comparator = null;

	private class TreeNode {
		TreeNode(T d) {
			data = d;
		}

		T data;
		TreeNode lhs;
		TreeNode rhs;
	}

	public BinaryTree() {}

	public BinaryTree(Comparator<? super T> comp) {
		comparator = comp;
	}

	/**
	 * toss out the tree and start anew.
	 *
	 * @author Steven Sinclair
	 */
	public void clear() {
		rootHolder.rhs = null;
		size = 0;
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @return boolean
	 * @param data
	 *            java.lang.Object
	 */
	public boolean contains(Object data) {
		return recursiveFindParent((T)data, rootHolder) != null;
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @param os
	 *            OutputStream
	 */
	public void dumpTree(OutputStream os) {
		Writer writer = new OutputStreamWriter(os);

		try {
			if (rootHolder.rhs == null)
				writer.write("<null tree>");
			else
				dumpTree(writer, rootHolder.rhs, 1);
			writer.flush();
		} catch (java.io.IOException ex) {
			System.err.println("tree dump: " + ex);
		}
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @param writer
	 *            java.io.Writer
	 * @param parent
	 *            TreeNode
	 * @param depth
	 *            int
	 */
	private void dumpTree(java.io.Writer writer, TreeNode curr, int depth) throws IOException {
		//if (curr == null)
		//	return;

		StringBuffer buf = new StringBuffer(depth);
		for (int i = 0; i < depth; ++i)
			buf.append("  ");

		if (curr == null) {
//			/writer.write(buf.toString() + "x");
		} else {
			dumpTree(writer, curr.rhs, depth + 1);
			writer.write(buf.toString() + curr.data + '\n');
			dumpTree(writer, curr.lhs, depth + 1);
		}
	}

	/**
	 * Sum of all available nodes and all used nodes.
	 *
	 * @author Steven Sinclair
	 * @return int
	 */
	public int getCapacity() {
		return size() + reserveNodeCount;
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @param data
	 *            java.lang.Object
	 */
	public boolean add(T data) {
		TreeNode node = newNode(data);
		if (rootHolder.rhs == null)
			rootHolder.rhs = node;
		else
			insertRecursive(node, rootHolder.rhs);
		++size;

		return true;
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @param node
	 *            TreeNode
	 */
	private void insertRecursive(TreeNode node, TreeNode curr) {
		if (compare(node.data, curr.data) < 0) {
			if (curr.lhs == null)
				curr.lhs = node;
			else
				insertRecursive(node, curr.lhs);
		} else {
			if (curr.rhs == null)
				curr.rhs = node;
			else
				insertRecursive(node, curr.rhs);
		}
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @return boolean
	 */
	private boolean isRoot(TreeNode node) {
		return node == rootHolder.rhs;
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @param args
	 *            java.lang.String[]
	 */
	public static void main(String args[]) {
		if (args.length == 0 || args[0].equals("-?") || args[0].equals("/?")) {
			System.out.println("Expected args: #nodesToCreate");
			return;
		}

		int numNodes = 10;
		try {
			numNodes = Integer.parseInt(args[0]);
		} catch (Throwable ex) {
		}

		System.out.println("Build a tree having " + numNodes + " nodes.");

		java.security.SecureRandom rnd = new java.security.SecureRandom();
		BinaryTree tree = new BinaryTree();

		List data = new ArrayList(numNodes);
		for (int i = 0; i < numNodes; ++i) {
			Integer integer = new Integer(Math.abs(rnd.nextInt() % 100000));
			tree.add(integer);
			data.addElement(integer);
			System.out.println("\tInserted " + integer);
		}

		System.out.println("Tree built of size " + tree.size());
		System.out.println("Tree dump:");
		tree.dumpTree(System.out);

		System.out.println("---------------");
		System.out.println("Find each element:");
		for (int i = 0; i < numNodes; ++i) {
			Object datum = data.elementAt(i);
			System.out.println("\ttree.contains(" + datum + ")? "
					+ tree.contains(datum));
		}
		System.out.println("Find elements which aren't in the tree:");
		System.out.println("\ttree.contains(-100)? "
				+ tree.contains(new Integer(-100)));
		System.out.println("\ttree.contains(-500)? "
				+ tree.contains(new Integer(-500)));

		System.out.println("----------------");
		System.out.println("Remove half of the elements (" + (numNodes / 2)
				+ "):");
		for (int i = 0; i < numNodes / 2; ++i) {
			Object datum = data.elementAt(i);
			System.out.println("Removing " + datum
					+ (tree.remove(datum) ? "" : "[failed]"));
			// tree.dumpTree(System.out);
			// System.out.println("");
		}
		System.out.println("Dump tree:");
		tree.dumpTree(System.out);

	}

    private int compare(T k1, T k2) {
        return (comparator==null ? ((Comparable</*-*/T>)k1).compareTo(k2)
                                 : comparator.compare((T)k1, (T)k2));
    }

	/**
	 *
	 * @author Steven Sinclair
	 * @return TreeNode
	 */
	private TreeNode newNode(T data) {
		if (reserveNodes != null) {
			TreeNode newNode = reserveNodes;
			reserveNodes = newNode.rhs;
			newNode.lhs = newNode.rhs = null;
			newNode.data = data;
			--reserveNodeCount;
			return newNode;
		} else
			return new TreeNode(data);
	}

	/**
	 * Remove any "reserve" nodes.
	 *
	 * @author Steven Sinclair
	 */
	public void pack() {
		reserveNodes = null;
	}

	/**
	 *
	 * This method assumes that curr.data has already been checked.
	 *
	 * @author Steven Sinclair
	 * @return TreeNode
	 * @param data
	 *            java.lang.Object
	 */
	protected TreeNode recursiveFindParent(T data, TreeNode curr) {
		if (!(curr == rootHolder)
				&& (data == null || compare(data, curr.data) < 0)) {
			if (curr.lhs == null)
				return null;
			else {
				if (curr.lhs.data == data)
					return curr;
				else
					return recursiveFindParent(data, curr.lhs);
			}
		} else {
			if (curr.rhs == null)
				return null;
			else {
				if (curr.rhs.data == data)
					return curr;
				else
					return recursiveFindParent(data, curr.rhs);
			}
		}
	}

	protected TreeNode recursiveFindAfter(T data, TreeNode curr) {
		if (!(curr == rootHolder)
				&& (data == null || compare(data, curr.data) < 0) ) {
			if (curr.lhs == null)
				return curr;
			else {
				if (curr.lhs.data == data)
					return curr;
				else
					return recursiveFindAfter(data, curr.lhs);
			}
		} else {
			if (curr.rhs == null)
				return curr;
			else {
				if (curr.rhs.data == data)
					return curr;
				else
					return recursiveFindAfter(data, curr.rhs);
			}
		}
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @return boolean
	 * @param data
	 *            java.lang.Object
	 */
	public boolean remove(Object o) {
		// SPS - this one's a little trickier.
		//

		T data = (T) o;

		if (rootHolder.rhs == null)
			return false;

		TreeNode parent = recursiveFindParent(data, rootHolder);
		if (parent == null)
			return false;
		boolean isLhs = parent == rootHolder ? false : compare(data, parent.data) <= 0;
		TreeNode reserve = isLhs ? parent.lhs : parent.rhs;
		removeNode(parent, isLhs);

		reserve.rhs = reserveNodes;
		reserveNodes = reserve;
		++reserveNodeCount;
		return true;
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @param parent
	 *            TreeNode
	 * @param isLhs
	 *            boolean
	 */
	private void removeNode(TreeNode parent, boolean isLhs) {
		TreeNode deathNode = isLhs ? parent.lhs : parent.rhs;

		System.out.println("[deathNode.data = " + deathNode.data + "]");

		TreeNode curr = deathNode.rhs;
		if (curr == null) {
			// There is no RHS, therefore make the root of the left subtree
			// the new element.
			//
			if (isLhs)
				parent.lhs = deathNode.lhs;
			else
				parent.rhs = deathNode.lhs;
		} else {
			if (curr.lhs == null) {
				// The right subtree has no left:
				// transfer deathNode's lhs to rst's root
				// and promote that node.
				//
				curr.lhs = deathNode.lhs;
				if (isLhs)
					parent.lhs = curr;
				else
					parent.rhs = curr;
			} else {
				// find the parent of the leftmost node of the rhs subtree:
				//
				while (curr.lhs.lhs != null)
					curr = curr.lhs;
				TreeNode rightLeftmostRight = curr.lhs.rhs;
				curr.lhs.rhs = deathNode.rhs;
				curr.lhs.lhs = deathNode.lhs;
				if (isLhs)
					parent.lhs = curr.lhs;
				else
					parent.rhs = curr.lhs;
				curr.lhs = rightLeftmostRight;
			}
		}
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @param size
	 *            int
	 */
	public void reserveCapacity(int size) {
		int newSize = size() - size;
		for (int i = 0; i < newSize; ++i) {
			TreeNode node = new TreeNode(null);
			node.rhs = reserveNodes;
			reserveNodes = node;
			++reserveNodeCount;
		}
	}

	/**
	 *
	 * @author Steven Sinclair
	 * @return int
	 */
	public int size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#comparator()
	 */
	public Comparator<? super T> comparator() {
		return comparator;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#subSet(E, E)
	 */
	public SortedSet<T> subSet(T fromElement, T toElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#headSet(E)
	 */
	public SortedSet<T> headSet(T toElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#tailSet(E)
	 */
	public SortedSet<T> tailSet(T fromElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#first()
	 */
	public T first() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#last()
	 */
	public T last() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Finds the object which would be directly after o
	 * @param o
	 * @return
	 */
	public T after(T o) {
		System.err.println(o);
		System.err.println(" " + recursiveFindAfter((T)o, rootHolder).data );
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	public Iterator<T> iterator() {
		throw new RuntimeException("Not implemented");
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
	public boolean addAll(Collection<? extends T> c) {
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

	public String toString() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		dumpTree(bytes);

		return bytes.toString();
	}
}
