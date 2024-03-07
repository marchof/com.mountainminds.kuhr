package com.mountainminds.kuhr.svg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SVGColorTest {

	@Test
	void parse_named() {
		assertEquals("SVGColor[255, 255, 255, 255]", SVGColor.of("white").toString());
	}

	@Test
	void parse_hex3() {
		assertEquals("SVGColor[51, 102, 255, 255]", SVGColor.of("#36f").toString());
	}

	@Test
	void parse_hex6() {
		assertEquals("SVGColor[3, 180, 240, 255]", SVGColor.of("#03b4f0").toString());
	}

}
