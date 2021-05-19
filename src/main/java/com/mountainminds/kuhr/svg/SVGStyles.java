package com.mountainminds.kuhr.svg;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.w3c.dom.Node;

class SVGStyles {

	private static Map<String, Integer> LINECAPS = Map.of( //
			"butt", BasicStroke.CAP_BUTT, //
			"round", BasicStroke.CAP_ROUND, //
			"square", BasicStroke.CAP_SQUARE);

	private static Map<String, Integer> LINEJOINS = Map.of( //
			"miter", BasicStroke.JOIN_MITER, //
			"bevel", BasicStroke.JOIN_BEVEL, //
			"round", BasicStroke.JOIN_ROUND);

	private SVGColor fill;
	private SVGColor stroke;
	private float strokeWidth;
	private int strokeLinecap;
	private int strokeLinejoin;

	public SVGStyles() {
		this.fill = SVGColor.BLACK;
		this.stroke = SVGColor.NONE;
		this.strokeWidth = 1.0f;
		this.strokeLinecap = BasicStroke.CAP_BUTT;
		this.strokeLinejoin = BasicStroke.JOIN_MITER;
	}

	public SVGStyles(SVGStyles parent) {
		this.fill = parent.fill;
		this.stroke = parent.stroke;
		this.strokeWidth = parent.strokeWidth;
		this.strokeLinecap = parent.strokeLinecap;
		this.strokeLinejoin = parent.strokeLinejoin;
	}

	public void read(Node node) {
		Function<String, Optional<String>> attributes = DomReader.attributes(node);
		read(attributes, "fill", SVGColor::of, c -> this.fill = c);
		read(attributes, "stroke", SVGColor::of, c -> this.stroke = c);
		read(attributes, "stroke-width", Float::parseFloat, w -> this.strokeWidth = w);
		read(attributes, "stroke-linecap", LINECAPS::get, c -> this.strokeLinecap = c);
		read(attributes, "stroke-linejoin", LINEJOINS::get, j -> this.strokeLinejoin = j);
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

	public SVGColor getFill() {
		return fill;
	}

	public SVGColor getStroke() {
		return stroke;
	}

	public Stroke createStroke() {
		return new BasicStroke(strokeWidth, strokeLinecap, strokeLinejoin);
	}

}
