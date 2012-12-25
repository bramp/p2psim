/*
 * Created on 05-Mar-2005
 */
package sim.events;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Simple class to add the time infront of all printed lines
 * @author Andrew Brampton
 */
public class EventPrintStream extends PrintStream {

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(java.lang.String)
	 */
	@Override
	public void println(String x) {
		super.println(Events.getTime() + "\t" + x);
	}

	/**
	 * @param out
	 */
	public EventPrintStream(OutputStream out) {
		super(out);
	}

}