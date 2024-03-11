package com.mountainminds.kuhr.svg;

import java.awt.Color;
import java.util.Map;

class SVGColor {

	public static final SVGColor NONE = new SVGColor(0, 0, 0, 0);

	public static final SVGColor BLACK = new SVGColor(0, 0, 0, 255);

	public static final SVGColor WHITE = new SVGColor(255, 255, 255, 255);

	private static final Map<String, SVGColor> NAMED_COLORS = Map.of( //
			"none", NONE, //
			"black", BLACK, //
			"white", WHITE, //
			"blue", new SVGColor(0x00, 0x00, 0xff, 0xff), //
			"green", new SVGColor(0x00, 0x80, 0x00, 0xff), //
			"purple", new SVGColor(0x80, 0x00, 0x80, 0xff), //
			"red", new SVGColor(0xff, 0x00, 0x00, 0xff));

	public static SVGColor of(String str) {
		SVGColor c = NAMED_COLORS.get(str);
		if (c != null) {
			return c;
		}
		if (str.startsWith("#") && str.length() == 4) {
			int r = Integer.parseInt(str.substring(1, 2), 16);
			int g = Integer.parseInt(str.substring(2, 3), 16);
			int b = Integer.parseInt(str.substring(3, 4), 16);
			return new SVGColor(r + 16 * r, g + 16 * g, b + 16 * b, 255);
		}
		if (str.startsWith("#") && str.length() == 7) {
			return new SVGColor(Integer.parseInt(str.substring(1, 3), 16), //
					Integer.parseInt(str.substring(3, 5), 16), //
					Integer.parseInt(str.substring(5, 7), 16), //
					255);
		}
		throw new IllegalArgumentException("Unknown color " + str);
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

	Color toAWT() {
		return new Color(r, g, b, a);
	}

	@Override
	public String toString() {
		return "SVGColor[%s, %s, %s, %s]".formatted(r, g, b, a);
	}

}
