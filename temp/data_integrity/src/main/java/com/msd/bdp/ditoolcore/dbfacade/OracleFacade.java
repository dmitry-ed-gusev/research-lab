/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.dbfacade;

import java.sql.Connection;

final class OracleFacade extends DbFacade {
    OracleFacade(Connection connection) {
        super(connection, DbType.ORACLE);
    }
}
