package jp.ac.keio.sfc.ht.sox.soxlib;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utility {

	  /**
	   * The time (in milliseconds) to sleep when inside a spin forever loop. Why 60
	   * seconds? Why not? Could pick any arbitrarily large number.
	   */
	  public static final int SPIN_LOOP_SLEEP_MS = 60000;

	  // A simple pattern match to catch a subset of invalid timestamp formats.
	  public static final Pattern SIMPLE_TIMESTAMP_CHECK =
	      Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");

	  /**
	   * Get the time now in a string format suitable for use in SOX timestamp
	   * fields.
	   *
	   * @return UTC timestamp to the nearest millisecond as a string.
	   */
	  public static final String getTimestampNow() {
	    DateTime now = new DateTime(DateTimeZone.UTC);
	    return now.toString(ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC));
	  }

	  /**
	   * Convert from "unix time" (utc milliseconds since 1970) into a string format
	   * for use in SOX timestamp fields.
	   *
	   * @param timestampMsUtc utc time in milliseconds since Jan 1, 1970 (aka unix
	   *        time).
	   * @return UTC timestamp to the nearest millisecond as a string.
	   */
	  public static final String getTimestampFromUnixTime(long timestampMsUtc) {
	    DateTime dt = new DateTime(timestampMsUtc, DateTimeZone.UTC);
	    return dt.toString(ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC));
	  }

	  /**
	   * Convert from a SOX string timestamp into a "unix timestamp" (milliseconds
	   * since 1970).
	   *
	   * @param timestamp as a SOX string (i.e. from a timestamp field).
	   * @return milliseconds since Jan 1, 1970 UTC.
	   */
	  public static final long getUnixTimeFromTimestamp(String timestamp) {
	    DateTime dt = new DateTime(timestamp);
	    return dt.getMillis();
	  }

	  /**
	   * Verify that the string provided is a valid RFC 3339 timestamp string and
	   * converts the string into the UTC timezone for use by the SOX library.
	   *
	   * @param timestamp string to be verified.
	   * @return timestamp converted to UTC in the SOX format.
	   * @throws IllegalArgumentException if the string cannot be converted to UTC.
	   */
	  public static final String verifyTimestamp(String timestamp) throws IllegalArgumentException {
	    Matcher m = Utility.SIMPLE_TIMESTAMP_CHECK.matcher(timestamp);
	    if (!m.find()) {
	      throw new IllegalArgumentException("timestamp not valid");
	    }
	    // convert entered value to the ISO standard and make that
	    // the official value
	    DateTime dt;
	    try {
	      dt = new DateTime(timestamp);
	    } catch (IllegalArgumentException e) {
	      throw new IllegalArgumentException("timestamp not valid");
	    }

	    return dt.toString(ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC));
	  }
	
	public static final boolean simpleSleepMs(int sleepMs) {
	    try {
	      Thread.sleep(sleepMs);
	      return true;
	    } catch (InterruptedException e) {
	      return false;
	    }
	  }
}
