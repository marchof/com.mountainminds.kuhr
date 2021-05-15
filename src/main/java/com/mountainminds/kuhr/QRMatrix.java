package com.mountainminds.kuhr;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.qrcode.decoder.Decoder;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRMatrix {

	public static final int POS_SIZE = 7;

	private final String content;

	private final List<Rectangle> positions;

	private BitMatrix matrix;

	public QRMatrix(Object content, ErrorCorrectionLevel level) throws WriterException {
		this.content = content.toString();
		Map<EncodeHintType, ?> hints = Map.of( //
				EncodeHintType.ERROR_CORRECTION, level, //
				EncodeHintType.MARGIN, 0);
		Writer writer = new MultiFormatWriter();
		matrix = writer.encode(content.toString(), BarcodeFormat.QR_CODE, 1, 1, hints);
		positions = List.of( //
				new Rectangle(0, 0, POS_SIZE, POS_SIZE), //
				new Rectangle(matrix.getWidth() - POS_SIZE, 0, POS_SIZE, POS_SIZE), //
				new Rectangle(0, matrix.getHeight() - POS_SIZE, POS_SIZE, POS_SIZE));
	}

	public BitMatrix getDataMatrix() {
		BitMatrix m = matrix.clone();
		for (Rectangle pos : positions) {
			for (int x = pos.x; x < pos.x + pos.width; x++) {
				for (int y = pos.y; y < pos.y + pos.height; y++) {
					m.unset(x, y);
				}
			}
		}
		return m;
	}

	public List<Rectangle> getPositions() {
		return List.copyOf(this.positions);
	}

	public void addCenteredLogo() {
		for (int logosize = matrix.getWidth(); --logosize > 0;) {
			BitMatrix withLogo = withLogoSpace(logosize);
			if (validate(withLogo)) {
				this.matrix = withLogo;
				return;
			}
		}
	}

	public void addCenteredLogo(int logosize) {
		BitMatrix withLogo = withLogoSpace(logosize);
		if (!validate(withLogo)) {
			throw new IllegalArgumentException("Logo size too big, QR Code not readable.");
		}
		this.matrix = withLogo;
	}

	private BitMatrix withLogoSpace(int logosize) {
		BitMatrix matrix = this.matrix.clone();
		Point center = new Point(matrix.getWidth() / 2, matrix.getHeight() / 2);
		for (int x = 0; x < logosize; x++) {
			for (int y = 0; y < logosize; y++) {
				matrix.unset(center.x + x - logosize / 2, center.y + y - logosize / 2);
			}
		}
		return matrix;
	}

	private boolean validate(BitMatrix matrix) {
		try {
			return content.equals(decode(matrix.clone()));
		} catch (ChecksumException | FormatException e) {
			return false;
		}
	}

	private static String decode(BitMatrix matrix) throws ChecksumException, FormatException {
		Decoder decoder = new Decoder();
		DecoderResult result = decoder.decode(matrix);
		return result.getText();
	}

}
