package com.mountainminds.kuhr.svg;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
		public void consume(SVGStyles styles, AffineTransform transform, Shape shape) {
			if (styles.isFillOpaque()) {
				var fillArea = new Area(shape);
				fillArea.transform(transform);
				if (styles.getFill().isBlack()) {
					combinedShape.add(fillArea);
				}
				if (styles.getFill().isWhite()) {
					combinedShape.subtract(fillArea);
				}
			}
			if (styles.isStrokeOpaque()) {
				var strokeArea = new Area(styles.createStroke().createStrokedShape(shape));
				strokeArea.transform(transform);
				if (styles.getStroke().isBlack()) {
					combinedShape.add(strokeArea);
				}
				if (styles.getStroke().isWhite()) {
					combinedShape.subtract(strokeArea);
				}
			}
		}

		public Shape getShape() {
			return combinedShape;
		}

	}

}
