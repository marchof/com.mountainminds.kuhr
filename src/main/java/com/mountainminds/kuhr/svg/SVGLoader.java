package com.mountainminds.kuhr.svg;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;

public class SVGLoader {

	private Area combinedShape = new Area();

	public SVGLoader() {
	}

	public void load(Path path) throws Exception {
		try (InputStream in = Files.newInputStream(path)) {
			load(in);
		}
	}

	public void load(InputStream in) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		element(builder.parse(in), new SVGStyles());
	}

	private void element(Node element, SVGStyles styles) {
		if (element.getNodeType() == Node.ELEMENT_NODE) {
			styles.read(element);
			switch (element.getNodeName()) {
			case "path":
				combine(path(element), styles);
				break;
			case "polygon":
				combine(polygon(element), styles);
				break;
			case "rect":
				combine(rect(element), styles);
				break;
			}
		}
		for (Node child : DomReader.children(element)) {
			element(child, new SVGStyles(styles));
		}
	}

	/**
	 * https://www.w3.org/TR/SVG2/paths.html
	 */
	private Shape path(Node node) {
		SVGPathScanner scanner = new SVGPathScanner(node, "d");
		double lastX = 0.0, lastY = 0.0, lastBezierX = 0.0, lastBezierY = 0.0;
		double cX1, cY1;
		Path2D path = new Path2D.Double();
		while (scanner.hasMoreTokens()) {
			int command = scanner.nextCommand();
			do {
				switch (command) {
				case 'M':
					path.moveTo( //
							lastX = scanner.nextNumber(), //
							lastY = scanner.nextNumber());
					break;
				case 'm':
					path.moveTo( //
							lastX = scanner.nextNumber(lastX), //
							lastY = scanner.nextNumber(lastY));
					break;
				case 'L':
					path.lineTo( //
							lastX = scanner.nextNumber(), //
							lastY = scanner.nextNumber());
					break;
				case 'l':
					path.lineTo( //
							lastX = scanner.nextNumber(lastX), //
							lastY = scanner.nextNumber(lastY));
					break;
				case 'H':
					path.lineTo(lastX = scanner.nextNumber(), lastY);
					break;
				case 'h':
					path.lineTo(lastX = scanner.nextNumber(lastX), lastY);
					break;
				case 'V':
					path.lineTo(lastX, lastY = scanner.nextNumber());
					break;
				case 'v':
					path.lineTo(lastX, lastY = scanner.nextNumber(lastY));
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
				case 'Z':
				case 'z':
					path.closePath();
					break;
				default:
					throw new IllegalArgumentException("Unsupported Command: " + (char) command + " in "
							+ node.getAttributes().getNamedItem("d").getTextContent());
				}
			} while (scanner.nextIsNumber());
		}
		return path;
	}

	/**
	 * https://www.w3.org/TR/SVG2/shapes.html#PolygonElement
	 */
	private Shape polygon(Node node) {
		SVGPathScanner scanner = new SVGPathScanner(node, "points");
		Path2D path = new Path2D.Double();
		path.moveTo(scanner.nextNumber(), scanner.nextNumber());
		while (scanner.hasMoreTokens()) {
			path.lineTo(scanner.nextNumber(), scanner.nextNumber());
		}
		path.closePath();
		return path;
	}

	/**
	 * https://www.w3.org/TR/SVG2/shapes.html#RectElement
	 */
	private Shape rect(Node node) {
		double x = Double.parseDouble(node.getAttributes().getNamedItem("x").getTextContent());
		double y = Double.parseDouble(node.getAttributes().getNamedItem("y").getTextContent());
		double w = Double.parseDouble(node.getAttributes().getNamedItem("width").getTextContent());
		double h = Double.parseDouble(node.getAttributes().getNamedItem("height").getTextContent());
		return new Rectangle2D.Double(x, y, w, h);
	}

	private void combine(Shape shape, SVGStyles styles) {
		if (styles.getFill().isBlack()) {
			combinedShape.add(new Area(shape));
		}
		if (styles.getFill().isWhite()) {
			combinedShape.subtract(new Area(shape));
		}
		if (styles.getStroke().isBlack()) {
			combinedShape.add(new Area(styles.createStroke().createStrokedShape(shape)));
		}
		if (styles.getStroke().isWhite()) {
			combinedShape.subtract(new Area(styles.createStroke().createStrokedShape(shape)));
		}
	}

	public Shape getShape() {
		return combinedShape;
	}

}
