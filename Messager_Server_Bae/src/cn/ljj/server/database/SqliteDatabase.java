
package cn.ljj.server.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteDatabase extends AbstractDatabase {

    public SqliteDatabase() throws Exception {
        Class.forName("org.sqlite.JDBC");
    }

    @Override
    public boolean open(String location) {
        try {
            mConnection = DriverManager.getConnection("jdbc:sqlite:" + location);
            mStatement = mConnection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        try {
            if (mStatement != null) {
                mStatement.close();
            }
            if (mConnection != null) {
                mConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
