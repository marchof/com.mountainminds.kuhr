package com.mountainminds.kuhr;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.zxing.common.BitMatrix;

public class QRShape {

	private QRMatrix matrix;

	private Supplier<Shape> posShape = position(0.0, 0.0, 0.0);

	private Supplier<Shape> dotShape = square();

	private Function<Shape, Shape> dotTransformation = Function.identity();

	private Shape logo = null;

	private double padding = 0.0;

	public QRShape(QRMatrix matrix) {
		this.matrix = matrix;
	}

	public void setDotShape(Supplier<Shape> dotShape) {
		this.dotShape = dotShape;
	}

	public void setPosShape(Supplier<Shape> posShape) {
		this.posShape = posShape;
	}

	public void setDotTransformation(Function<Shape, Shape> dotTransformation) {
		this.dotTransformation = dotTransformation;
	}

	public void setLogo(Shape logo, double padding) {
		this.logo = logo;
		this.padding = padding;
	}

	public Shape createShape() {
		BitMatrix m = matrix.getDataMatrix();
		Area area = new Area();
		for (int x = 0; x < m.getWidth(); x++) {
			for (int y = 0; y < m.getHeight(); y++) {
				if (m.get(x, y)) {
					Area a = new Area(dotShape.get());
					a.transform(AffineTransform.getTranslateInstance(x, y));
					area.add(a);
				}
			}
		}
		area = new Area(dotTransformation.apply(area));
		for (Rectangle pos : matrix.getPositions()) {
			Area a = new Area(posShape.get());
			a.transform(AffineTransform.getTranslateInstance(pos.x, pos.y));
			area.add(a);
		}
		if (logo != null) {
			Rectangle2D src = logo.getBounds2D();
			Rectangle dst = matrix.getLogo();
			double scale = Math.min((dst.getWidth() - 2 * padding) / src.getWidth(),
					(dst.getHeight() - 2 * padding) / src.getHeight());
			AffineTransform t = AffineTransform.getTranslateInstance(dst.getCenterX(), dst.getCenterY());
			t.concatenate(AffineTransform.getScaleInstance(scale, scale));
			t.concatenate(AffineTransform.getTranslateInstance(-src.getCenterX(), -src.getCenterY()));
			Area logoarea = new Area(logo);
			logoarea.transform(t);
			area.add(logoarea);
		}
		return area;
	}

	public static Supplier<Shape> position(double r1, double r2, double r3) {
		return () -> {
			Area pos = new Area();
			pos.add(new Area(new RoundRectangle2D.Double(0, 0, 7, 7, r1, r1)));
			pos.subtract(new Area(new RoundRectangle2D.Double(1, 1, 5, 5, r2, r2)));
			pos.add(new Area(new RoundRectangle2D.Double(2, 2, 3, 3, r3, r3)));
			return pos;
		};
	}

	public static Supplier<Shape> square() {
		return square(1);
	}

	public static Supplier<Shape> square(double size) {
		return () -> new Rectangle2D.Double(0, 0, size, size);
	}

	public static Supplier<Shape> circle(double size) {
		return () -> new Ellipse2D.Double(0, 0, size, size);
	}

}
