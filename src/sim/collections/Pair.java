/**
 *
 */
package sim.collections;

/**
 * Simple class to hold two objects together
 * @author Andrew Brampton
 *
 */
public class Pair <T1, T2> {

	public final T1 left;
	public final T2 right;

	public Pair(final T1 left, final T2 right) {
		this.left = left;
		this.right = right;
	}

}
