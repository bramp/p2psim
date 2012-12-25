/*
 * Created on Feb 8, 2005
 */
package sim.net;

/**
 * @author Andrew Brampton
 */
public class InvalidHostException extends Exception {
	public InvalidHostException(Host host) {
		super(host + " is not valid");
	}

}
