package com.mountainminds.kuhr.svg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SVGPathScannerTest {

	private SVGPathScanner scanner;

	@Test
	void compact_tokens() {
		scanner = new SVGPathScanner("M3.0-2.5");
		assertEquals('M', scanner.nextCommand());
		assertEquals(3.0, scanner.nextNumber());
		assertEquals(-2.5, scanner.nextNumber());
		assertEquals(-1, scanner.nextCommand());
	}

	@Test
	void comma_separated_tokens() {
		scanner = new SVGPathScanner("M3.0,5.0");
		assertEquals('M', scanner.nextCommand());
		assertEquals(3.0, scanner.nextNumber());
		assertEquals(5.0, scanner.nextNumber());
		assertEquals(-1, scanner.nextCommand());
	}

	@Test
	void whitespace_separated_tokens() {
		scanner = new SVGPathScanner(" M -3.0 5.0   H 1.0 ");
		assertEquals('M', scanner.nextCommand());
		assertEquals(-3.0, scanner.nextNumber());
		assertEquals(5.0, scanner.nextNumber());
		assertEquals('H', scanner.nextCommand());
		assertEquals(1.0, scanner.nextNumber());
		assertEquals(-1, scanner.nextCommand());
	}

	@Test
	void whitespace_only() {
		scanner = new SVGPathScanner("   ");
		assertEquals(-1, scanner.nextCommand());
	}

	@Test
	void relative_number() {
		scanner = new SVGPathScanner("3.0");
		assertEquals(3.5, scanner.nextNumber(0.5));
		assertEquals(-1, scanner.nextCommand());
	}

}
