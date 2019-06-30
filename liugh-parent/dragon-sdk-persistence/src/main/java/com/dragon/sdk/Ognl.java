package com.dragon.sdk;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class Ognl {

	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object o) throws IllegalArgumentException {
		if (o == null) {
			return true;
		}

		if ((o instanceof String)) {
			if (((String) o).trim().length() == 0) {
				return true;
			}
		} else if ((o instanceof Collection)) {
			if (((Collection) o).isEmpty()) {
				return true;
			}
		} else if (o.getClass().isArray()) {
			if (Array.getLength(o) == 0) {
				return true;
			}
		} else if ((o instanceof Map)) {
			if (((Map) o).isEmpty()) {
				return true;
			}
		} else {
			return false;
		}

		return false;
	}

	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	public static boolean isNotBlank(Object o) {
		return !isBlank(o);
	}

	public static boolean isNumber(Object o) {
		if (o == null) {
			return false;
		}
		if ((o instanceof Number)) {
			return true;
		}
		if ((o instanceof String)) {
			String str = (String) o;
			if (str.length() == 0) {
				return false;
			}
			if (str.trim().length() == 0) {
				return false;
			}
			try {
				Double.parseDouble(str);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

	public static boolean isBlank(Object o) {
		if (o == null) {
			return true;
		}
		if ((o instanceof String)) {
			String str = (String) o;
			return isBlank(str);
		}
		return false;
	}

	public static boolean isBlank(String str) {
		if ((str == null) || (str.length() == 0)) {
			return true;
		}

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
