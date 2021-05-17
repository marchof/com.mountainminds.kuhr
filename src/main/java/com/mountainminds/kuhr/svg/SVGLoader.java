package com.mountainminds.kuhr.svg;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
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
				path(element, styles);
				break;
			}
		}
		for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
			element(child, new SVGStyles(styles));
		}
	}

	private void path(Node path, SVGStyles styles) {
		SVGPathScanner scanner = new SVGPathScanner(path.getAttributes().getNamedItem("d").getTextContent());
		int command;
		double lastX = 0.0, lastY = 0.0, lastBezierX = 0.0, lastBezierY = 0.0;
		double cX1, cY1;
		Path2D.Double out = new Path2D.Double();
		while ((command = scanner.nextCommand()) != -1) {
			switch (command) {
			case 'M':
				out.moveTo( //
						lastX = scanner.nextNumber(), //
						lastY = scanner.nextNumber());
				break;
			case 'm':
				out.moveTo( //
						lastX = scanner.nextNumber(lastX), //
						lastY = scanner.nextNumber(lastY));
				break;
			case 'L':
				out.lineTo( //
						lastX = scanner.nextNumber(), //
						lastY = scanner.nextNumber());
				break;
			case 'l':
				out.lineTo( //
						lastX = scanner.nextNumber(lastX), //
						lastY = scanner.nextNumber(lastY));
				break;
			case 'H':
				out.lineTo(lastX = scanner.nextNumber(), lastY);
				break;
			case 'h':
				out.lineTo(lastX = scanner.nextNumber(lastX), lastY);
				break;
			case 'V':
				out.lineTo(lastX, lastY = scanner.nextNumber());
				break;
			case 'v':
				out.lineTo(lastX, lastY = scanner.nextNumber(lastY));
				break;
			case 'C':
				out.curveTo( //
						scanner.nextNumber(), //
						scanner.nextNumber(), //
						lastBezierX = scanner.nextNumber(), //
						lastBezierY = scanner.nextNumber(), //
						lastX = scanner.nextNumber(), //
						lastY = scanner.nextNumber());
				break;
			case 'c':
				out.curveTo( //
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
				out.curveTo(cX1, cY1, //
						lastBezierX = scanner.nextNumber(), //
						lastBezierY = scanner.nextNumber(), //
						lastX = scanner.nextNumber(), //
						lastY = scanner.nextNumber());
				break;
			case 's':
				cX1 = lastBezierX + 2 * (lastX - lastBezierX);
				cY1 = lastBezierY + 2 * (lastY - lastBezierY);
				out.curveTo(cX1, cY1, //
						lastBezierX = scanner.nextNumber(lastX), //
						lastBezierY = scanner.nextNumber(lastY), //
						lastX = scanner.nextNumber(lastX), //
						lastY = scanner.nextNumber(lastY));
				break;
			case 'Z':
			case 'z':
				out.closePath();
				break;
			default:
				throw new IllegalArgumentException("Unsupported Command: " + (char) command);
			}
		}

		if (styles.getFill().isBlack()) {
			combinedShape.add(new Area(out));
		}
		if (styles.getFill().isWhite()) {
			combinedShape.subtract(new Area(out));
		}
		if (styles.getStroke().isBlack()) {
			combinedShape.add(new Area(styles.createStroke().createStrokedShape(out)));
		}
		if (styles.getStroke().isWhite()) {
			combinedShape.subtract(new Area(styles.createStroke().createStrokedShape(out)));
		}
	}

	public Shape getShape() {
		return combinedShape;
	}

}
