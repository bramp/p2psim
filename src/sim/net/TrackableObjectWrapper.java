package sim.net;

public class TrackableObjectWrapper extends TrackableObject {
	Object o = null;
	int size;

	public TrackableObjectWrapper(Object o) {
		this.o = o;
		size = o.toString().length(); // Best guest at its size
	}

	public Object get() {
		return o;
	}

	/* (non-Javadoc)
	 * @see sim.net.TrackableObject#getSize()
	 */
	@Override
	public int getSize() {
		return size;
	}
}
