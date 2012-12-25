/**
 *
 */
package sim.stats.trace;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Andrew Brampton
 *
 */
public class BufferedStringOutputStream extends FilterOutputStream {

	protected class ListItem {
		public byte[] buf;
		public int off;
		public int len;

		public ListItem next;

		ListItem(byte[] buf, int off, int len) {
			this.buf = buf;
			this.off = off;
			this.len = len;

			next = null;
		}
	}

    protected ListItem first = null;
    protected ListItem last = null;
    protected int count;
    protected final int maxcount;

    public BufferedStringOutputStream(OutputStream out) {
    	this(out, 10);
    }

    public BufferedStringOutputStream(OutputStream out, int size) {
    	super(out);

        if (size < 0) {
            throw new IllegalArgumentException("Buffer size < 0");
        }

        count = 0;
        first = null;
        maxcount = size;
    }

    /** Flush the internal buffer */
    private void flushBuffer() throws IOException {
        while (first != null) {
        	out.write(first.buf, first.off, first.len);
        	count--;
        	first = first.next;
        }
        last = null;
    }


    /**
     * Writes the specified byte to this buffered output stream.
     *
     * @param      b   the byte to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized void write(int b) throws IOException {
		throw new RuntimeException("write(int) is not supported");
    }

    public synchronized void write(byte b[], int off, int len) throws IOException {
    	ListItem tmp = new ListItem(b, off, len);

    	if (last == null) {
    		first = tmp;
    	} else {
	    	last.next = tmp;
    	}

    	last = tmp;
    	count++;

		if (count > maxcount) {
		    flushBuffer();
		}
    }

    public synchronized void flush() throws IOException {
        flushBuffer();
        out.flush();
    }

}
