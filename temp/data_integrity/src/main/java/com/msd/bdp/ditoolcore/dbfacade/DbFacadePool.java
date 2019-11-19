/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.dbfacade;

import com.msd.bdp.ditoolcore.DiCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is basic pool connection pool implementation
 */
public final class DbFacadePool implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbFacadePool.class);

    private final String dbUri;
    private final String userName;
    private final String password;
    private final String kerbKeyTab;
    private final String kerbAlias;
    private volatile ConcurrentLinkedQueue<DbFacade> pool;


    public DbFacadePool(String dbUri, String userName, String password, String kerbKeyTab, String kerbAlias) {
        this.dbUri = dbUri;
        this.userName = userName;
        this.password = password;
        this.kerbKeyTab = kerbKeyTab;
        this.kerbAlias = kerbAlias;
        pool = new ConcurrentLinkedQueue<>();
    }

    public DbFacadePool(String dbUri, String userName, String password) {
        this.dbUri = dbUri;
        this.userName = userName;
        this.password = password;
        this.kerbKeyTab = null;
        this.kerbAlias = null;
        pool = new ConcurrentLinkedQueue<>();
    }

    /**
     * Borrows the connection from the pool. If connection doesnt exist, creates it.
     *
     * @return Db facade
     * @throws SQLException
     * @throws IOException
     */
    public DbFacade borrowConnection() throws SQLException, IOException, DiCoreException {
        DbFacade df;
        check();
        if ((df = pool.poll()) == null) {
            df = DbFacade.createFacade(dbUri, userName, password, kerbKeyTab, kerbAlias);
        }
        return df;
    }

    /**
     * Borrows the connection from the pool. Check the connection, as sometimes connection could be closed
     * on the server side. If check fails, then recreated the connection
     *
     * @return Db facade
     * @throws SQLException
     * @throws IOException
     */
    public DbFacade borrowConnectionWithCheck() throws SQLException, IOException, DiCoreException {

        DbFacade df = borrowConnection();
        if (!df.isAlive()) {
            LOGGER.info("{} connection is dead. Recreating", df.dbType);
            df = DbFacade.createFacade(dbUri, userName, password, kerbKeyTab, kerbAlias);
        }
        return df;
    }

    public void returnConnection(DbFacade df) {
        check();
        if (df != null) {
            pool.offer(df);
        }
    }


    /**
     * Destroys the connection. Connection is get to close state
     *
     * @param df database facade
     */
    public void destroyConnection(DbFacade df) {
        check();

        if (df != null && df.isAlive()) {
            df.close();
        }
    }


    private void check() {
        if (pool == null) {
            throw new IllegalStateException("The connection pool is already closed.");
        }
    }

    @Override
    public void close() throws IOException {
        check();

        while (!pool.isEmpty()) {
            pool.remove().close();
        }

        pool = null;
    }


}
