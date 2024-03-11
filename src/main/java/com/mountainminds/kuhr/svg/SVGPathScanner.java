package com.mountainminds.kuhr.svg;

import java.util.NoSuchElementException;

class SVGPathScanner {

	private final String d;

	private int pos = 0;

	SVGPathScanner(String d) {
		this.d = d;
	}

	boolean hasMoreTokens() {
		skipWhitespaces();
		return pos < d.length();
	}

	int nextCommand() {
		if (!hasMoreTokens()) {
			throw new NoSuchElementException();
		}
		return pop();
	}

	boolean nextIsNumber() {
		skipWhitespaces();
		if (pos < d.length()) {
			switch (d.charAt(pos)) {
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
			case '-':
				return true;
			}
		}
		return false;
	}

	double nextNumber(double rel) {
		return nextNumber() + rel;
	}

	double nextNumber() {
		if (!hasMoreTokens()) {
			throw new NoSuchElementException();
		}
		StringBuilder numstr = new StringBuilder();
		int c;
		done: while ((c = pop()) != -1) {
			switch (c) {
			default:
				push();
			case ',':
				break done;
			case '-':
				if (numstr.length() > 0) {
					push();
					break done;
				}
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
				numstr.append((char) c);
				break;
			}
		}
		return Double.parseDouble(numstr.toString());
	}

	private void skipWhitespaces() {
		while (pos < d.length() && Character.isWhitespace(d.charAt(pos))) {
			pos++;
		}
	}

	private int pop() {
		if (pos < d.length()) {
			return d.charAt(pos++);
		}
		return -1;
	}

	private void push() {
		pos--;
	}

}
