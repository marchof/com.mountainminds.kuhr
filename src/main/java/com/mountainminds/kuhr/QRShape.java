package com.mountainminds.kuhr;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.zxing.common.BitMatrix;

public class QRShape {

	private QRMatrix matrix;

	private Supplier<Shape> posShape = position();

	private Supplier<Shape> dotShape = square();

	private Function<Shape, Shape> dotTransformation = Function.identity();

	private BiConsumer<BitMatrix, Consumer<Shape>> extraMatrixShapes;

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

	public void setExtraMatrixShapes(BiConsumer<BitMatrix, Consumer<Shape>> extraMatrixShapes) {
		this.extraMatrixShapes = extraMatrixShapes;
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
		if (extraMatrixShapes != null) {
			final Area a = area;
			extraMatrixShapes.accept(m, s -> a.add(new Area(s)));
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

	public static Supplier<Shape> position() {
		return position(0.0, 0.0, 0.0);
	}

	public static Supplier<Shape> position(double r1, double r2, double r3) {
		return position(1.0, r1, r2, r3);
	}

	public static Supplier<Shape> position(double width, double r1, double r2, double r3) {
		return () -> {
			Area pos = new Area();
			double w2 = width / 2.0;
			pos.add(new Area(new RoundRectangle2D.Double(0.5 - w2, 0, 6.0 + width, 6.0 + width, r1, r1)));
			pos.subtract(new Area(new RoundRectangle2D.Double(0.5 + w2, width, 6.0 - width, 6.0 - width, r2, r2)));
			pos.add(new Area(new RoundRectangle2D.Double(2.5 - w2, 2, 2.0 + width, 2.0 + width, r3, r3)));
			return pos;
		};
	}

	public static Supplier<Shape> rectangle(double width, double height) {
		return () -> new Rectangle2D.Double(0, 0, width, height);
	}

	public static Supplier<Shape> square(double size) {
		return rectangle(size, size);
	}

	public static Supplier<Shape> square() {
		return square(1);
	}

	public static Supplier<Shape> circle(double size) {
		return () -> new Ellipse2D.Double(0, 0, size, size);
	}

}
