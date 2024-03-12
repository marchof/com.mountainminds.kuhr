package com.mountainminds.kuhr.svg;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.w3c.dom.Node;

/**
 * https://www.w3.org/TR/SVG2/painting.html
 */
class SVGStyles {

	private static Map<String, Integer> LINECAPS = Map.of( //
			"butt", BasicStroke.CAP_BUTT, //
			"round", BasicStroke.CAP_ROUND, //
			"square", BasicStroke.CAP_SQUARE);

	private static Map<String, Integer> LINEJOINS = Map.of( //
			"miter", BasicStroke.JOIN_MITER, //
			"bevel", BasicStroke.JOIN_BEVEL, //
			"round", BasicStroke.JOIN_ROUND);

	private SVGColor fill = SVGColor.BLACK;
	private float fillOpacity = 1.0f;
	private SVGColor stroke = SVGColor.NONE;
	private float strokeOpacity = 1.0f;
	private float strokeWidth = 1.0f;
	private int strokeLinecap = BasicStroke.CAP_BUTT;
	private int strokeLinejoin = BasicStroke.JOIN_MITER;
	private float strokeMiterlimit = 4.0f;
	private float[] strokeDasharray = null;
	private float strokeDashoffset = 0.0f;

	SVGStyles() {
	}

	private SVGStyles(SVGStyles parent) {
		this.fill = parent.fill;
		this.fillOpacity = parent.fillOpacity;
		this.stroke = parent.stroke;
		this.strokeOpacity = parent.strokeOpacity;
		this.strokeWidth = parent.strokeWidth;
		this.strokeLinecap = parent.strokeLinecap;
		this.strokeLinejoin = parent.strokeLinejoin;
		this.strokeMiterlimit = parent.strokeMiterlimit;
		this.strokeDasharray = parent.strokeDasharray;
		this.strokeDashoffset = parent.strokeDashoffset;
	}

	SVGStyles with(Node node) {
		var s = new SVGStyles(this);
		s.parseStyle(DomReader.attr(node, "style", ""));
		s.read(DomReader.attributes(node));
		return s;
	}

	private void read(Function<String, Optional<String>> attributes) {
		read(attributes, "fill", SVGColor::of, c -> this.fill = c);
		read(attributes, "fill-opacity", Float::parseFloat, o -> this.fillOpacity = o);
		read(attributes, "stroke", SVGColor::of, c -> this.stroke = c);
		read(attributes, "stroke-opacity", Float::parseFloat, o -> this.strokeOpacity = o);
		read(attributes, "stroke-width", Float::parseFloat, w -> this.strokeWidth = w);
		read(attributes, "stroke-linecap", LINECAPS::get, c -> this.strokeLinecap = c);
		read(attributes, "stroke-linejoin", LINEJOINS::get, j -> this.strokeLinejoin = j);
		read(attributes, "stroke-miterlimit", Float::parseFloat, m -> this.strokeMiterlimit = m);
		read(attributes, "stroke-dasharray", SVGStyles::parseFloatArray, a -> this.strokeDasharray = a);
		read(attributes, "stroke-dashoffset", Float::parseFloat, o -> this.strokeDashoffset = o);
	}

	private static float[] parseFloatArray(String s) {
		var tokens = s.split("[,\\s]+");
		var array = new float[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			array[i] = Float.parseFloat(tokens[i]);
		}
		return array;
	}

	private static <T> void read(Function<String, Optional<String>> attributes, String attr,
			Function<String, T> converter, Consumer<T> consumer) {
		attributes.apply(attr).ifPresent((Consumer<String>) text -> {
			T value = converter.apply(text);
			if (value == null) {
				throw new IllegalArgumentException("Invalid value " + text + " for attribute " + attr);
			}
			consumer.accept(value);
		});
	}

	private Function<String, Optional<String>> parseStyle(String style) {
		Map<String, String> defs = new HashMap<>();
		for (String entry : style.split(";")) {
			String[] tokens = entry.split(":");
			if (tokens.length == 2) {
				defs.put(tokens[0].strip(), tokens[1].strip());
			}
		}
		return a -> Optional.ofNullable(defs.get(a));
	}

	SVGColor getFill() {
		return fill;
	}

	boolean isFillOpaque() {
		return fillOpacity > 0.5;
	}

	SVGColor getStroke() {
		return stroke;
	}

	boolean isStrokeOpaque() {
		return strokeOpacity > 0.5;
	}

	Stroke createStroke() {
		return new BasicStroke(strokeWidth, strokeLinecap, strokeLinejoin, strokeMiterlimit, strokeDasharray,
				strokeDashoffset);
	}

}
