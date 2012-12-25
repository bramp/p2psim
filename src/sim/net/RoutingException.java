/*
 * Created on 13-Feb-2005
 */
package sim.net;

/**
 * @author Andrew Brampton
 */
public class RoutingException extends Exception {
	/**
	 * @param string
	 */
	public RoutingException(String string) {
		super(string);
	}

	/**
	 * @param e
	 */
	public RoutingException(Exception e) {
		super(e);
	}
}
