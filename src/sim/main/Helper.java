package sim.main;

import java.util.Collections;
import java.util.List;

/**
 * @author Andrew Brampton
 *
 */
public abstract class Helper {

	/**
	 * Returns the short class name of this object
	 * @param o
	 * @return
	 */
	public static String getShortName(Object o) {
		return getShortName(o.getClass());
	}

	public static String getShortName(Class<?> c) {
		String name = c.getName();
		int idx = name.lastIndexOf('.');
		return name.substring(idx + 1);
	}

	/**
	 * Shuffles a List of objects
	 * @param list
	 */
	public static void shuffle(List<?> list) {
		Collections.shuffle(list, Global.rand);
	}

	public static Object pickFromList(List<?> list) {
		return list.get( Global.rand.nextInt(list.size()) );
	}
}
