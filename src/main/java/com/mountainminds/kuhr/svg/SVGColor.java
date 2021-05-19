package com.mountainminds.kuhr.svg;

import java.util.Map;

class SVGColor {

	public static final SVGColor NONE = new SVGColor(0, 0, 0, 0);

	public static final SVGColor BLACK = new SVGColor(0, 0, 0, 255);

	public static final SVGColor WHITE = new SVGColor(255, 255, 255, 255);

	private static final Map<String, SVGColor> NAMED_COLORS = Map.of( //
			"none", NONE, //
			"black", BLACK, //
			"white", WHITE);

	public static SVGColor of(String str) {
		SVGColor c = NAMED_COLORS.get(str);
		if (c == null) {
			throw new IllegalArgumentException("Unknown color " + str);
		}
		return c;
	}

	private final int r, g, b, a;

	public SVGColor(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public boolean isBlack() {
		return r < 128 & g < 128 & b < 128 & a >= 128;
	}

	public boolean isWhite() {
		return r >= 128 & g >= 128 & b >= 128 & a >= 128;
	}

}
