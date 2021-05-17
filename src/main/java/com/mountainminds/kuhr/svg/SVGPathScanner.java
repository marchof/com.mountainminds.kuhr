package com.mountainminds.kuhr.svg;

class SVGPathScanner {

	private String d;

	private int pos = 0;

	SVGPathScanner(String d) {
		this.d = d;
	}

	int nextCommand() {
		return pop();
	}

	double nextNumber(double rel) {
		return nextNumber() + rel;
	}

	double nextNumber() {
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

	private int pop() {
		while (pos < d.length()) {
			char c = d.charAt(pos++);
			if (!Character.isWhitespace(c)) {
				return c;
			}
		}
		return -1;
	}

	private void push() {
		pos--;
	}

}
