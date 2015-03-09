
package cn.ljj.server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.server.database.TableDefines.*;
import cn.ljj.server.log.Log;

public class DatabasePersister extends DatabaseObservable {
    public static final String TAG = "persistNewMessage";
    private static DatabasePersister sInstance = null;
    AbstractDatabase mDatabase = null;

    private DatabasePersister() {
        mDatabase = DatabaseFactory.getDatabase();
    }

    @Override
    protected void finalize() throws Throwable {
        mDatabase.close();
        super.finalize();
    }

    public static DatabasePersister getInstance() {
        if (sInstance == null) {
            sInstance = new DatabasePersister();
        }
        return sInstance;
    }

    public boolean persistNewMessage(IPMessage msg) {
        boolean ret = false;
        if (!checkMessage(msg)) {
            Log.i(TAG, "persistNewMessage can not persist message like " + msg);
            return ret;
        }
        String sql = ("insert into "
                + MessageColunms.TABLE_NAME + " ("
                + MessageColunms.FROM_ID + ", "
                + MessageColunms.FROM_NAME + ", "
                + MessageColunms.TO_ID + ", "
                + MessageColunms.TO_NAME + ", "
                + MessageColunms.DATE + ", "
                + MessageColunms.MSG_ID + ", "
                + MessageColunms.MSG_TYPE + ", "
                + MessageColunms.MSG_BODY + ", "
                + MessageColunms.MSG_INDEX + ", "
                + MessageColunms.TRANSACTION_ID + " )"
                + "values(?,?,?,?,?,?,?,?,?,?);");
        PreparedStatement prep = null;
        try {
            // insert
            prep = mDatabase.getConnection().prepareStatement(sql);
            prep.setInt(MessageColunms.INFDEX_FROM_ID, msg.getFromId());
            prep.setString(MessageColunms.INFDEX_FROM_NAME, msg.getFromName());
            prep.setInt(MessageColunms.INFDEX_TO_ID, msg.getToId());
            prep.setString(MessageColunms.INFDEX_TO_NAME, msg.getToName());
            prep.setString(MessageColunms.INFDEX_DATE, msg.getDate());
            prep.setInt(MessageColunms.INFDEX_MSG_ID, msg.getMessageId());
            prep.setInt(MessageColunms.INFDEX_MSG_TYPE, msg.getMessageType());
            prep.setBytes(MessageColunms.INFDEX_MSG_BODY, msg.getBody());
            prep.setInt(MessageColunms.INFDEX_MSG_INDEX, msg.getMessageIndex());
            prep.setInt(MessageColunms.INFDEX_TRANSACTION_ID, msg.getTransactionId());
            ret = prep.executeUpdate() > 0;
            // notify
            if (ret) {
                notifyDatabaseChanged(MessageColunms.TABLE_NAME,
                        IDatabaseObserver.OPERATE_INSERT, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    public boolean persistNewUser(User user) {
        boolean ret = false;
        if (!checkUser(user)) {
            return ret;
        }
        // insert
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(UserColunms.IDENTITY, user.getIdentity());
        values.put(UserColunms.NAME, user.getName());
        values.put(UserColunms.PASSWORD, user.getPassword());
        values.put(UserColunms.STATUS, user.getStatus());
        ret = mDatabase.insert(UserColunms.TABLE_NAME, values) > 0;
        // notify
        if (ret) {
            notifyDatabaseChanged(UserColunms.TABLE_NAME,
                    IDatabaseObserver.OPERATE_INSERT, user);
        }
        return ret;
    }

    public boolean updateUser(User user) {
        boolean ret = false;
        if (!checkUser(user)) {
            return ret;
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(UserColunms.IDENTITY, user.getIdentity());
        values.put(UserColunms.NAME, user.getName());
        values.put(UserColunms.PASSWORD, user.getPassword());
        values.put(UserColunms.STATUS, user.getStatus());
        ret = mDatabase.update(UserColunms.TABLE_NAME, values,
                UserColunms.IDENTITY + "=" + user.getIdentity(), null) > 0;
        if (ret) {
            notifyDatabaseChanged(UserColunms.TABLE_NAME,
                    IDatabaseObserver.OPERATE_UPDATE, user);
        }
        return ret;
    }

    public int deleteMessage(IPMessage msg){
        int ret = -1;
        String sql = ("delete from "
                + MessageColunms.TABLE_NAME + " where "
                + MessageColunms.FROM_ID + "=? and "
                + MessageColunms.FROM_NAME + "=? and "
                + MessageColunms.TO_ID + "=? and "
                + MessageColunms.TO_NAME + "=? and "
                + MessageColunms.DATE + "=? and "
                + MessageColunms.MSG_ID + "=? and "
                + MessageColunms.MSG_TYPE + "=? and "
                + MessageColunms.MSG_BODY + "=? and "
                + MessageColunms.MSG_INDEX + "=? and "
                + MessageColunms.TRANSACTION_ID + "=?;");
        PreparedStatement prep = null;
        try {
            prep = mDatabase.getConnection().prepareStatement(sql);
            prep.setInt(MessageColunms.INFDEX_FROM_ID, msg.getFromId());
            prep.setString(MessageColunms.INFDEX_FROM_NAME, msg.getFromName());
            prep.setInt(MessageColunms.INFDEX_TO_ID, msg.getToId());
            prep.setString(MessageColunms.INFDEX_TO_NAME, msg.getToName());
            prep.setString(MessageColunms.INFDEX_DATE, msg.getDate());
            prep.setInt(MessageColunms.INFDEX_MSG_ID, msg.getMessageId());
            prep.setInt(MessageColunms.INFDEX_MSG_TYPE, msg.getMessageType());
            prep.setBytes(MessageColunms.INFDEX_MSG_BODY, msg.getBody());
            prep.setInt(MessageColunms.INFDEX_MSG_INDEX, msg.getMessageIndex());
            prep.setInt(MessageColunms.INFDEX_TRANSACTION_ID, msg.getTransactionId());
            ret = prep.executeUpdate();
            if (ret > 0) {
                notifyDatabaseChanged(MessageColunms.TABLE_NAME,
                        IDatabaseObserver.OPERATE_DELETE, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    public int deleteUser(User user){
        int ret = -1;        String sql = ("delete from "
                + UserColunms.TABLE_NAME + " where "
                + UserColunms.IDENTITY + "=? and "
                + UserColunms.NAME + "=? and "
                + UserColunms.PASSWORD + "=? and "
                + UserColunms.STATUS + "=?;");
        PreparedStatement prep = null;
        try {
            prep = mDatabase.getConnection().prepareStatement(sql);
            prep.setInt(1, user.getIdentity());
            prep.setString(2, user.getName());
            prep.setString(3, user.getPassword());
            prep.setInt(4, user.getStatus());
            ret = prep.executeUpdate();
            if (ret > 0) {
                notifyDatabaseChanged(UserColunms.TABLE_NAME,
                        IDatabaseObserver.OPERATE_DELETE, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    private boolean checkUser(User user) {
        if (user.getIdentity() == 0 || user.getName() == null || user.getName().length() <= 0
                || user.getPassword() == null || user.getPassword().length() <= 0
                || user.getStatus() < 0) {
            return false;
        }
        return true;
    }

    private boolean checkMessage(IPMessage msg) {
        if (msg.getToId() <= 0 || msg.getFromId() <= 0) {
            return false;
        }
        return true;
    }

    public IPMessage getMessage(ResultSet rs) {
        int fromId = 0;
        int toId = 0;
        String fromName = null;
        String toName = null;
        String date = null;
        int messageType = IPMessage.MESSAGE_TYPE_NONE;
        int messageIndex = -1;
        int messageId = -1;
        int transactionId = 0;
        byte[] body = null;
        try {
            if (rs.getRow() == 0 && !rs.next()) {
                return null;
            }
            fromId = rs.getInt(MessageColunms.FROM_ID);
            toId = rs.getInt(MessageColunms.TO_ID);
            fromName = rs.getString(MessageColunms.FROM_NAME);
            toName = rs.getString(MessageColunms.TO_NAME);
            date = rs.getString(MessageColunms.DATE);
            messageType = rs.getInt(MessageColunms.MSG_TYPE);
            messageIndex = rs.getInt(MessageColunms.MSG_INDEX);
            messageId = rs.getInt(MessageColunms.MSG_ID);
            transactionId = rs.getInt(MessageColunms.TRANSACTION_ID);
            body = rs.getBytes(MessageColunms.MSG_BODY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        IPMessage msg = new IPMessage();
        msg.setBody(body);
        msg.setDate(date);
        msg.setFromId(fromId);
        msg.setFromName(fromName);
        msg.setMessageId(messageId);
        msg.setMessageIndex(messageIndex);
        msg.setMessageType(messageType);
        msg.setToId(toId);
        msg.setToName(toName);
        msg.setTransactionId(transactionId);
        return msg;
    }

    public User getUser(ResultSet rs) {
        String name = null;
        String password = null;
        int identity = 0;
        int status = 0;
        try {
            if (rs.getRow() == 0 && !rs.next()) {
                return null;
            }
            name = rs.getString(UserColunms.NAME);
            password = rs.getString(UserColunms.PASSWORD);
            identity = rs.getInt(UserColunms.IDENTITY);
            status = rs.getInt(UserColunms.STATUS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        User user = new User();
        user.setName(name);
        user.setIdentity(identity);
        user.setPassword(password);
        user.setStatus(status);
        return user;
    }
}
