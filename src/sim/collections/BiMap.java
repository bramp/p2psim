/**
 *
 */
package sim.collections;

import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Implements a bidirectional (one-to-one) map using a TreeMap
 * @author brampton
 *
 */
public class BiMap<K, V> extends TreeMap<K, V> {

	/**
	 * This is included because we can't use the original one in TreeMap
	 * @param o1
	 * @param o2
	 * @return
	 */
	@SuppressWarnings("all") // TODO find the correct warning to suppress
    final static boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

	/**
	 * Gets the key mapped to by this value
	 * @param value
	 * @return The key otherwise null
	 */
	public K getKey(Object value) {
        for ( Entry<K, V> e : entrySet() )
        	if ( valEquals( value, e.getValue() ) )
        		return e.getKey();

        return null;
	}

	/**
	 * Gets the value mapped to by this key
	 * @param key
	 * @return The value otherwise null
	 */
	public V getValue(Object key) {
		return get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.TreeMap#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		// If getKey is optimised, this will be faster than the original containsValue
		return getKey(value) != null;
	}

	/* (non-Javadoc)
	 * @see java.util.TreeMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		// TODO make sure this value is unique!
		return super.put(key, value);
	}
}
