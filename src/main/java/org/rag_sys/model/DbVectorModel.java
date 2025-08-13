package org.rag_sys.model;

public class DbVectorModel {

    private final String dbName;
    private final String dbUser;
    private final String dbPassword;
    private final String dbHost;
    private final int dbPort;
    private final String dbTable;

    public DbVectorModel(String dbName, String dbUser, String dbPassword, String dbHost, int dbPort, String dbTable) {
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbTable = dbTable;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbHost() {
        return dbHost;
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbTable() {
        return dbTable;
    }
}
