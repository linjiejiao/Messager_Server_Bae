package cn.ljj.server.config;

import cn.ljj.server.database.DatabaseFactory;

public class Config {
    public static final int DATABASE_TYPE = DatabaseFactory.DATABASE_TYPE_SQLITE;
    
    public static final String DATABASE_LOCATION_LIN = "/home/bae/database.db";
    public static final String DATABASE_LOCATION_WIN = ".\\database.db";
}
