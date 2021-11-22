package herokuapp.common;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public final class StringUtil {
	private static final Pattern SIMPLE_HTML_TAG_PATTERN = Pattern.compile("\\<.*?\\>");

	private StringUtil() {
		throw new UnsupportedOperationException("StringUtil class shouldn't be instantiated");
	}

	/**
	 * Splits a string based on its delimiter. Trims results and omits empty strings.
	 *
	 * @param delimiter       The delimiter to split the string on.
	 * @param delimitedString The string to split.
	 * @return The string split by its delimiter.
	 */
	public static List<String> split(String delimiter, String delimitedString) {
		if (delimitedString == null) {
			delimitedString = "";
		}

		return Lists.newArrayList(Splitter.on(delimiter)
				.trimResults()
				.omitEmptyStrings()
				.split(delimitedString));
	}

	/**
	 * Joins items with the specified separator. Skips nulls. Objects to be joined are converted using their
	 * toString() method.
	 *
	 * @param separator What to separate the objects by.
	 * @param items     The items to join together in a string.
	 * @return The joined string.
	 */
	public static String join(String separator, List<String> items) {
		return Joiner.on(separator).skipNulls().join(items);
	}

	/**
	 * @param str The string to strip non-ASCII characters from.
	 * @return A copy of the provided string, without the non-ASCII characters.
	 */
	public static String stripNonAsciiCharacters(String str) {
		checkNotNull(str);
		return str.replaceAll("[^\\x00-\\x7F]", "");
	}

	/**
	 * @param str The string to strip whitespace from.
	 * @return The string given, without whitespace. If the given string is null, returns null.
	 */
	public static String removeWhitespace(String str) {
		if (str == null) {
			return null;
		}
		return str.replaceAll("\\s+", "");
	}

	/**
	 * @param html
	 * @return
	 */
	public static boolean containsHtml(String html) {
		return html != null && SIMPLE_HTML_TAG_PATTERN.matcher(html).find();
	}

	/**
	 * Returns the given list, but with all adjacent whitespace replaced by a single space.
	 *
	 * @param list The list to sanitise.
	 * @return A new list, with the same strings as the original but with whitespace all replaced by a single space.
	 */
	public static List<String> sanitiseWhitespace(List<String> list) {
		List<String> newList = new ArrayList<>();

		for (String item : list) {
			newList.add(item.replaceAll("\\s+", StringUtils.EMPTY));
		}
		return newList;
	}

	/**
	 * Tests that the first string contains the second string, ignoring the case. Returns false if either
	 * of the strings are null.
	 * @param str1 The first string to compare.
	 * @param str2 The second string to compare.
	 * @return true if the first string contains the second string ignoring the case, false otherwise.
	 */
	public static boolean containsIgnoreCase(String str1, String str2) {
		if (str2 == null || str1 == null) {
			return false;
		}
		return str1.toLowerCase().contains(str2.toLowerCase());
	}
}
