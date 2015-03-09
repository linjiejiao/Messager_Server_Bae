package cn.ljj.server.log;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
	private static Logger logger = Logger.getLogger("java");
	private static DateFormat mDateFormat = DateFormat
			.getDateInstance(DateFormat.SHORT);

	public static void i(String tag, String log) {
		Date date = new Date(System.currentTimeMillis());
		logger.log(Level.INFO, mDateFormat.format(date) + " :" + tag + " - "
				+ log);
	}

	public static void e(String tag, String log) {
		Date date = new Date(System.currentTimeMillis());
		logger.log(Level.SEVERE, mDateFormat.format(date) + " :" + tag + " - "
				+ log);
	}

	public static void w(String tag, String log) {
		Date date = new Date(System.currentTimeMillis());
		logger.log(Level.WARNING, mDateFormat.format(date) + " :" + tag + " - "
				+ log);
	}

	public static void d(String tag, String log) {
		Date date = new Date(System.currentTimeMillis());
		logger.log(Level.FINE, mDateFormat.format(date) + " :" + tag + " - "
				+ log);
	}
}
