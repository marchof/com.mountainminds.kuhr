package com.mountainminds.kuhr.svg;

class SVGTransformScanner {

	private final String t;
	private int pos = 0;

	SVGTransformScanner(String text) {
		this.t = text;
	}

	String op() {
		skipWhitespaces();
		if (pos == t.length()) {
			return null;
		}
		StringBuilder op = new StringBuilder();
		int c;
		while (Character.isJavaIdentifierPart(c = pop())) {
			op.append((char) c);
		}
		if (c != -1) {
			push();
		}
		return op.toString();
	}

	double number() {
		skipWhitespaces();
		StringBuilder numstr = new StringBuilder();
		int c;
		done: while ((c = pop()) != -1) {
			if (isDigit(c)) {
				numstr.append((char) c);
			} else {
				push();
				break done;
			}
		}
		return Double.parseDouble(numstr.toString());
	}

	double number(double defaultValue) {
		skipWhitespaces();
		if (isDigit(pop())) {
			push();
			return number();
		} else {
			push();
			return defaultValue;
		}
	}

	void ordinal(char c) {
		skipWhitespaces();
		if (c != pop()) {
			throw new IllegalArgumentException("Missing '%s' in '%s'".formatted(c, t));
		}
	}

	private void skipWhitespaces() {
		while (pos < t.length() && isWhitespaceOrComma(t.charAt(pos))) {
			pos++;
		}
	}

	private boolean isWhitespaceOrComma(int c) {
		return Character.isWhitespace(c) || c == ',';
	}

	private boolean isDigit(int c) {
		switch (c) {
		case '+':
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '.':
			return true;
		}
		return false;
	}

	private int pop() {
		if (pos < t.length()) {
			return t.charAt(pos++);
		}
		return -1;
	}

	private void push() {
		pos--;
	}

}
