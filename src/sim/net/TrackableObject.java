package sim.net;

public abstract class TrackableObject {

	/**
	 * The ID of this Object
	 */
	public long objectID;
	public static long totalObjects = 0;
	{
		newID();
	}

	protected void newID() {
		totalObjects++;
		objectID = totalObjects;
	}

	public abstract int getSize();
}
