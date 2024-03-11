package com.mountainminds.kuhr.svg;

import static java.lang.Math.PI;

import java.awt.geom.AffineTransform;

import org.w3c.dom.Node;

class SVGTransform {

	private final AffineTransform transformation;

	private SVGTransform(AffineTransform t) {
		transformation = t;
	}

	SVGTransform() {
		this(new AffineTransform());
	}

	SVGTransform with(Node node) {
		return with(DomReader.attr(node, "transform", "none"));
	}

	SVGTransform with(String text) {
		if ("none".equals(text)) {
			return this;
		}
		var scanner = new SVGTransformScanner(text);
		var t = new AffineTransform(transformation);
		for (var op = scanner.op(); op != null; op = scanner.op()) {
			scanner.ordinal('(');
			switch (op) {
			case "matrix":
				t.concatenate(new AffineTransform( //
						scanner.number(), scanner.number(), scanner.number(), //
						scanner.number(), scanner.number(), scanner.number()));
				break;
			case "translate":
				t.translate(scanner.number(), scanner.number(0));
				break;
			case "translateX":
				t.translate(scanner.number(), 0);
				break;
			case "translateY":
				t.translate(0, scanner.number());
				break;
			case "scale":
				double sx = scanner.number();
				double sy = scanner.number(sx);
				t.scale(sx, sy);
				break;
			case "scaleX":
				t.scale(scanner.number(), 1);
				break;
			case "scaleY":
				t.scale(1, scanner.number());
				break;
			case "rotate":
				t.rotate(scanner.number() * PI / 180.0, scanner.number(0), scanner.number(0));
				break;
			case "skew":
				t.shear(scanner.number(), scanner.number(0));
				break;
			case "skewX":
				t.shear(Math.tan(scanner.number() * PI / 180.0), 0);
				break;
			case "skewY":
				t.shear(0, Math.tan(scanner.number() * PI / 180.0));
				break;
			default:
				throw new IllegalArgumentException("Unknown transform operation: " + op);
			}
			scanner.ordinal(')');
		}
		return new SVGTransform(t);
	}

	AffineTransform getTransform() {
		return transformation;
	}

}
