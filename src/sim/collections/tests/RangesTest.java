package sim.collections.tests;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import sim.collections.Range;
import sim.collections.Ranges;

public class RangesTest extends TestCase {

	Ranges r;

	@Before
	public void setUp() throws Exception {
		r = new Ranges();
	}

	@Test
	public void testRanges() {
		//Range[] answer1 = { new Range(0, 10) };
		r.add( new Range(0, 10) );

		//assertArrayEquals( answer1 , r.toArray());
		assertEquals( 10, r.length());

		Range[] answer2 = { new Range(0, 20) };
		r.add( new Range(10, 20) );

		//assertArrayEquals( answer2 , r.toArray());
		assertEquals( 20, r.length());

		r.add( new Range(0, 20) );

		assertEquals( answer2 , r.toArray());
		assertEquals( 20, r.length());

		r.add( new Range(1, 20) );

		assertEquals( answer2 , r.toArray());
		assertEquals( 20, r.length());

		r.add( new Range(0, 19) );

		assertEquals( answer2 , r.toArray());
		assertEquals( 20, r.length());

		r.add( new Range(1, 19) );

		assertEquals( answer2 , r.toArray());
		assertEquals( 20, r.length());
	}

}
