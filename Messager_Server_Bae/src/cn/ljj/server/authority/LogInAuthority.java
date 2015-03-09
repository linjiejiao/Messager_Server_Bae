
package cn.ljj.server.authority;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.ljj.message.User;
import cn.ljj.server.database.AbstractDatabase;
import cn.ljj.server.database.DatabaseFactory;
import cn.ljj.server.database.DatabasePersister;
import cn.ljj.server.database.TableDefines.UserColunms;
import cn.ljj.server.log.Log;

public class LogInAuthority {
    public static final String TAG = "LogInAuthority";
    private static LogInAuthority sInstance;

    private AbstractDatabase mDatabase = null;

    private LogInAuthority() {
        mDatabase = DatabaseFactory.getDatabase();
    }

    public static LogInAuthority getInstance() {
        if (sInstance == null) {
            sInstance = new LogInAuthority();
        }
        return sInstance;
    }

    public boolean authorize(User user) {
        Log.i(TAG, " authorize:" + user);
        boolean ret = false;
        ResultSet rs = null;
        try {
            rs = getUserById(user.getIdentity());
            User dbUser = DatabasePersister.getInstance().getUser(rs);
            if (user.equals(dbUser)) {
                ret = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean isUserExist(int id) {
        boolean ret = false;
        ResultSet rs = null;
        try {
            rs = getUserById(id);
            if (rs.next()) {
                ret = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private ResultSet getUserById(int id) {
        String sql = "select * from " + UserColunms.TABLE_NAME + " where " + UserColunms.IDENTITY
                + "=" + id;
        return mDatabase.rawQuery(sql, null);
    }
   
    public List<User> getAllUsers(boolean noPassword){
        List<User> users = new ArrayList<User>();
        String sql = "select * from " + UserColunms.TABLE_NAME
                + " order by " + UserColunms.STATUS + " desc, " + UserColunms.IDENTITY + ";";
        ResultSet rs = null;
        try {
            rs = mDatabase.rawQuery(sql, null);
            while(rs.next()){
                User user = DatabasePersister.getInstance().getUser(rs);
                if(noPassword){
                    user.setPassword("");
                }
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return users;
    }
}
