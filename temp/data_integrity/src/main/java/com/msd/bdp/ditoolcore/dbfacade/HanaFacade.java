/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.dbfacade;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class HanaFacade extends DbFacade {
    HanaFacade(Connection connection) {
        super(connection, DbType.HANA);
    }

    @Override
    public boolean isAlive() {
        if (connection != null) {
            try (final Statement stm = connection.createStatement()) {
                stm.execute("SELECT 1 FROM dummy");
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
}

