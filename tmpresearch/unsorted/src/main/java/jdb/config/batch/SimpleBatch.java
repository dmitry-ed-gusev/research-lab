package jdb.config.batch;

import jdb.filter.sql.SqlFilter;
import jdb.processing.sql.execution.batch.helpers.BatchPreProcessor;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ������� sql-���� (����� ��������� sql-��������). ����� ����� �������� ��������� ������� ��� ������ �� �������
 * ��������. ��� ������ �������������� ��������� ������. ����� ������ ��� ������������� ��� � ������� ������ ������������
 * sql-����� - ��. ����� BatchConfig.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 16.11.2010)
*/

public class SimpleBatch
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(getClass().getName());
  /** ����� sql-�������� ������� �����. */
  private ArrayList<String> batch = null;

  /**
   * �����������. ��������� ���� ������ � ����.
   * @param sql String ������, ����������� � ����.
   * @param useSqlFilter boolean ������������ �� ���������� (�������) ��� ���������� �������.
  */
  public SimpleBatch(String sql, boolean useSqlFilter)
   {this.addSqlToBatch(sql, useSqlFilter);}

  /**
   * �����������. ��������� ����� �������� � ����.
   * @param batch ArrayList[String] �������, ����������� � ����.
   * @param useSqlFilter boolean ������������ �� ���������� (��������) ��� ���������� ��������.
  */
  public SimpleBatch(ArrayList<String> batch, boolean useSqlFilter)
   {this.setBatch(batch, useSqlFilter);}

  public ArrayList<String> getBatch() {
   return batch;
  }

  /**
   * ��������� �������� sql-�����. ��� ��������� ����� ��������� ��� ��������� (�������������) - �������� ������ �
   * select-��������. ���� ����� ��������� ���� �������� ������, ���������� �������� �� ���������� � ���������
   * �����. ������ � ���. ���� ���� ������� ���������� ������� ��� �������� �����-���� ������� (��� ��� ���������������),
   * �� ��� ������� ����� ������� ��������������� ������.
   * @param batch ArrayList[String] ��������������� �������� �����.
   * @param useSqlFilter boolean ������������ �� ���������� (��������).
  */
  public void setBatch(ArrayList<String> batch, boolean useSqlFilter)
   {
    // ����� �� ��������� ������������� sql-�����
    ArrayList<String> tmpBatch = BatchPreProcessor.preProcessSqlBatch(batch, useSqlFilter);
    // ���� ���������� ��������� �� ���� - �������� (���� ��������� ����, �� ������ �� ������)
    if ((tmpBatch != null) && (!tmpBatch.isEmpty())) {this.batch = tmpBatch;}
    // ���� ���� ���� - ������� �� ���� � ���
    else {logger.error("Trying to set empty batch!");}
   }

  /**
   * ����� ��������� ���� sql-������ � ����� ������� ������� (����������� ������ � ����� �����). ���� ���� ��� ��
   * ��������������� - ���������� �������������. ����� ����������� ������ ����������� �� ���������� - �� �� ������ ����
   * ����, �� ������ ���� select-��������. �����, � ����������� �� ��������, ����������� ���������� �������.
   * @param sql String ����������� � ���� ����� ������.
   * @param useSqlFilter boolean ������������ �� ���������� (�������) ��� ���������� �������.
  */
  public void addSqlToBatch(String sql, boolean useSqlFilter)
   {
    // ���� ������ �� ���� - ���������
    if (!StringUtils.isBlank(sql))
     {
      try
       {
        // ���� ������ ��-select - ����������
        if (!DBUtils.isSelectQuery(sql))
         {
          // ���� ����������� ���������� �������� - ���������
          if (useSqlFilter)
           {
            String localSql = SqlFilter.removeDeprecated(sql);
            // ���� ����� ���������� ������ �� ���� - ���������
            if (!StringUtils.isBlank(localSql))
             {
              // ������������� �����
              if (this.batch == null) {batch = new ArrayList<String>();}
              batch.add(localSql);
             }
            // ���� �� ����� ���������� ������ ���� ������ - ������ � ���
            else {logger.error("Sql query become empty after filtering!");}
           }
          // ���� ��� ���������� sql-��������, ������ ��������� ������ � ����
          else
           {
            // ������������� �����
            if (this.batch == null) {batch = new ArrayList<String>();}
            batch.add(sql);
           }
         }
        // ���� �� ������ �������� select-��������, �� ��������� ���!
        else {logger.error("Trying to add SELECT-query to batch!");}
       }
      catch (SQLException e) {logger.error(e.getMessage());}
     }
    // ���� ��������� ������ ���� - �������� � ���
    else {logger.error("Trying to add empty query to batch!");}
   }

  /**
   * ����� ��������� ����� sql-���� � ����� ������� ������� (����� ���� ����������� � ����� ������� �����). ���� ����
   * ������� ������� ��� �� ��������������� - ���������� �������������. ����� ����������� ����� ���������� ��� ���������
   * (�������������) - �������� ������ � select-��������. ���� ����� ��������� ����� ���� �������� ������, ���������� ��
   * ����������, ����������� ������ ������ � ���.
   * @param batch ArrayList[String]
   * @param useSqlFilter boolean ������������ �� ���������� (��������) ��� ���������� ��������.
  */
  public void addBatch(ArrayList<String> batch, boolean useSqlFilter)
   {
    // ����� �� ��������� ������������� sql-�����
    ArrayList<String> tmpBatch = BatchPreProcessor.preProcessSqlBatch(batch, useSqlFilter);
    // ���� ���������� ��������� �� ���� - �������� (���� ��������� ����, �� ������ �� ������)
    if ((tmpBatch != null) && (!tmpBatch.isEmpty())) {this.batch.addAll(tmpBatch);}
    // ���� ���� ���� - ������� �� ���� � ���
    else {logger.error("Trying to add empty batch!");}
   }

  /**
   * ����� ���������� ������ �����.
   * @return int ������ �����. ���� ���� ��� �� ���������������, ����� ������ �������� 0.
  */
  public int getBatchSize()
   {
    int result = 0;
    if ((batch != null) && (!batch.isEmpty())) {result = batch.size();}
    return result;
   }

  /**
   * ����� �������� ������ - ���� �� ������ ��������� ������-�����.
   * @return boolean ������/���� � ����������� �� ����, ���� ��� ��� ������ ��������� �����.
  */
  public boolean isEmpty() {return ((batch == null) || (batch.isEmpty()));}

  /** ��������� ������������� �����. */
  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            append("batch", batch).
            toString();
   }
  
 }