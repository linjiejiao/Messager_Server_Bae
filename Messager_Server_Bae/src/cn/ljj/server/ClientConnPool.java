
package cn.ljj.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.ljj.message.User;

public class ClientConnPool {
    private Map<Integer, ClientHandleThread> mClients = new HashMap<Integer, ClientHandleThread>();
    Set<IUserStatusChangerListner> mListners = new HashSet<IUserStatusChangerListner>();

    public Set<Integer> keySet() {
        return mClients.keySet();
    }

    public ClientHandleThread get(int id) {
        return mClients.get(id);
    }

    public void clear() {
        mClients.clear();
        notifyUserStatusChanged(User.STATUS_ON_LINE, User.STATUS_OFF_LINE, null);
    }

    public void remove(int identity) {
        ClientHandleThread client = mClients.remove(identity);
        if(client == null){
            return ;
        }
        client.stop();
        User user = client.getUser();
        user.setStatus(User.STATUS_OFF_LINE);
        notifyUserStatusChanged(User.STATUS_ON_LINE, User.STATUS_OFF_LINE, user);
    }

    public void put(int identity, ClientHandleThread clientHandleThread) {
        mClients.put(identity, clientHandleThread);
        notifyUserStatusChanged(User.STATUS_OFF_LINE, User.STATUS_ON_LINE,
                clientHandleThread.getUser());
    }

    public void notifyUserStatusChanged(int oldStatus, int newStataus, User user) {
        for (IUserStatusChangerListner i : mListners) {
            i.onUserStatusChanged(oldStatus, newStataus, user);
        }
    }

    public interface IUserStatusChangerListner {
        public void onUserStatusChanged(int oldStatus, int newStataus, User user);
    }

    public void addUserStatusChangerListner(IUserStatusChangerListner i) {
        if (i == null) {
            return;
        }
        mListners.add(i);
    }

    public void removeUserStatusChangerListner(IUserStatusChangerListner i) {
        if (i == null) {
            return;
        }
        mListners.remove(i);
    }

    public void removeAllUserStatusChangerListner() {
        mListners.clear();
    }
}
