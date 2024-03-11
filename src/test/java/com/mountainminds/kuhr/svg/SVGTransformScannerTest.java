package com.mountainminds.kuhr.svg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SVGTransformScannerTest {

	@Test
	public void empty() {
		var s = new SVGTransformScanner("   ");
		assertNull(s.op());
	}

	@Test
	public void missing_paranthesis() {
		var s = new SVGTransformScanner("translate 10 10");
		assertEquals("translate", s.op());
		var ex = assertThrows(IllegalArgumentException.class, () -> s.ordinal('('));
		assertEquals("Missing '(' in 'translate 10 10'", ex.getMessage());
	}

	@Test
	public void rotate() {
		var s = new SVGTransformScanner("rotate(30)");
		assertEquals("rotate", s.op());
		s.ordinal('(');
		assertEquals(30, s.number());
		assertEquals(0, s.number(0));
		assertEquals(0, s.number(0));
		s.ordinal(')');
		assertNull(s.op());
	}

	@Test
	public void transform_rotate() {
		var s = new SVGTransformScanner("  transform(-42),  rotate(10, 3.1, 8.2)");

		assertEquals("transform", s.op());
		s.ordinal('(');
		assertEquals(-42, s.number());
		assertEquals(-42, s.number(-42));
		s.ordinal(')');

		assertEquals("rotate", s.op());
		s.ordinal('(');
		assertEquals(10, s.number());
		assertEquals(3.1, s.number(0));
		assertEquals(8.2, s.number(0));
		s.ordinal(')');

		assertNull(s.op());
	}

}
