package cn.ljj.server.database;

public class TableDefines {
    public class AdminColunms{
        public static final String TABLE_NAME = "admin";
        
        public static final String IDENTITY = "identity";
        public static final String NAME = "name";
        public static final String PASSWORD = "password";
        public static final String AUTHORITY = "authority";
    }
    
    public class MessageColunms{
        public static final String TABLE_NAME = "message";
        
        public static final String _ID = "_id";
        public static final String FROM_ID = "from_id";
        public static final String FROM_NAME = "from_name";
        public static final String TO_ID = "to_id";
        public static final String TO_NAME = "to_name";
        public static final String DATE = "date";
        public static final String MSG_ID = "msg_id";
        public static final String MSG_TYPE = "msg_type";
        public static final String MSG_BODY = "msg_body";
        public static final String MSG_INDEX = "msg_index";
        public static final String TRANSACTION_ID = "transaction_id";
        
        public static final int INFDEX_ID = 0;
        public static final int INFDEX_FROM_ID = 1;
        public static final int INFDEX_FROM_NAME = 2;
        public static final int INFDEX_TO_ID = 3;
        public static final int INFDEX_TO_NAME = 4;
        public static final int INFDEX_DATE = 5;
        public static final int INFDEX_MSG_ID = 6;
        public static final int INFDEX_MSG_TYPE = 7;
        public static final int INFDEX_MSG_BODY = 8;
        public static final int INFDEX_MSG_INDEX = 9;
        public static final int INFDEX_TRANSACTION_ID = 10;
    }
    
    public class UserColunms{
        public static final String TABLE_NAME = "user";
        
        public static final String IDENTITY = "identity";
        public static final String NAME = "name";
        public static final String PASSWORD = "password";
        public static final String STATUS = "status";
    }
    
    public static final String SQL_CRAETE_ADMIN_TABLE = "create table if not exists "
            + AdminColunms.TABLE_NAME + " ("
            + AdminColunms.IDENTITY +" integer primary key, "
            + AdminColunms.NAME + " varchar not null, "
            + AdminColunms.PASSWORD + " varchar not null, "
            + AdminColunms.AUTHORITY + " integer default -1);";

    public static final String SQL_CRAETE_USER_TABLE = "create table if not exists "
            + UserColunms.TABLE_NAME + " ("
            + UserColunms.IDENTITY +" integer primary key, "
            + UserColunms.NAME + " varchar not null, "
            + UserColunms.PASSWORD + " varchar not null, "
            + UserColunms.STATUS + " integer default 0);";

    public static final String SQL_CRAETE_MESSAGE_TABLE = "create table if not exists "
            + MessageColunms.TABLE_NAME + " ("
            + MessageColunms._ID +" integer primary key autoincrement, "
            + MessageColunms.FROM_ID + " integer, "
            + MessageColunms.FROM_NAME + " varchar, "
            + MessageColunms.TO_ID + " integer not null, "
            + MessageColunms.TO_NAME + " varchar, "
            + MessageColunms.DATE + " varchar, "
            + MessageColunms.MSG_ID + " integer, "
            + MessageColunms.MSG_TYPE + " integer, "
            + MessageColunms.MSG_BODY + " blob, "
            + MessageColunms.MSG_INDEX + " integer, "
            + MessageColunms.TRANSACTION_ID + " integer);";
    
    public static final String[] INITIAL_SQLS = new String[]{
        SQL_CRAETE_ADMIN_TABLE, SQL_CRAETE_USER_TABLE, SQL_CRAETE_MESSAGE_TABLE
    };
}
