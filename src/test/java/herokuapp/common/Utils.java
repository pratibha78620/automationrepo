package herokuapp.common;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public final class Utils {
	private static Charset CHARSET = Charset.forName("UTF-8");
	private Utils() {
		throw new UnsupportedOperationException("Utils class shouldn't be instantiated");
	}

	/**
	 * Converts a string to a URI.
	 *
	 * @param uri
	 * @return
	 */
	public static URI convertStringToUri(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI syntax exception: " + uri);
		}
	}

	/**
	 * @return A String representing the current time in milliseconds since the Java epoch. Waits for a millisecond,
	 * so as to guarantee a unique timestamp.
	 */
	public static synchronized String uniqueDateTimeStamp() {
		timeDelay(1);
		return String.valueOf((new DateTime(org.joda.time.DateTimeZone.UTC)).getMillis());
	}

	public static synchronized String uniqueDateTimeStampNoMillis(Integer seconds) {
		timeDelay(1000);
		return String.valueOf((System.currentTimeMillis() - (1000 * seconds)) / 1000L);
	}

	/**
	 * @return The current time, formatted in ISO 8601 - "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
	 */
	public static String getIsoTimestamp(Integer seconds) {
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZone(DateTimeZone.getDefault());
		DateTime dt = new DateTime();
		return fmt.print(dt.minusSeconds(seconds));
	}

	public static void timeDelay(int millisecs) {
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Method to generate the unique id for automation content.
	 * @return
	 */
	public static String getUniqueId() {
		return "AT__TEST_" + Utils.uniqueDateTimeStamp();
	}


	}
