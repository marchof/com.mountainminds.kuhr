package com.mountainminds.kuhr.svg;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class SVGColor {

	private static final Map<String, SVGColor> NAMED_COLORS = new HashMap<>();

	static {
		var p = new Properties();
		try (var in = SVGColor.class.getResourceAsStream("namedcolors.properties")) {
			p.load(in);
		} catch (IOException e) {
			throw new AssertionError("Can't load named colors", e);
		}
		p.forEach((name, color) -> NAMED_COLORS.put(name.toString(), of(color.toString())));
		NAMED_COLORS.put("none", new SVGColor(0, 0, 0, 0));
	}

	public static final SVGColor NONE = of("none");

	public static final SVGColor BLACK = of("black");

	public static SVGColor of(String str) {
		SVGColor c = NAMED_COLORS.get(str.toLowerCase());
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
		return String.format("SVGColor[%s, %s, %s, %s]", r, g, b, a);
	}

}
