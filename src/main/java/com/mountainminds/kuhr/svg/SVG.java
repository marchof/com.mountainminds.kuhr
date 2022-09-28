package com.mountainminds.kuhr.svg;

import java.awt.Shape;
import java.awt.geom.Area;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.mountainminds.kuhr.svg.SVGLoader.ShapeConsumer;

public class SVG {

	public static Shape toShape(InputStream in) throws IOException {
		ShapeCombiner combiner = new ShapeCombiner();
		SVGLoader svgloader = new SVGLoader(combiner);
		svgloader.load(in);
		return combiner.getShape();
	}

	public static Shape toShape(Path path) throws IOException {
		ShapeCombiner combiner = new ShapeCombiner();
		SVGLoader svgloader = new SVGLoader(combiner);
		svgloader.load(path);
		return combiner.getShape();
	}

	private static class ShapeCombiner implements ShapeConsumer {

		private Area combinedShape = new Area();

		@Override
		public void consume(SVGStyles styles, Shape shape) {
			if (styles.isFillOpaque()) {
				if (styles.getFill().isBlack()) {
					combinedShape.add(new Area(shape));
				}
				if (styles.getFill().isWhite()) {
					combinedShape.subtract(new Area(shape));
				}
			}
			if (styles.isStrokeOpaque()) {
				if (styles.getStroke().isBlack()) {
					combinedShape.add(new Area(styles.createStroke().createStrokedShape(shape)));
				}
				if (styles.getStroke().isWhite()) {
					combinedShape.subtract(new Area(styles.createStroke().createStrokedShape(shape)));
				}
			}
		}

		public Shape getShape() {
			return combinedShape;
		}

	}

}
