package sim.net.overlay.dht;

/**
 * Currently a utility class for any reusable bitwise operations/comparisons
 * on node addresses
 * @author macquire
 *
 */
public class ID implements Comparable {
	private static final int HALF_LONG = Long.SIZE / 2;   // saves calculating it...
	private static final int PAD_BASE = 4;				  // using hex strings

	private long[] id;		// array of values making up the ID
	private int bits;		// number of bits making up the ID

	/**
	 * Constructs an ID, a single long in length (usually 64 bits)
	 * @param id the long value to use for the ID
	 */
	public ID(long id) {
		this.id = new long[1];
		this.id[0] = id;
		bits = Long.SIZE - 1; // default to size of a single long (0-x, not 1-x)
	}

	/**
	 * Constructs an ID with a bitcount equal to the combined size of
	 * the longs in the supplied array
	 * @param id the long array from which to construct the ID
	 */
	public ID(long[] id) {
		this.id = id;
		bits = Long.SIZE * id.length - 1 ; // size of n longs (0-x, not 1-x)
	}

	/**
	 * Constructs an ID to the parameters given
	 * @param id a set of longs, most significant at the lowest index
	 * @param bits the number of bits to read from the long array
	 * @throws Exception thrown if an invalid bit count is specified
	 */
	public ID(long[] id, int bits) throws Exception {
		// do not allow IDs with fewer than 2 bits, or those not divisible by 2
		if (bits < 2 || !((bits & 1) == 0)) {
			throw new Exception("Invalid ID bit count!");
		}
		// allocate an extra long if a remainder exists
		this.id = new long[(bits/Long.SIZE) + ((bits & Long.SIZE-1) > 0 ? 1 : 0)];

		// move to the 0-x range instead of 1-x
		bits--;
		this.bits = bits; // arbitrary size (within constraints)

		// fill array with supplied data
		int index = 0;
		while(bits > 0) {
			// are we near the end?
			if (bits < Long.SIZE) {
				// mask off all but remaining bits
				this.id[index] = id[index] & ((2 << bits) - 1);
			}
			else {this.id[index] = id[index];}
			// move on to next long index/set of bits
			index++;
			bits -= Long.SIZE;
		}
	}

	/**
	 * Returns the value of this ID - NB the bit count is also required to
	 * properly read this value
	 * @return array of longs representing the value held in this ID
	 */
	public long[] getValue() {return id;}
	/**
	 * Returns the number of bits that this ID is made up of
	 * @return the number of bits making up this ID
	 */
	public int getBitCount() {return bits + 1;}

	/**
	 * ID implements the Comparable interface for easy comparisons
	 * @param o the ID to compare to
	 * @return -1 if the object passed in is larger, 0 if equal, 1 if smaller
	 */
	public int compareTo(Object o) {
		ID e = (ID)o;

		// NB, getBitCount returns in the range 1-X, bits is in 0-X
		if (e.getBitCount() != (this.bits + 1)) {
			throw new ClassCastException("Differing bit count!");
		}

		int readBits = this.bits;
		int index = 0;

		long[] id = e.getValue();
		long a,b;

		while(readBits > 0) {
			if (readBits < Long.SIZE) {
				// mask off all but remaining bits
				a = this.id[index] & ((2 << readBits) - 1);
				b = 	 id[index] & ((2 << readBits) - 1);

					 if (a > b) return  1;		// we're larger
				else if (a < b) return -1;		// we're smaller
				return 0;   					// must be equal
			}

			// go on to next full long value
			// examine first 32 bits of the current long
			a = (this.id[index] & 0xFFFFFFFF00000000L) >>> HALF_LONG;
			b = 	 (id[index] & 0xFFFFFFFF00000000L) >>> HALF_LONG;

				 if (a > b) return  1;		// we're larger
			else if (a < b) return -1;		// we're smaller

			// no score draw, examine second 32 bits
			a = (this.id[index] & 0x00000000FFFFFFFFL);
			b = 	 (id[index] & 0x00000000FFFFFFFFL);

				 if (a > b) return  1;		// we're larger
			else if (a < b) return -1;		// we're smaller

			// still no result - move down to the next index/set of bits
			index++;
			readBits -= Long.SIZE;
		}

		// should never reach me!
		return 0;
	}

	@Override
	/**
	 * Outputs the value of the ID in the form of a hexadecimal string
	 */
	public String toString() {
		String result = "";
		int readBits = bits;
		int index = 0;

		while(readBits > 0) {
			String temp;
			int pad;

			if (readBits < Long.SIZE) {
				// convert the last set of bits to a string
				temp = Long.toHexString(id[index] & ((2 << readBits) - 1));
				pad = readBits;
			}
			else {
				// convert the next long to a string
				temp = Long.toHexString(id[index]);
				pad = Long.SIZE ;
			}

			// pad the value read with an appropriate number of zeroes
			while (temp.length() < (pad / PAD_BASE))
				temp = '0' + temp;

			// add to the complete string, move on to the next long and
			// decrement the number of bits left to read
			result += temp.toUpperCase();
			index++;
			readBits -= Long.SIZE;
		}
		return result;
	}
}
