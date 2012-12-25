package sim.net;

public class BrokenLinkException extends RoutingException {
	/**
	 * @param string
	 */
	public BrokenLinkException(String string) {
		super(string);
	}

}
