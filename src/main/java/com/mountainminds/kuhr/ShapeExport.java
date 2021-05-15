package com.mountainminds.kuhr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.jfree.svg.SVGGraphics2D;

public class ShapeExport {

	public static BufferedImage toRaster(Shape shape, int scale) {
		Rectangle bounds = shape.getBounds();
		BufferedImage image = new BufferedImage(bounds.width * scale, bounds.height * scale,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);

		AffineTransform t = AffineTransform.getScaleInstance(scale, scale);
		t.concatenate(AffineTransform.getTranslateInstance(-bounds.x, -bounds.y));
		g.setTransform(t);

		g.fill(shape);
		return image;
	}

	public static void toPNG(Shape shape, int scale, OutputStream out) throws IOException {
		ImageIO.write(toRaster(shape, scale), "PNG", out);
	}

	public static void toSVG(Shape shape, int scale, Writer out) throws IOException {
		Rectangle bounds = shape.getBounds();
		SVGGraphics2D g = new SVGGraphics2D(bounds.width * scale, bounds.height * scale);
		g.setColor(Color.BLACK);
		AffineTransform t = AffineTransform.getScaleInstance(scale, scale);
		t.concatenate(AffineTransform.getTranslateInstance(-bounds.x, -bounds.y));
		g.setTransform(t);
		g.fill(shape);
		out.write(
				"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
		out.write(g.getSVGElement());
		out.write("\n");
	}

}
