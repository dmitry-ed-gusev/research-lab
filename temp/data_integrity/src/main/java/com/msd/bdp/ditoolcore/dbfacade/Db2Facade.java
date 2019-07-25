/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.dbfacade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class Db2Facade extends DbFacade {
    Db2Facade(Connection connection) {
        super(connection, DbType.DB2);
    }

    @Override
    public boolean isAlive() {
        if (connection != null) {
            try (final Statement stm = connection.createStatement()) {
                stm.execute("SELECT 1 FROM SYSIBM.SYSDUMMY1");
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
}

