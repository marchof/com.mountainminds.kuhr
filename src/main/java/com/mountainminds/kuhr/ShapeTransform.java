package com.mountainminds.kuhr;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ShapeTransform {

	public static Shape roundInnerCorners(Shape shape, double radius) {
		BasicStroke strokeInner = new BasicStroke((float) radius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		Area result = new Area(shape);
		result.add(new Area(strokeInner.createStrokedShape(result)));
		result.subtract(new Area(strokeInner.createStrokedShape(result)));
		return result;
	}

	public static Function<Shape, Shape> roundInnerCorners(double radius) {
		return s -> roundInnerCorners(s, radius);
	}

	public static Shape roundOuterCorners(Shape shape, double radius) {
		BasicStroke strokeInner = new BasicStroke((float) radius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		Area result = new Area(shape);
		result.subtract(new Area(strokeInner.createStrokedShape(result)));
		result.add(new Area(strokeInner.createStrokedShape(result)));
		return result;
	}

	public static Function<Shape, Shape> roundOuterCorners(double radius) {
		return s -> roundOuterCorners(s, radius);
	}

	public static Shape applyOnSingulars(Shape shape, Function<Shape, Shape> transformation) {
		Area result = new Area();
		splitInSingulars(shape).stream().map(transformation).map(Area::new).forEach(result::add);
		return result;
	}

	public static final List<Shape> splitInSingulars(Shape shape) {
		List<Shape> result = new ArrayList<>();
		double coords[] = new double[6];
		Path2D path = null;
		for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
			switch (i.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				path = new Path2D.Double(i.getWindingRule());
				result.add(path);
				path.moveTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				path.lineTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_QUADTO:
				path.quadTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CUBICTO:
				path.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				break;
			case PathIterator.SEG_CLOSE:
				path.closePath();
				System.out.println(new Area(path).isEmpty());
				break;
			}
		}
		return result;
	}

}
