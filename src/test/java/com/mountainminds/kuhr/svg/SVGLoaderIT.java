package com.mountainminds.kuhr.svg;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

public class SVGLoaderIT {

	private static String INPATTERN = "https://dev.w3.org/SVG/profiles/1.2T/test/svg/%s.svg";

	private static String OUTPATTERN = "target/it/svg/%s.png";

	private static Path DIFFHTML = Path.of("target/it/svg/diff.html");

	private static List<String> TESTCASES = List.of( //
			"coords-trans-01-t", //
			"coords-trans-02-t", //
			"coords-trans-03-t", //
			"coords-trans-04-t", //
			"coords-trans-05-t", //
			"coords-trans-06-t", //
			"coords-trans-07-t", //
			"coords-trans-08-t", //
			"coords-trans-09-t", //
			"paths-data-01-t", //
			"paths-data-02-t", //
			"paths-data-04-t", //
			"paths-data-05-t", //
			"paths-data-06-t", //
			"paths-data-07-t", //
			"paths-data-08-t", //
			"paths-data-09-t", //
			"paths-data-10-t", //
			"paths-data-12-t", //
			"paths-data-13-t", //
			"paths-data-14-t", //
			"paths-data-15-t", //
			"shapes-line-01-t", //
			"shapes-line-02-t", //
			"shapes-polygon-01-t", //
			// "shapes-polygon-02-t", // contains expected error
			"shapes-polyline-01-t", //
			// "shapes-polyline-02-t", // contains expected error
			"shapes-rect-01-t", //
			"shapes-rect-02-t", //
			"shapes-rect-03-t" //
	);

	private static void convertToPNG(String testcase) throws IOException {
		var image = new BufferedImage(480 * 2, 360 * 2, BufferedImage.TYPE_4BYTE_ABGR);
		var g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		var loader = new SVGLoader((SVGStyles styles, AffineTransform transform, Shape shape) -> {
			g.setTransform(AffineTransform.getScaleInstance(2.0, 2.0));
			g.transform(transform);

			g.setColor(styles.getFill().toAWT());
			g.fill(shape);

			g.setColor(styles.getStroke().toAWT());
			g.setStroke(styles.createStroke());
			g.draw(shape);
		});

		try (var in = new URL(INPATTERN.formatted(testcase)).openStream()) {
			loader.load(in);
		}

		var outpath = Path.of(OUTPATTERN.formatted(testcase));
		Files.createDirectories(outpath.getParent());
		try (var out = Files.newOutputStream(outpath)) {
			ImageIO.write(image, "PNG", out);
		}
	}

	private static void writeDiffHTML() throws IOException {
		try (var w = new PrintWriter(Files.newBufferedWriter(DIFFHTML))) {
			w.println("<html>");
			w.println("<body>");
			w.println("<table>");
			for (var name : TESTCASES) {
				w.println("<tr>");
				w.println("<td><img src=\"%s.png\" width=\"480\" height=\"360\"></td>".formatted(name));
				w.println(
						"<td><img src=\"%s\" width=\"480\" height=\"360\"></td>".formatted(INPATTERN.formatted(name)));
				w.println("</tr>");
			}
			w.println("</table>");
			w.println("</body>");
			w.println("</html>");
		}
	}

	public static void main(String[] args) throws IOException {
		for (var name : TESTCASES) {
			System.out.println(name);
			convertToPNG(name);
		}
		writeDiffHTML();
	}

}
