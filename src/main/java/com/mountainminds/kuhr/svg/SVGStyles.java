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

	private SVGColor fill;
	private float fillOpacity;
	private SVGColor stroke;
	private float strokeOpacity;
	private float strokeWidth;
	private int strokeLinecap;
	private int strokeLinejoin;

	public SVGStyles() {
		this.fill = SVGColor.BLACK;
		this.fillOpacity = 1.0f;
		this.stroke = SVGColor.NONE;
		this.strokeOpacity = 1.0f;
		this.strokeWidth = 1.0f;
		this.strokeLinecap = BasicStroke.CAP_BUTT;
		this.strokeLinejoin = BasicStroke.JOIN_MITER;
	}

	public SVGStyles(SVGStyles parent) {
		this.fill = parent.fill;
		this.fillOpacity = parent.fillOpacity;
		this.stroke = parent.stroke;
		this.strokeOpacity = parent.strokeOpacity;
		this.strokeWidth = parent.strokeWidth;
		this.strokeLinecap = parent.strokeLinecap;
		this.strokeLinejoin = parent.strokeLinejoin;
	}

	public void read(Node node) {
		Function<String, Optional<String>> attributes = DomReader.attributes(node);
		attributes.apply("style").ifPresent(style -> read(parseStyle(style)));
		read(attributes);
	}

	private void read(Function<String, Optional<String>> attributes) {
		read(attributes, "fill", SVGColor::of, c -> this.fill = c);
		read(attributes, "fill-opacity", Float::parseFloat, o -> this.fillOpacity = o);
		read(attributes, "stroke", SVGColor::of, c -> this.stroke = c);
		read(attributes, "stroke-opacity", Float::parseFloat, o -> this.strokeOpacity = o);
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

	public SVGColor getFill() {
		return fill;
	}

	public boolean isFillOpaque() {
		return fillOpacity > 0.5;
	}

	public SVGColor getStroke() {
		return stroke;
	}

	public boolean isStrokeOpaque() {
		return strokeOpacity > 0.5;
	}

	public Stroke createStroke() {
		return new BasicStroke(strokeWidth, strokeLinecap, strokeLinejoin);
	}

}
