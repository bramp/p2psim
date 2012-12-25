package sim.collections;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * This is the Vector.java source code, but changed to not be a collection of objects, but instead
 * be a collection of ints. Lots of stuff was hacked out or replaced
 */

/*
 * @(#)Vector.java	1.96 04/02/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * The <code>Vector</code> class implements a growable array of
 * objects. Like an array, it contains components that can be
 * accessed using an integer index. However, the size of a
 * <code>Vector</code> can grow or shrink as needed to accommodate
 * adding and removing items after the <code>Vector</code> has been created.<p>
 *
 * Each vector tries to optimize storage management by maintaining a
 * <code>capacity</code> and a <code>capacityIncrement</code>. The
 * <code>capacity</code> is always at least as large as the vector
 * size; it is usually larger because as components are added to the
 * vector, the vector's storage increases in chunks the size of
 * <code>capacityIncrement</code>. An application can increase the
 * capacity of a vector before inserting a large number of
 * components; this reduces the amount of incremental reallocation. <p>
 *
 * As of the Java 2 platform v1.2, this class has been retrofitted to
 * implement List, so that it becomes a part of Java's collection framework.
 * Unlike the new collection implementations, Vector is synchronized.<p>
 *
 * The Iterators returned by Vector's iterator and listIterator
 * methods are <em>fail-fast</em>: if the Vector is structurally modified
 * at any time after the Iterator is created, in any way except through the
 * Iterator's own remove or add methods, the Iterator will throw a
 * ConcurrentModificationException.  Thus, in the face of concurrent
 * modification, the Iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * The Enumerations returned by Vector's elements method are <em>not</em>
 * fail-fast.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i><p>
 *
 * This class is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Lee Boynton
 * @author  Jonathan Payne
 * @version 1.96, 02/19/04
 * @see Collection
 * @see List
 * @see ArrayList
 * @see LinkedList
 * @since   JDK1.0
 */
