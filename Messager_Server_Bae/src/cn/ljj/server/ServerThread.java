
package cn.ljj.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ljj.message.IPMessage;
import cn.ljj.message.User;
import cn.ljj.message.composerparser.MessageComposer;
import cn.ljj.server.ClientConnPool.IUserStatusChangerListner;
import cn.ljj.server.database.AbstractDatabase;
import cn.ljj.server.database.DatabaseObservable.IDatabaseObserver;
import cn.ljj.server.database.DatabaseFactory;
import cn.ljj.server.database.DatabasePersister;
import cn.ljj.server.database.TableDefines.MessageColunms;
import cn.ljj.server.database.TableDefines.UserColunms;
import cn.ljj.server.log.Log;

public class ServerThread implements Runnable, IDatabaseObserver, IUserStatusChangerListner {
    public static final String TAG = "ServerThread";

    private ClientConnPool mClients = new ClientConnPool();
    private boolean isRunning = false;
    private ServerSocket mServer;
    private AbstractDatabase mDatabase = null;
    DatabasePersister mPersister = null;

    private void init(){
        mPersister = DatabasePersister.getInstance();
        mPersister.registerObserver(this);
        mClients.addUserStatusChangerListner(this);
        mDatabase = DatabaseFactory.getDatabase();
    }
    
    @Override
    public void run() {
        isRunning = true;
        try {
            mServer = new ServerSocket();
            InetSocketAddress inetAddr = new InetSocketAddress(8888);
            mServer.bind(inetAddr);
            Log.e(TAG, "Server is running, binding on - "
                    + InetAddress.getLocalHost().getHostAddress()
                    + " : " + inetAddr.getPort());
            init();
            while (isRunning) {
                try {
                    Socket s = mServer.accept();
                    ClientHandleThread client = new ClientHandleThread(s, this);
                    new Thread(client).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        isRunning = false;
        synchronized (mClients) {
            Set<Integer> ids = mClients.keySet();
            for (int id : ids) {
                mClients.get(id).stop();
            }
            mClients.clear();
        }
    }

    public ClientConnPool getAllClients() {
        return mClients;
    }

    @Override
    public void onDatabaseChanged(String table, int operate, Object obj) {
        switch (operate) {
            case IDatabaseObserver.OPERATE_DELETE:
                Log.d(TAG, "onDatabaseChanged delete from " + table + "; obj=" + obj);
                break;
            case IDatabaseObserver.OPERATE_INSERT:
                Log.d(TAG, "onDatabaseChanged insert into " + table + "; obj=" + obj);
                if (MessageColunms.TABLE_NAME.equals(table) && obj instanceof IPMessage) {
                    transmitMessage((IPMessage) obj);
                }
                break;
            case IDatabaseObserver.OPERATE_UPDATE:
                Log.d(TAG, "onDatabaseChanged update " + table + "; obj=" + obj);
                break;
        }
    }

    @Override
    public void onUserStatusChanged(int oldStatus, int newStataus, User user) {
        if(oldStatus == User.STATUS_OFF_LINE && newStataus == User.STATUS_ON_LINE){
            Log.d(TAG, "User login : " + user);
            if(user != null){
                sendOfflineMsg(user);
                mPersister.updateUser(user);
            }
        }else if(oldStatus == User.STATUS_ON_LINE && newStataus == User.STATUS_OFF_LINE){
            Log.d(TAG, "User logout : " + user);
            if(user != null){
                mPersister.updateUser(user);
                // notify user off line
            }else{
                markAllUserOffLine();
                // notify all user offline
            }
        }
        
    }

    private void markAllUserOffLine(){
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(UserColunms.STATUS, User.STATUS_OFF_LINE);
        mDatabase.update(UserColunms.TABLE_NAME, values, null, null);
    }

    private void transmitMessage(IPMessage msg) {
        ClientHandleThread target = mClients.get(msg.getToId());
        if (target != null) {
            boolean succ = false;
            try {
                succ = target.writeToTarget(MessageComposer.composeMessage(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (succ) {
                DatabasePersister.getInstance().deleteMessage(msg);
            } else {
                Log.e(TAG, "transmitMessageAndRespon failed! msg=" + msg);
            }
        } else {
            Log.i(TAG, "transmitMessageAndRespon target user offline!");
            // Target is off line, message had been stored in database.
            // And will be sent when target user login.
        }
    }

    private void sendOfflineMsg(User target){
        String sql = "select * from " + MessageColunms.TABLE_NAME + " where "
                + MessageColunms.TO_ID + "=" + target.getIdentity();
        try {
            ResultSet rs = mDatabase.rawQuery(sql, null);
            List<IPMessage> msgs = new ArrayList<IPMessage>();
            while(rs.next()){
                IPMessage msg = mPersister.getMessage(rs);
                msgs.add(msg);
            }
            rs.close();
            for(IPMessage msg : msgs){
                transmitMessage(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
