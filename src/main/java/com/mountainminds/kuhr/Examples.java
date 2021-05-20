package com.mountainminds.kuhr;

import java.awt.Shape;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mountainminds.kuhr.svg.SVG;

public class Examples {

	static void example1() throws Exception {
		Wifi wifi = new Wifi(Wifi.Encryption.WPA, "kuhr", "password", false);

		QRMatrix qm = new QRMatrix(wifi, ErrorCorrectionLevel.M);

		QRShape qs = new QRShape(qm);
		qs.setDotShape(QRShape.square(0.9));

		Shape shape = qs.createShape();

		try (java.io.Writer out = Files.newBufferedWriter(Path.of("examples/qr1.svg"), StandardCharsets.UTF_8)) {
			ShapeExport.toSVG(shape, 8, out);
		}
	}

	static void example2() throws Exception {
		Wifi wifi = new Wifi(Wifi.Encryption.WPA, "kuhr", "password", false);

		QRMatrix qm = new QRMatrix(wifi, ErrorCorrectionLevel.M);

		QRShape qs = new QRShape(qm);
		qs.setPosShape(QRShape.position(2.0, 1.0, 1.0));
		qs.setDotShape(QRShape.circle(0.9));

		Shape shape = qs.createShape();

		try (java.io.Writer out = Files.newBufferedWriter(Path.of("examples/qr2.svg"), StandardCharsets.UTF_8)) {
			ShapeExport.toSVG(shape, 8, out);
		}
	}

	static void example3() throws Exception {
		String url = "https://github.com/marchof/com.mountainminds.kuhr";

		QRMatrix qm = new QRMatrix(url, ErrorCorrectionLevel.H);
		qm.addCenteredLogo();

		Function<Shape, Shape> dotTransformation = ShapeTransform.roundInnerCorners(0.5)
				.andThen(ShapeTransform.roundOuterCorners(0.999));

		QRShape qs = new QRShape(qm);
		qs.setPosShape(QRShape.position(2.0, 1.0, 3.0));
		qs.setDotTransformation(dotTransformation);
		qs.setLogo(SVG.toShape(Path.of("examples/cow.svg")), 1.0);

		Shape shape = qs.createShape();

		try (java.io.Writer out = Files.newBufferedWriter(Path.of("examples/qr3.svg"), StandardCharsets.UTF_8)) {
			ShapeExport.toSVG(shape, 8, out);
		}
	}

	static void example4() throws Exception {
		String url = "https://github.com/marchof/com.mountainminds.kuhr";

		QRMatrix qm = new QRMatrix(url, ErrorCorrectionLevel.Q);
		qm.addCenteredLogo();

		Function<Shape, Shape> dotTransformation = ShapeTransform.roundInnerCorners(0.999);

		QRShape qs = new QRShape(qm);
		qs.setPosShape(QRShape.position(0.0, 1.0, 1.0));
		qs.setDotTransformation(dotTransformation);
		qs.setLogo(SVG.toShape(Path.of("examples/fly.svg")), 1.0);

		Shape shape = qs.createShape();

		try (java.io.Writer out = Files.newBufferedWriter(Path.of("examples/qr4.svg"), StandardCharsets.UTF_8)) {
			ShapeExport.toSVG(shape, 8, out);
		}
	}

	static void example5() throws Exception {
		String url = "https://github.com/marchof/com.mountainminds.kuhr";

		QRMatrix qm = new QRMatrix(url, ErrorCorrectionLevel.M);
		qm.addCenteredLogo();

		Function<Shape, Shape> dotTransformation = ShapeTransform.roundInnerCorners(0.3);

		QRShape qs = new QRShape(qm);
		qs.setPosShape(QRShape.position(2.0, 1.0, 1.0));
		qs.setDotShape(QRShape.circle(0.8));
		qs.setDotTransformation(dotTransformation);
		qs.setLogo(SVG.toShape(Path.of("examples/calendar.svg")), 1.0);

		Shape shape = qs.createShape();

		try (java.io.Writer out = Files.newBufferedWriter(Path.of("examples/qr5.svg"), StandardCharsets.UTF_8)) {
			ShapeExport.toSVG(shape, 8, out);
		}
	}

	public static void main(String... args) throws Exception {
		example1();
		example2();
		example3();
		example4();
		example5();
	}

}
