package gusev.dmitry.hiberExample2.dbmanagement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 01.12.11)
 */
public class DBPilot
 {
  public static void main(String[] args)
   {
    Log log = LogFactory.getLog(DBPilot.class);
    log.info("DBPilot started.");
    Configuration cfg = new Configuration().configure();
    SchemaExport schemaExport = new SchemaExport(cfg);
    schemaExport.create(false, true);
    log.info("DB schema exported!");
   }
 }
