package com.mountainminds.kuhr.svg;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

import org.w3c.dom.Node;

final class DomReader {

	private DomReader() {
	}

	public static Iterable<Node> children(Node parent) {
		return () -> new Iterator<Node>() {

			private Node cursor = parent.getFirstChild();

			@Override
			public boolean hasNext() {
				return cursor != null;
			}

			@Override
			public Node next() {
				Node next = cursor;
				cursor = cursor.getNextSibling();
				return next;
			}
		};
	}

	public static Function<String, Optional<String>> attributes(Node node) {
		return name -> {
			Node item = node.getAttributes().getNamedItem(name);
			return item == null ? Optional.empty() : Optional.of(item.getTextContent());
		};
	}

	public static String attr(Node node, String name, String defaltValue) {
		var t = node.getAttributes().getNamedItem(name);
		return t == null ? defaltValue : t.getTextContent();
	}

	public static String attr(Node node, String name) {
		var t = node.getAttributes().getNamedItem(name);
		if (t == null) {
			throw new IllegalArgumentException("Missing attribute " + name);
		}
		return t.getTextContent();
	}

	public static double doubleAttr(Node node, String name, double defaltValue) {
		var t = node.getAttributes().getNamedItem(name);
		return t == null ? defaltValue : Double.parseDouble(t.getTextContent());
	}

	public static double doubleAttr(Node node, String name) {
		return Double.parseDouble(attr(node, name));
	}

}
