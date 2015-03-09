package cn.ljj.server.database;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.ljj.server.config.Config;
import cn.ljj.server.database.TableDefines.*;
import cn.ljj.server.log.Log;

public class DatabaseFactory {
	public static final String TAG = "DatabaseFactory";
	public static final int DATABASE_TYPE_SQLITE = 0;
	public static final int DATABASE_TYPE_BAE = 1;
	private static final boolean isWindows;
	static {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		Log.i(TAG, "Running OS: " + os);
		if (os.startsWith("win") || os.startsWith("Win")) {
			isWindows = true;
		} else {
			isWindows = false;
		}
		initDatabase();
	}

	public static AbstractDatabase getDatabase() {
		String path = Config.DATABASE_LOCATION_LIN;
		if (isWindowsOs()) {
			path = Config.DATABASE_LOCATION_WIN;
		}
		AbstractDatabase db = null;
		try {
			db = new SqliteDatabase();
			db.open(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void crateTables(AbstractDatabase db) {
		for (String sql : TableDefines.INITIAL_SQLS) {
			db.executeSql(sql, null);
		}
	}

	public static void initDatabaseData(AbstractDatabase db) {
		// admin
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(AdminColunms.NAME, "admin");
		values.put(AdminColunms.PASSWORD, "admin");
		values.put(AdminColunms.IDENTITY, 88888);
		db.insert(AdminColunms.TABLE_NAME, values);
		// users
		values = new HashMap<String, Object>();
		values.put(UserColunms.IDENTITY, 123);
		values.put(UserColunms.NAME, "name_123");
		values.put(UserColunms.PASSWORD, "123");
		db.insert(UserColunms.TABLE_NAME, values);
		values = new HashMap<String, Object>();
		values.put(UserColunms.IDENTITY, 456);
		values.put(UserColunms.NAME, "name_456");
		values.put(UserColunms.PASSWORD, "456");
		db.insert(UserColunms.TABLE_NAME, values);
		values = new HashMap<String, Object>();
		values.put(UserColunms.IDENTITY, 789);
		values.put(UserColunms.NAME, "name_789");
		values.put(UserColunms.PASSWORD, "789");
		db.insert(UserColunms.TABLE_NAME, values);
		values = new HashMap<String, Object>();
		values.put(UserColunms.IDENTITY, 888);
		values.put(UserColunms.NAME, "abc");
		values.put(UserColunms.PASSWORD, "abc");
		db.insert(UserColunms.TABLE_NAME, values);
		// no message
	}

	private static void initDatabase() {
		String path = Config.DATABASE_LOCATION_LIN;
		if (isWindowsOs()) {
			path = Config.DATABASE_LOCATION_WIN;
		}
		File file = new File(path);
		if (!file.exists()) {
			AbstractDatabase db = getDatabase();
			crateTables(db);
			initDatabaseData(db);
		}
	}

	public static boolean isWindowsOs() {
		return isWindows;
	}
}
