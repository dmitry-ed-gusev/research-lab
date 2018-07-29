package jdb.processing.sql.execution.batch.helpers;

import jdb.filter.sql.SqlFilter;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ����� �������� �����(�) ���������� (��������������) ������ sql-�������� (sql-batch) � �������� ���������. ����������
 * ����������� � ���������� ������ ��������, ���������� SELECT-�������� (��� �� ������ ����������� � �����) �, ��������,
 * ���������� sql-�������� (�������� "����������� ��������", ������ ������� � �.�.)
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 16.11.2010)
*/

public class BatchPreProcessor
 {
  /** ������ ������� ������. */
  private static Logger logger = Logger.getLogger(BatchPreProcessor.class.getName());

  /**
   * ����� ��������� ������������� ������ sql-�������� (�����) ��� �������� �� ���� ������ �������� (null ��� ������ ������)
   * � select-��������. ���� �������������� ���� ���� ��� null, ����� ���������� �������� null.
   * @param batch ArrayList[String] ���� ��� ���������
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-�������� ����� ����������� � ����.
   * @return ArrayList[String] ������������ ���� ��� null.
  */
  public static ArrayList<String> preProcessSqlBatch(ArrayList<String> batch, boolean useSqlFilter)
   {
    logger.debug("BatchPreProcessor: preProcessSqlBatch().");
    ArrayList<String> result = null;
    // ���� �������� ���� �� ���� - ��������
    if ((batch != null) && (!batch.isEmpty()))
     {
      logger.debug("Batch is not empty. Processing. SQL filter mode [" + useSqlFilter + "]");
      // � ����� �������� �� ��������������� ����� � �������� �� ���� �� ������ �� select-�������
      for (String sql : batch)
       {
        // ����������� try...catch ����� ��� ����, ����� ���� �� �� �������� ���� ����
        try
         {
          // ���� ������ �� ������ �� select-������ - � ��������� ���!
          if (!StringUtils.isBlank(sql) && !DBUtils.isSelectQuery(sql))
           {
            // ���� �������� ���������� sql-��������, �� ��������� ��� ������� ����� ����������� � ����
            if (useSqlFilter)
             {
              String localSql = SqlFilter.removeDeprecated(sql);
              // ���� ����� ���������� ������ �� ���� ������ - ��������� ��� � ����
              if (!StringUtils.isBlank(localSql))
               {
                // ���� ��������� ��� �� ��������������� - �������������
                if (result == null) {result = new ArrayList<String>();}
                result.add(localSql);
               }
              // ������ ����� ���������� ���� ������ - ������ � ���
              else {logger.error("Sql query become empty after filtering!");}
             }
            // ���� ���������� ��������� - ��������� ������ � ���� ��� ����������
            else
             {
              // ���� ��������� ��� �� ��������������� - �������������
              if (result == null) {result = new ArrayList<String>();}
              result.add(sql);
             }
           }
         }
        catch (SQLException e) {logger.error("Error: " + e.getMessage());}
       }
     }
    // ���� ���� ���� - ������ ������� �� ���� � ���
    else {logger.warn("Batch for processing is empty!");}
    return result;
   }

 }