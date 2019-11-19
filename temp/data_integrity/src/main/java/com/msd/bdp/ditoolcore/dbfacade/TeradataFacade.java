/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.dbfacade;

import java.sql.Connection;

final class TeradataFacade extends DbFacade {
    TeradataFacade(Connection connection) {
        super(connection, DbType.TERADATA);
    }
}
