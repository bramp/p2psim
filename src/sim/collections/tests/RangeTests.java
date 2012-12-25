package sim.collections.tests;

import junit.framework.TestCase;

import org.junit.Test;

import sim.collections.Range;

public class RangeTests extends TestCase {

	@Test
	public void testRange() {
		Range r = new Range(0, 10);
		assertEquals ( 0, r.start );
		assertEquals ( 10, r.end );
	}

	@Test
	public void testSetDimensions() {
		Range r = new Range(0, 10);

		r.setDimensions(10, 20);
		assertEquals ( 10, r.start );
		assertEquals ( 20, r.end );
	}

	@Test
	public void testLength() {
		Range r = new Range(0, 10);
		assertEquals ( 10, r.length() );
	}

	@Test
	public void testOverlap1() {
		Range r1 = new Range(0, 10);
		Range r2 = new Range(0, 10);
		Range overlap1 = r1.overlap(r2);
		Range overlap2 = r2.overlap(r1);

		assertEquals ( overlap1, overlap2);
		assertEquals ( overlap1, r1);
		assertEquals ( overlap1, r2);
	}

	@Test
	public void testOverlap2() {
		Range r1 = new Range(0, 10);
		Range r2 = new Range(0, 5);
		Range overlap1 = r1.overlap(r2);
		Range overlap2 = r2.overlap(r1);

		assertEquals ( overlap1, overlap2);
		assertEquals ( overlap1, r2);
	}

	@Test
	public void testOverlap3() {
		Range r1 = new Range(0, 10);
		Range r2 = new Range(5, 10);
		Range overlap1 = r1.overlap(r2);
		Range overlap2 = r2.overlap(r1);

		assertEquals ( overlap1, overlap2);
		assertEquals ( overlap1, r2);
	}

	@Test
	public void testOverlap4() {
		Range r1 = new Range(0, 10);
		Range r2 = new Range(2, 8);
		Range overlap1 = r1.overlap(r2);
		Range overlap2 = r2.overlap(r1);

		assertEquals ( overlap1, overlap2 );
		assertEquals ( overlap1, r2);
	}

	@Test
	public void testOverlap5() {
		Range r1 = new Range(0, 10);
		Range r2 = new Range(10, 20);
		Range overlap1 = r1.overlap(r2);
		Range overlap2 = r2.overlap(r1);

		assertNull (overlap1);
		assertNull (overlap2);
	}

	@Test
	public void testDelete() {

	}

	@Test
	public void testClone() {

	}

	@Test
	public void testEqualsObject() {
		Range r1 = new Range(0,10);
		Range r2 = new Range(0,10);

		assertEquals ( r1, r2 );
		assertEquals ( r2, r1 );
	}

}
