/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.dbfacade;

import java.sql.Connection;

final class HiveFacade extends DbFacade {
    HiveFacade(Connection connection) {
        super(connection, DbType.HIVE);
    }
}
