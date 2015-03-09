
package cn.ljj.server.database;

import java.util.ArrayList;
import java.util.List;

public class DatabaseObservable {
    private List<IDatabaseObserver> mObsers = new ArrayList<IDatabaseObserver>();

    public void registerObserver(IDatabaseObserver observer) {
        if (observer == null) {
            return;
        }
        mObsers.add(observer);
    }

    public void unregisterObserver(IDatabaseObserver observer) {
        if (observer == null) {
            return;
        }
        mObsers.remove(observer);
    }

    public void notifyDatabaseChanged(String table, int id, Object obj) {
        for (IDatabaseObserver o : mObsers) {
            o.onDatabaseChanged(table, id, obj);
        }
    }

    public interface IDatabaseObserver {
        public static final int OPERATE_INSERT = 0;
        public static final int OPERATE_DELETE = 1;
        public static final int OPERATE_UPDATE = 2;

        public void onDatabaseChanged(String table, int operate, Object obj);
    }

}
