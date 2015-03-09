
package cn.ljj.server.database;

import java.sql.*;
import java.util.Map;
import java.util.Set;

import cn.ljj.server.log.Log;

public abstract class AbstractDatabase {
    public static final String TAG = "AbstractDatabase";
    protected Connection mConnection = null;
    protected Statement mStatement = null;

    private boolean isDebugSQL() {
        return false;
    }

    public abstract boolean open(String location);

    public abstract void close();

    public Statement getStatement() {
        return mStatement;
    }

    public Connection getConnection() {
        return mConnection;
    }

    public int[] executeBatch(String[] sqls) {
        int[] ret = null;
        try {
            mStatement.clearBatch();
            mStatement.execute("begin;");
            for (String sql : sqls) {
                mStatement.addBatch(sql);
            }
            ret = mStatement.executeBatch();
            mStatement.execute("commit;");
        } catch (SQLException e) {
            try {
                mStatement.execute("roolback;");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                mStatement.clearBatch();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return ret;
    }

    public ResultSet query(String table, String[] columns, String selection,
            Object[] selectionArgs, String groupBy, String having, String orderBy) {
        StringBuilder sql = new StringBuilder("select ");
        if (columns == null || columns.length == 0) {
            sql.append("*");
        } else {
            for (String col : columns) {
                sql.append(col).append(", ");
            }
            // delete the last", "
            sql.delete(sql.length() - 2, sql.length());
        }
        sql.append(" from ").append(table);
        if (selection != null) {
            sql.append(" where ");
            sql.append(fillArgs(selection, selectionArgs));
        }
        if (groupBy != null && !groupBy.isEmpty()) {
            sql.append(" group by ").append(groupBy);
        }
        if (having != null && !having.isEmpty()) {
            sql.append(" having ").append(having);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            sql.append(" order by ").append(orderBy);
        }
        sql.append(";");
        ResultSet rs = null;
        try {
            if (isDebugSQL()) {
                Log.i(TAG, "query: " + sql.toString());
            }
            rs = mStatement.executeQuery(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet rawQuery(String sql, Object[] selectionArgs) {
        ResultSet rs = null;
        try {
            String fullSql = fillArgs(sql, selectionArgs);
            if (isDebugSQL()) {
                Log.i(TAG, "rawQuery: " + fullSql);
            }
            rs = mStatement.executeQuery(fullSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public long insert(String table, Map<String, Object> values) {
        long ret = -1;
        Set<String> keySet = values.keySet();
        if (keySet.size() == 0) {
            return ret;
        }
        StringBuilder keyString = new StringBuilder(" (");
        StringBuilder valueString = new StringBuilder(" (");
        for (String key : keySet) {
            String value = wrapString(values.get(key));
            if (value == null) {
                continue;
            }
            keyString.append(key).append(", ");
            valueString.append(value).append(", ");
        }
        // delete the last", "
        keyString.delete(keyString.length() - 2, keyString.length());
        keyString.append(") ");
        valueString.delete(valueString.length() - 2, valueString.length());
        valueString.append(");");
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(table);
        sql.append(keyString);
        sql.append("values");
        sql.append(valueString);
        try {
            if (isDebugSQL()) {
                Log.i(TAG, "insert: " + sql.toString());
            }
            ret = mStatement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int delete(String table, String whereClause, Object[] whereArgs) {
        int ret = -1;
        StringBuilder sql = new StringBuilder("delete from ");
        sql.append(table);
        if (whereClause != null) {
            sql.append(" where ");
            sql.append(fillArgs(whereClause, whereArgs));
        }
        try {
            if (isDebugSQL()) {
                Log.i(TAG, "delete: " + sql.toString());
            }
            ret = mStatement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int update(String table, Map<String, Object> values, String whereClause,
            Object[] whereArgs) {
        int ret = -1;
        StringBuilder sql = new StringBuilder("update ");
        sql.append(table);
        sql.append(" set ");
        Set<String> keySet = values.keySet();
        for (String key : keySet) {
            sql.append(key);
            sql.append('=');
            sql.append(wrapString(values.get(key)));
            sql.append(", ");
        }
        // delete the last", "
        sql.delete(sql.length() - 2, sql.length());
        if (whereClause != null) {
            sql.append(" where ");
            sql.append(fillArgs(whereClause, whereArgs));
        }
        try {
            if (isDebugSQL()) {
                Log.i(TAG, "update: " + sql.toString());
            }
            ret = mStatement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean executeSql(String sql, Object[] bindArgs) {
        boolean ret = false;
        try {
            String fullSql = fillArgs(sql, bindArgs);
            if (isDebugSQL()) {
                Log.i(TAG, "executeSql: " + fullSql);
            }
            ret = mStatement.execute(fullSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String fillArgs(String selection, Object[] selectionArgs) {
        StringBuilder builder = new StringBuilder();
        if (selectionArgs == null || selectionArgs.length == 0) {
            builder.append(selection);
        } else {
            boolean appendSpace = false;
            if (selection.charAt(selection.length() - 1) == '?') {
                selection += " ";
                appendSpace = true;
            }
            String[] sels = selection.split("\\?");
            if (sels == null || sels.length <= 1) {
                builder.append(selection);
            } else {
                int length = Math.min(sels.length - 1, selectionArgs.length);
                for (int i = 0; i < length; i++) {
                    builder.append(sels[i]).append(wrapString(selectionArgs[i]));
                }
                if (!appendSpace) {
                    builder.append(sels[length]);
                }
            }
        }
        return builder.toString();
    }

    // private String[] wrapStrings(Object[] bindArgs) {
    // if(bindArgs == null){
    // return null;
    // }
    // String[] strings = new String[bindArgs.length];
    // for (int i = 0; i < bindArgs.length; i++) {
    // strings[i] = wrapString(bindArgs[i]);
    // Log.i(TAG, "strings[i]=" + strings[i]);
    // }
    // return strings;
    // }

    private String wrapString(Object bindArg) {
        if (bindArg instanceof CharSequence || bindArg instanceof Character
                || bindArg instanceof Boolean) {
            return "'" + bindArg + "'";
        }
        return bindArg.toString();
    }
}
