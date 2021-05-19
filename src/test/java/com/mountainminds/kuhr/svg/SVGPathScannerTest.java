package com.mountainminds.kuhr.svg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SVGPathScannerTest {

	private SVGPathScanner scanner;

	@Test
	void compact_tokens() {
		scanner = new SVGPathScanner("M3.0-2.5");
		assertTrue(scanner.hasMoreTokens());
		assertEquals('M', scanner.nextCommand());
		assertTrue(scanner.hasMoreTokens());
		assertEquals(3.0, scanner.nextNumber());
		assertTrue(scanner.hasMoreTokens());
		assertEquals(-2.5, scanner.nextNumber());
		assertFalse(scanner.hasMoreTokens());
	}

	@Test
	void comma_separated_tokens() {
		scanner = new SVGPathScanner("M3.0,5.0");
		assertTrue(scanner.hasMoreTokens());
		assertEquals('M', scanner.nextCommand());
		assertTrue(scanner.hasMoreTokens());
		assertEquals(3.0, scanner.nextNumber());
		assertTrue(scanner.hasMoreTokens());
		assertEquals(5.0, scanner.nextNumber());
		assertFalse(scanner.hasMoreTokens());
	}

	@Test
	void whitespace_separated_tokens() {
		scanner = new SVGPathScanner(" M -3.0 5.0   H 1.0 ");
		assertTrue(scanner.hasMoreTokens());
		assertEquals('M', scanner.nextCommand());
		assertTrue(scanner.hasMoreTokens());
		assertEquals(-3.0, scanner.nextNumber());
		assertTrue(scanner.hasMoreTokens());
		assertEquals(5.0, scanner.nextNumber());
		assertTrue(scanner.hasMoreTokens());
		assertEquals('H', scanner.nextCommand());
		assertTrue(scanner.hasMoreTokens());
		assertEquals(1.0, scanner.nextNumber());
		assertFalse(scanner.hasMoreTokens());
	}

	@Test
	void whitespace_only() {
		scanner = new SVGPathScanner("   ");
		assertFalse(scanner.hasMoreTokens());
		Assertions.assertThrows(NoSuchElementException.class, () -> scanner.nextCommand());
		Assertions.assertThrows(NoSuchElementException.class, () -> scanner.nextNumber());
	}

	@Test
	void relative_number() {
		scanner = new SVGPathScanner("3.0");
		assertTrue(scanner.hasMoreTokens());
		assertEquals(3.5, scanner.nextNumber(0.5));
		assertFalse(scanner.hasMoreTokens());
	}

}