public class IntVector
    /*extends AbstractList
    implements List,*/
	implements RandomAccess, Cloneable, java.io.Serializable
{

    /**
     * The number of times this list has been <i>structurally modified</i>.
     * Structural modifications are those that change the size of the
     * list, or otherwise perturb it in such a fashion that iterations in
     * progress may yield incorrect results.<p>
     *
     * This field is used by the iterator and list iterator implementation
     * returned by the <tt>iterator</tt> and <tt>listIterator</tt> methods.
     * If the value of this field changes unexpectedly, the iterator (or list
     * iterator) will throw a <tt>ConcurrentModificationException</tt> in
     * response to the <tt>next</tt>, <tt>remove</tt>, <tt>previous</tt>,
     * <tt>set</tt> or <tt>add</tt> operations.  This provides
     * <i>fail-fast</i> behavior, rather than non-deterministic behavior in
     * the face of concurrent modification during iteration.<p>
     *
     * <b>Use of this field by subclasses is optional.</b> If a subclass
     * wishes to provide fail-fast iterators (and list iterators), then it
     * merely has to increment this field in its <tt>add(int, Object)</tt> and
     * <tt>remove(int)</tt> methods (and any other methods that it overrides
     * that result in structural modifications to the list).  A single call to
     * <tt>add(int, Object)</tt> or <tt>remove(int)</tt> must add no more than
     * one to this field, or the iterators (and list iterators) will throw
     * bogus <tt>ConcurrentModificationExceptions</tt>.  If an implementation
     * does not wish to provide fail-fast iterators, this field may be
     * ignored.
     */
    protected transient int modCount = 0;

    /**
     * The array buffer into which the components of the vector are
     * stored. The capacity of the vector is the length of this array buffer,
     * and is at least large enough to contain all the vector's elements.<p>
     *
     * Any array elements following the last element in the Vector are null.
     *
     * @serial
     */
    protected int[] elementData;

    /**
     * The number of valid components in this <tt>Vector</tt> object.
     * Components <tt>elementData[0]</tt> through
     * <tt>elementData[elementCount-1]</tt> are the actual items.
     *
     * @serial
     */
    protected int elementCount;

    /**
     * The amount by which the capacity of the vector is automatically
     * incremented when its size becomes greater than its capacity.  If
     * the capacity increment is less than or equal to zero, the capacity
     * of the vector is doubled each time it needs to grow.
     *
     * @serial
     */
    protected int capacityIncrement;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -2767605614048989439L;

    /**
     * Constructs an empty vector with the specified initial capacity and
     * capacity increment.
     *
     * @param   initialCapacity     the initial capacity of the vector.
     * @param   capacityIncrement   the amount by which the capacity is
     *                              increased when the vector overflows.
     * @exception IllegalArgumentException if the specified initial capacity
     *               is negative
     */
    public IntVector(int initialCapacity, int capacityIncrement) {
	super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
	this.elementData = new int[initialCapacity];
	this.capacityIncrement = capacityIncrement;
    }

    /**
     * Constructs an empty vector with the specified initial capacity and
     * with its capacity increment equal to zero.
     *
     * @param   initialCapacity   the initial capacity of the vector.
     * @exception IllegalArgumentException if the specified initial capacity
     *               is negative
     */
    public IntVector(int initialCapacity) {
	this(initialCapacity, 0);
    }

    /**
     * Constructs an empty vector so that its internal data array
     * has size <tt>10</tt> and its standard capacity increment is
     * zero.
     */
    public IntVector() {
	this(10);
    }

    /**
     * Copies the components of this vector into the specified array. The
     * item at index <tt>k</tt> in this vector is copied into component
     * <tt>k</tt> of <tt>anArray</tt>. The array must be big enough to hold
     * all the objects in this vector, else an
     * <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param   anArray   the array into which the components get copied.
     * @throws  NullPointerException if the given array is null.
     */
    public synchronized void copyInto(int[] anArray) {
	System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }

    /**
     * Trims the capacity of this vector to be the vector's current
     * size. If the capacity of this vector is larger than its current
     * size, then the capacity is changed to equal the size by replacing
     * its internal data array, kept in the field <tt>elementData</tt>,
     * with a smaller one. An application can use this operation to
     * minimize the storage of a vector.
     */
    public synchronized void trimToSize() {
	modCount++;
	int oldCapacity = elementData.length;
	if (elementCount < oldCapacity) {
	    int oldData[] = elementData;
	    elementData = new int[elementCount];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }

    /**
     * Increases the capacity of this vector, if necessary, to ensure
     * that it can hold at least the number of components specified by
     * the minimum capacity argument.
     *
     * <p>If the current capacity of this vector is less than
     * <tt>minCapacity</tt>, then its capacity is increased by replacing its
     * internal data array, kept in the field <tt>elementData</tt>, with a
     * larger one.  The size of the new data array will be the old size plus
     * <tt>capacityIncrement</tt>, unless the value of
     * <tt>capacityIncrement</tt> is less than or equal to zero, in which case
     * the new capacity will be twice the old capacity; but if this new size
     * is still smaller than <tt>minCapacity</tt>, then the new capacity will
     * be <tt>minCapacity</tt>.
     *
     * @param minCapacity the desired minimum capacity.
     */
    public synchronized void ensureCapacity(int minCapacity) {
	modCount++;
	ensureCapacityHelper(minCapacity);
    }

    /**
     * This implements the unsynchronized semantics of ensureCapacity.
     * Synchronized methods in this class can internally call this
     * method for ensuring capacity without incurring the cost of an
     * extra synchronization.
     *
     * @see java.util.Vector#ensureCapacity(int)
     */
    private void ensureCapacityHelper(int minCapacity) {
	int oldCapacity = elementData.length;
	if (minCapacity > oldCapacity) {
	    int[] oldData = elementData;
	    int newCapacity = (capacityIncrement > 0) ?
		(oldCapacity + capacityIncrement) : (oldCapacity * 2);
    	    if (newCapacity < minCapacity) {
		newCapacity = minCapacity;
	    }
	    elementData = new int[newCapacity];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }

    /**
     * Sets the size of this vector. If the new size is greater than the
     * current size, new <code>null</code> items are added to the end of
     * the vector. If the new size is less than the current size, all
     * components at index <code>newSize</code> and greater are discarded.
     *
     * @param   newSize   the new size of this vector.
     * @throws  ArrayIndexOutOfBoundsException if new size is negative.
     */
    public synchronized void setSize(int newSize) {
	modCount++;
	if (newSize > elementCount) {
	    ensureCapacityHelper(newSize);
	}
	elementCount = newSize;
    }

    /**
     * Returns the current capacity of this vector.
     *
     * @return  the current capacity (the length of its internal
     *          data array, kept in the field <tt>elementData</tt>
     *          of this vector).
     */
    public synchronized int capacity() {
	return elementData.length;
    }

    /**
     * Returns the number of components in this vector.
     *
     * @return  the number of components in this vector.
     */
    public synchronized int size() {
	return elementCount;
    }

    /**
     * Tests if this vector has no components.
     *
     * @return  <code>true</code> if and only if this vector has
     *          no components, that is, its size is zero;
     *          <code>false</code> otherwise.
     */
    public synchronized boolean isEmpty() {
    	return elementCount == 0;
    }

    /**
     * Tests if the specified object is a component in this vector.
     *
     * @param   elem   an object.
     * @return  <code>true</code> if and only if the specified object
     * is the same as a component in this vector, as determined by the
     * <tt>equals</tt> method; <code>false</code> otherwise.
     */
    public boolean contains(int elem) {
    	return indexOf(elem, 0) >= 0;
    }

    /**
     * Searches for the first occurence of the given argument, testing
     * for equality using the <code>equals</code> method.
     *
     * @param   elem   an object.
     * @return  the index of the first occurrence of the argument in this
     *          vector, that is, the smallest value <tt>k</tt> such that
     *          <tt>elem.equals(elementData[k])</tt> is <tt>true</tt>;
     *          returns <code>-1</code> if the object is not found.
     * @see     Object#equals(Object)
     */
    public int indexOf(int elem) {
	return indexOf(elem, 0);
    }

    /**
     * Searches for the first occurence of the given argument, beginning
     * the search at <code>index</code>, and testing for equality using
     * the <code>equals</code> method.
     *
     * @param   elem    an object.
     * @param   index   the non-negative index to start searching from.
     * @return  the index of the first occurrence of the object argument in
     *          this vector at position <code>index</code> or later in the
     *          vector, that is, the smallest value <tt>k</tt> such that
     *          <tt>elem.equals(elementData[k]) && (k &gt;= index)</tt> is
     *          <tt>true</tt>; returns <code>-1</code> if the object is not
     *          found. (Returns <code>-1</code> if <tt>index</tt> &gt;= the
     *          current size of this <tt>Vector</tt>.)
     * @exception  IndexOutOfBoundsException  if <tt>index</tt> is negative.
     * @see     Object#equals(Object)
     */
    public synchronized int indexOf(int elem, int index) {
	    for (int i = index ; i < elementCount ; i++)
	    	if (elem == elementData[i])
	    		return i;
		return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified object in
     * this vector.
     *
     * @param   elem   the desired component.
     * @return  the index of the last occurrence of the specified object in
     *          this vector, that is, the largest value <tt>k</tt> such that
     *          <tt>elem.equals(elementData[k])</tt> is <tt>true</tt>;
     *          returns <code>-1</code> if the object is not found.
     */
    public synchronized int lastIndexOf(int elem) {
    	return lastIndexOf(elem, elementCount-1);
    }

    /**
     * Searches backwards for the specified object, starting from the
     * specified index, and returns an index to it.
     *
     * @param  elem    the desired component.
     * @param  index   the index to start searching from.
     * @return the index of the last occurrence of the specified object in this
     *          vector at position less than or equal to <code>index</code> in
     *          the vector, that is, the largest value <tt>k</tt> such that
     *          <tt>elem.equals(elementData[k]) && (k &lt;= index)</tt> is
     *          <tt>true</tt>; <code>-1</code> if the object is not found.
     *          (Returns <code>-1</code> if <tt>index</tt> is negative.)
     * @exception  IndexOutOfBoundsException  if <tt>index</tt> is greater
     *             than or equal to the current size of this vector.
     */
    public synchronized int lastIndexOf(int elem, int index) {
        if (index >= elementCount)
            throw new IndexOutOfBoundsException(index + " >= "+ elementCount);

	    for (int i = index; i >= 0; i--)
		if (elem == elementData[i])
		    return i;

	    return -1;
    }

    /**
     * Returns the component at the specified index.<p>
     *
     * This method is identical in functionality to the get method
     * (which is part of the List interface).
     *
     * @param      index   an index into this vector.
     * @return     the component at the specified index.
     * @exception  ArrayIndexOutOfBoundsException  if the <tt>index</tt>
     *             is negative or not less than the current size of this
     *             <tt>Vector</tt> object.
     *             given.
     * @see	   #get(int)
     * @see	   List
     */
    public synchronized int elementAt(int index) {
		if (index >= elementCount) {
		    throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		}

        return elementData[index];
    }

    /**
     * Returns the first component (the item at index <tt>0</tt>) of
     * this vector.
     *
     * @return     the first component of this vector.
     * @exception  NoSuchElementException  if this vector has no components.
     */
    public synchronized int firstElement() {
		if (elementCount == 0) {
		    throw new NoSuchElementException();
		}
		return elementData[0];
    }

    /**
     * Returns the last component of the vector.
     *
     * @return  the last component of the vector, i.e., the component at index
     *          <code>size()&nbsp;-&nbsp;1</code>.
     * @exception  NoSuchElementException  if this vector is empty.
     */
    public synchronized int lastElement() {
		if (elementCount == 0) {
		    throw new NoSuchElementException();
		}
		return elementData[elementCount - 1];
    }

    /**
     * Sets the component at the specified <code>index</code> of this
     * vector to be the specified object. The previous component at that
     * position is discarded.<p>
     *
     * The index must be a value greater than or equal to <code>0</code>
     * and less than the current size of the vector. <p>
     *
     * This method is identical in functionality to the set method
     * (which is part of the List interface). Note that the set method reverses
     * the order of the parameters, to more closely match array usage.  Note
     * also that the set method returns the old value that was stored at the
     * specified position.
     *
     * @param      obj     what the component is to be set to.
     * @param      index   the specified index.
     * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
     * @see        #size()
     * @see        List
     * @see	   #set(int, java.lang.Object)
     */
    public synchronized void setElementAt(int obj, int index) {
		if (index >= elementCount) {
		    throw new ArrayIndexOutOfBoundsException(index + " >= " +
							     elementCount);
		}
		elementData[index] = obj;
    }

    /**
     * Deletes the component at the specified index. Each component in
     * this vector with an index greater or equal to the specified
     * <code>index</code> is shifted downward to have an index one
     * smaller than the value it had previously. The size of this vector
     * is decreased by <tt>1</tt>.<p>
     *
     * The index must be a value greater than or equal to <code>0</code>
     * and less than the current size of the vector. <p>
     *
     * This method is identical in functionality to the remove method
     * (which is part of the List interface).  Note that the remove method
     * returns the old value that was stored at the specified position.
     *
     * @param      index   the index of the object to remove.
     * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
     * @see        #size()
     * @see	   #remove(int)
     * @see	   List
     */
    public synchronized void removeElementAt(int index) {
		modCount++;
		if (index >= elementCount) {
		    throw new ArrayIndexOutOfBoundsException(index + " >= " +
							     elementCount);
		}
		else if (index < 0) {
		    throw new ArrayIndexOutOfBoundsException(index);
		}
		int j = elementCount - index - 1;
		if (j > 0) {
		    System.arraycopy(elementData, index + 1, elementData, index, j);
		}
		elementCount--;
    }

    /**
     * Inserts the specified object as a component in this vector at the
     * specified <code>index</code>. Each component in this vector with
     * an index greater or equal to the specified <code>index</code> is
     * shifted upward to have an index one greater than the value it had
     * previously. <p>
     *
     * The index must be a value greater than or equal to <code>0</code>
     * and less than or equal to the current size of the vector. (If the
     * index is equal to the current size of the vector, the new element
     * is appended to the Vector.)<p>
     *
     * This method is identical in functionality to the add(Object, int) method
     * (which is part of the List interface). Note that the add method reverses
     * the order of the parameters, to more closely match array usage.
     *
     * @param      obj     the component to insert.
     * @param      index   where to insert the new component.
     * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
     * @see        #size()
     * @see	   #add(int, Object)
     * @see	   List
     */
    public synchronized void insertElementAt(int obj, int index) {
		modCount++;
		if (index > elementCount) {
		    throw new ArrayIndexOutOfBoundsException(index
							     + " > " + elementCount);
		}
		ensureCapacityHelper(elementCount + 1);
		System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
		elementData[index] = obj;
		elementCount++;
    }

    /**
     * Adds the specified component to the end of this vector,
     * increasing its size by one. The capacity of this vector is
     * increased if its size becomes greater than its capacity. <p>
     *
     * This method is identical in functionality to the add(Object) method
     * (which is part of the List interface).
     *
     * @param   obj   the component to be added.
     * @see	   #add(Object)
     * @see	   List
     */
    public synchronized void addElement(int obj) {
		modCount++;
		ensureCapacityHelper(elementCount + 1);
		elementData[elementCount++] = obj;
    }

    /**
     * Removes the first (lowest-indexed) occurrence of the argument
     * from this vector. If the object is found in this vector, each
     * component in the vector with an index greater or equal to the
     * object's index is shifted downward to have an index one smaller
     * than the value it had previously.<p>
     *
     * This method is identical in functionality to the remove(Object)
     * method (which is part of the List interface).
     *
     * @param   obj   the component to be removed.
     * @return  <code>true</code> if the argument was a component of this
     *          vector; <code>false</code> otherwise.
     * @see	List#remove(Object)
     * @see	List
     */
    public synchronized boolean removeElement(int obj) {
		modCount++;
		int i = indexOf(obj);
		if (i >= 0) {
		    removeElementAt(i);
		    return true;
		}
		return false;
    }

    /**
     * Removes all components from this vector and sets its size to zero.<p>
     *
     * This method is identical in functionality to the clear method
     * (which is part of the List interface).
     *
     * @see	#clear
     * @see	List
     */
    public synchronized void removeAllElements() {
        modCount++;
		elementCount = 0;
    }

    /**
     * Returns a clone of this vector. The copy will contain a
     * reference to a clone of the internal data array, not a reference
     * to the original internal data array of this <tt>Vector</tt> object.
     *
     * @return  a clone of this vector.
     */
    public synchronized Object clone() {
	try {
	    IntVector v = (IntVector) super.clone();
	    v.elementData = new int[elementCount];
	    System.arraycopy(elementData, 0, v.elementData, 0, elementCount);
	    v.modCount = 0;
	    return v;
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns an array containing all of the elements in this Vector
     * in the correct order.
     *
     * @since 1.2
     */
    public synchronized int[] toArray() {
		int[] result = new int[elementCount];
		System.arraycopy(elementData, 0, result, 0, elementCount);
		return result;
    }

    /**
     * Returns an array containing all of the elements in this Vector in the
     * correct order; the runtime type of the returned array is that of the
     * specified array.  If the Vector fits in the specified array, it is
     * returned therein.  Otherwise, a new array is allocated with the runtime
     * type of the specified array and the size of this Vector.<p>
     *
     * If the Vector fits in the specified array with room to spare
     * (i.e., the array has more elements than the Vector),
     * the element in the array immediately following the end of the
     * Vector is set to null.  This is useful in determining the length
     * of the Vector <em>only</em> if the caller knows that the Vector
     * does not contain any null elements.
     *
     * @param a the array into which the elements of the Vector are to
     *		be stored, if it is big enough; otherwise, a new array of the
     * 		same runtime type is allocated for this purpose.
     * @return an array containing the elements of the Vector.
     * @exception ArrayStoreException the runtime type of a is not a supertype
     * of the runtime type of every element in this Vector.
     * @throws NullPointerException if the given array is null.
     * @since 1.2
     */
    public synchronized int[] toArray(int[] a) {
        if (a.length < elementCount)
            a = new int[ elementCount ];

        System.arraycopy(elementData, 0, a, 0, elementCount);

        return a;
    }

    // Positional Access Operations

    /**
     * Returns the element at the specified position in this Vector.
     *
     * @param index index of element to return.
     * @return object at the specified index
     * @exception ArrayIndexOutOfBoundsException index is out of range (index
     * 		  &lt; 0 || index &gt;= size()).
     * @since 1.2
     */
    public synchronized int get(int index) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);

	return elementData[index];
    }

    /**
     * Replaces the element at the specified position in this Vector with the
     * specified element.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @exception ArrayIndexOutOfBoundsException index out of range
     *		  (index &lt; 0 || index &gt;= size()).
     * @since 1.2
     */
    public synchronized int set(int index, int element) {
		if (index >= elementCount)
		    throw new ArrayIndexOutOfBoundsException(index);

		int oldValue = elementData[index];
		elementData[index] = element;
		return oldValue;
    }

    /**
     * Appends the specified element to the end of this Vector.
     *
     * @param o element to be appended to this Vector.
     * @return true (as per the general contract of Collection.add).
     * @since 1.2
     */
    public synchronized boolean add(int o) {
		modCount++;
		ensureCapacityHelper(elementCount + 1);
		elementData[elementCount++] = o;
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this Vector.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     * @exception ArrayIndexOutOfBoundsException index is out of range
     *		  (index &lt; 0 || index &gt; size()).
     * @since 1.2
     */
    public void add(int index, int element) {
        insertElementAt(element, index);
    }

    /**
     * Removes the element at the specified position in this Vector.
     * shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the Vector.
     *
     * @exception ArrayIndexOutOfBoundsException index out of range (index
     * 		  &lt; 0 || index &gt;= size()).
     * @param index the index of the element to removed.
     * @return element that was removed
     * @since 1.2
     */
    public synchronized int remove(int index) {
		modCount++;
		if (index >= elementCount)
		    throw new ArrayIndexOutOfBoundsException(index);
		int oldValue = elementData[index];

		int numMoved = elementCount - index - 1;
		elementCount--;

		if (numMoved > 0)
		    System.arraycopy(elementData, index+1, elementData, index, numMoved);

		return oldValue;
    }

    /**
     * Removes all of the elements from this Vector.  The Vector will
     * be empty after this call returns (unless it throws an exception).
     *
     * @since 1.2
     */
    public void clear() {
        removeAllElements();
    }

    // Bulk Operations

    /**
     * Appends all of the elements in the specified Collection to the end of
     * this Vector, in the order that they are returned by the specified
     * Collection's Iterator.  The behavior of this operation is undefined if
     * the specified Collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified Collection is this Vector, and this Vector is nonempty.)
     *
     * @param c elements to be inserted into this Vector.
     * @return <tt>true</tt> if this Vector changed as a result of the call.
     * @throws NullPointerException if the specified collection is null.
     * @since 1.2
     */
    public synchronized boolean addAll(int[] c) {
		modCount++;
        int[] a = c;
        int numNew = a.length;
        ensureCapacityHelper(elementCount + numNew);
        System.arraycopy(a, 0, elementData, elementCount, numNew);
        elementCount += numNew;
		return numNew != 0;
    }


    /**
     * Inserts all of the elements in the specified Collection into this
     * Vector at the specified position.  Shifts the element currently at
     * that position (if any) and any subsequent elements to the right
     * (increases their indices).  The new elements will appear in the Vector
     * in the order that they are returned by the specified Collection's
     * iterator.
     *
     * @param index index at which to insert first element
     *		    from the specified collection.
     * @param c elements to be inserted into this Vector.
     * @return <tt>true</tt> if this Vector changed as a result of the call.
     * @exception ArrayIndexOutOfBoundsException index out of range (index
     *		  &lt; 0 || index &gt; size()).
     * @throws NullPointerException if the specified collection is null.
     * @since 1.2
     */
    public synchronized boolean addAll(int index, int[] c) {
		modCount++;
		if (index < 0 || index > elementCount)
		    throw new ArrayIndexOutOfBoundsException(index);

        int[] a = c;
		int numNew = a.length;
		ensureCapacityHelper(elementCount + numNew);

		int numMoved = elementCount - index;
		if (numMoved > 0)
		    System.arraycopy(elementData, index, elementData, index + numNew,
				     numMoved);

	    System.arraycopy(a, 0, elementData, index, numNew);
		elementCount += numNew;
		return numNew != 0;
    }

    /**
     * Compares the specified Object with this Vector for equality.  Returns
     * true if and only if the specified Object is also a List, both Lists
     * have the same size, and all corresponding pairs of elements in the two
     * Lists are <em>equal</em>.  (Two elements <code>e1</code> and
     * <code>e2</code> are <em>equal</em> if <code>(e1==null ? e2==null :
     * e1.equals(e2))</code>.)  In other words, two Lists are defined to be
     * equal if they contain the same elements in the same order.
     *
     * @param o the Object to be compared for equality with this Vector.
     * @return true if the specified Object is equal to this Vector
     */
    public synchronized boolean equals(int o) {
        return super.equals(o);
    }

    /**
     * Returns the hash code value for this Vector.
     */
    public synchronized int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a string representation of this Vector, containing
     * the String representation of each element.
     */
    public synchronized String toString() {
    	String result = "[";

    	for (int i=0;i<this.size();i++) {
    		result += this.elementAt(i) + " ";
    	}

    	result = result.trim() + "]";

    	return result;
        //return super.toString();
    }

    /**
     * Removes from this List all of the elements whose index is between
     * fromIndex, inclusive and toIndex, exclusive.  Shifts any succeeding
     * elements to the left (reduces their index).
     * This call shortens the ArrayList by (toIndex - fromIndex) elements.  (If
     * toIndex==fromIndex, this operation has no effect.)
     *
     * @param fromIndex index of first element to be removed.
     * @param toIndex index after last element to be removed.
     */
    protected synchronized void removeRange(int fromIndex, int toIndex) {
		modCount++;
		int numMoved = elementCount - toIndex;
	        System.arraycopy(elementData, toIndex, elementData, fromIndex,
	                         numMoved);
    }

    /**
     * Save the state of the <tt>Vector</tt> instance to a stream (that
     * is, serialize it).  This method is present merely for synchronization.
     * It just calls the default readObject method.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException
    {
    	s.defaultWriteObject();
    }
}
