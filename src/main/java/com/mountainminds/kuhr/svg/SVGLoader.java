package com.mountainminds.kuhr.svg;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

class SVGLoader {

	interface ShapeConsumer {
		void consume(SVGStyles styles, AffineTransform transform, Shape shape);
	}

	private ShapeConsumer consumer;

	public SVGLoader(ShapeConsumer consumer) {
		this.consumer = consumer;
	}

	public void load(Path path) throws IOException {
		try (InputStream in = Files.newInputStream(path)) {
			load(in);
		}
	}

	public void load(InputStream in) throws IOException {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			element(builder.parse(in), new SVGStyles(), new SVGTransform());
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException(e);
		}
	}

	private void element(Node element, SVGStyles styles, SVGTransform transform) {
		if (element.getNodeType() == Node.ELEMENT_NODE) {
			styles = styles.with(element);
			transform = transform.with(element);
			switch (element.getNodeName()) {
			case "path":
				consumer.consume(styles, transform.getTransform(), path(element));
				break;
			case "polygon":
				consumer.consume(styles, transform.getTransform(), polygon(element));
				break;
			case "rect":
				consumer.consume(styles, transform.getTransform(), rect(element));
				break;
			case "line":
				consumer.consume(styles, transform.getTransform(), line(element));
				break;
			case "polyline":
				consumer.consume(styles, transform.getTransform(), polyline(element));
				break;
			case "circle":
				consumer.consume(styles, transform.getTransform(), circle(element));
				break;
			}
		}
		for (Node child : DomReader.children(element)) {
			element(child, styles, transform);
		}
	}

	/**
	 * https://www.w3.org/TR/SVG2/paths.html
	 */
	private Shape path(Node node) {
		String d = DomReader.attr(node, "d", "");
		SVGPathScanner scanner = new SVGPathScanner(d);
		double lastX = 0.0, lastY = 0.0, lastBezierX = 0.0, lastBezierY = 0.0;
		double cX1, cY1;
		Path2D path = new Path2D.Double();
		path.setWindingRule(getWindingRule(node));
		while (scanner.hasMoreTokens()) {
			int command = scanner.nextCommand();
			boolean first = true;
			do {
				switch (command) {
				case 'M':
					lastX = lastBezierX = scanner.nextNumber();
					lastY = lastBezierY = scanner.nextNumber();
					if (first) {
						path.moveTo(lastX, lastY);
					} else {
						path.lineTo(lastX, lastY);
					}
					break;
				case 'm':
					lastX = lastBezierX = scanner.nextNumber(lastX);
					lastY = lastBezierY = scanner.nextNumber(lastY);
					if (first) {
						path.moveTo(lastX, lastY);
					} else {
						path.lineTo(lastX, lastY);
					}
					break;
				case 'L':
					path.lineTo( //
							lastX = lastBezierX = scanner.nextNumber(), //
							lastY = lastBezierY = scanner.nextNumber());
					break;
				case 'l':
					path.lineTo( //
							lastX = lastBezierX = scanner.nextNumber(lastX), //
							lastY = lastBezierY = scanner.nextNumber(lastY));
					break;
				case 'H':
					path.lineTo( //
							lastX = lastBezierX = scanner.nextNumber(), //
							lastY);
					break;
				case 'h':
					path.lineTo( //
							lastX = lastBezierX = scanner.nextNumber(lastX), //
							lastY);
					break;
				case 'V':
					path.lineTo( //
							lastX, //
							lastY = lastBezierY = scanner.nextNumber());
					break;
				case 'v':
					path.lineTo( //
							lastX, //
							lastY = lastBezierY = scanner.nextNumber(lastY));
					break;
				case 'C':
					path.curveTo( //
							scanner.nextNumber(), //
							scanner.nextNumber(), //
							lastBezierX = scanner.nextNumber(), //
							lastBezierY = scanner.nextNumber(), //
							lastX = scanner.nextNumber(), //
							lastY = scanner.nextNumber());
					break;
				case 'c':
					path.curveTo( //
							scanner.nextNumber(lastX), //
							scanner.nextNumber(lastY), //
							lastBezierX = scanner.nextNumber(lastX), //
							lastBezierY = scanner.nextNumber(lastY), //
							lastX = scanner.nextNumber(lastX), //
							lastY = scanner.nextNumber(lastY));
					break;
				case 'S':
					cX1 = lastBezierX + 2 * (lastX - lastBezierX);
					cY1 = lastBezierY + 2 * (lastY - lastBezierY);
					path.curveTo(cX1, cY1, //
							lastBezierX = scanner.nextNumber(), //
							lastBezierY = scanner.nextNumber(), //
							lastX = scanner.nextNumber(), //
							lastY = scanner.nextNumber());
					break;
				case 's':
					cX1 = lastBezierX + 2 * (lastX - lastBezierX);
					cY1 = lastBezierY + 2 * (lastY - lastBezierY);
					path.curveTo(cX1, cY1, //
							lastBezierX = scanner.nextNumber(lastX), //
							lastBezierY = scanner.nextNumber(lastY), //
							lastX = scanner.nextNumber(lastX), //
							lastY = scanner.nextNumber(lastY));
					break;
				case 'Q':
					path.quadTo( //
							lastBezierX = scanner.nextNumber(), //
							lastBezierY = scanner.nextNumber(), //
							lastX = scanner.nextNumber(), //
							lastY = scanner.nextNumber());
					break;
				case 'q':
					path.quadTo( //
							lastBezierX = scanner.nextNumber(lastX), //
							lastBezierY = scanner.nextNumber(lastY), //
							lastX = scanner.nextNumber(lastX), //
							lastY = scanner.nextNumber(lastY));
					break;
				case 'T':
					cX1 = lastBezierX + 2 * (lastX - lastBezierX);
					cY1 = lastBezierY + 2 * (lastY - lastBezierY);
					path.quadTo( //
							lastBezierX = cX1, //
							lastBezierY = cY1, //
							lastX = scanner.nextNumber(), //
							lastY = scanner.nextNumber());
					break;
				case 't':
					cX1 = lastBezierX + 2 * (lastX - lastBezierX);
					cY1 = lastBezierY + 2 * (lastY - lastBezierY);
					path.quadTo( //
							lastBezierX = cX1, //
							lastBezierY = cY1, //
							lastX = scanner.nextNumber(lastX), //
							lastY = scanner.nextNumber(lastY));
					break;
				case 'Z':
				case 'z':
					path.closePath();
					var cp = path.getCurrentPoint();
					lastX = lastBezierX = cp.getX();
					lastY = lastBezierY = cp.getY();
					break;
				default:
					throw new IllegalArgumentException("Unsupported Command: %s in %s".formatted((char) command, d));
				}
				first = false;
			} while (scanner.nextIsNumber());
		}
		return path;
	}

	/**
	 * https://www.w3.org/TR/SVG2/shapes.html#PolygonElement
	 */
	private Shape polygon(Node node) {
		SVGPathScanner scanner = new SVGPathScanner(DomReader.attr(node, "points", ""));
		Path2D path = new Path2D.Double();
		path.setWindingRule(getWindingRule(node));
		if (scanner.hasMoreTokens()) {
			path.moveTo(scanner.nextNumber(), scanner.nextNumber());
			while (scanner.hasMoreTokens()) {
				path.lineTo(scanner.nextNumber(), scanner.nextNumber());
			}
			path.closePath();
		}
		return path;
	}

	/**
	 * https://www.w3.org/TR/SVG2/shapes.html#RectElement
	 */
	private Shape rect(Node node) {
		double x = DomReader.doubleAttr(node, "x", 0);
		double y = DomReader.doubleAttr(node, "y", 0);
		double w = DomReader.doubleAttr(node, "width");
		double h = DomReader.doubleAttr(node, "height");
		double rx = DomReader.doubleAttr(node, "rx", -1);
		double ry = DomReader.doubleAttr(node, "ry", -1);
		if (rx != -1 && ry == -1) {
			ry = rx;
		}
		if (rx == -1 && ry != -1) {
			rx = ry;
		}
		rx = Math.max(0, rx);
		ry = Math.max(0, ry);
		if (rx > 0 || ry > 0) {
			return new RoundRectangle2D.Double(x, y, w, h, rx * 2, ry * 2);
		} else {
			return new Rectangle2D.Double(x, y, w, h);
		}
	}

	/**
	 * https://www.w3.org/TR/SVG2/shapes.html#LineElement
	 */
	private Shape line(Node node) {
		double x1 = DomReader.doubleAttr(node, "x1", 0);
		double y1 = DomReader.doubleAttr(node, "y1", 0);
		double x2 = DomReader.doubleAttr(node, "x2", 0);
		double y2 = DomReader.doubleAttr(node, "y2", 0);
		return new Line2D.Double(x1, y1, x2, y2);
	}

	/**
	 * https://www.w3.org/TR/SVGTiny12/shapes.html#PolylineElement
	 */
	private Shape polyline(Node node) {
		SVGPathScanner scanner = new SVGPathScanner(DomReader.attr(node, "points", ""));
		Path2D path = new Path2D.Double();
		if (scanner.hasMoreTokens()) {
			path.moveTo(scanner.nextNumber(), scanner.nextNumber());
			while (scanner.hasMoreTokens()) {
				path.lineTo(scanner.nextNumber(), scanner.nextNumber());
			}
		}
		return path;
	}

	/**
	 * https://www.w3.org/TR/SVGTiny12/shapes.html#CircleElement
	 */
	private Shape circle(Node node) {
		double cx = DomReader.doubleAttr(node, "cx", 0);
		double cy = DomReader.doubleAttr(node, "cy", 0);
		double r = DomReader.doubleAttr(node, "r", 0);
		if (r == 0) {
			return new Area();
		}
		return new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2);
	}

	private int getWindingRule(Node node) {
		String text = DomReader.attr(node, "fill-rule", "nonzero");
		switch (text) {
		case "nonzero":
			return Path2D.WIND_NON_ZERO;
		case "evenodd":
			return Path2D.WIND_EVEN_ODD;
		}
		throw new IllegalArgumentException("Unknown fill-rule: " + text);
	}

}
