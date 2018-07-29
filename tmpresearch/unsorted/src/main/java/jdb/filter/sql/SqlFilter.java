package jdb.filter.sql;

import jdb.DBConsts;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * ������ ����� ��������� ���������� sql-������� �� ��������� ����������� ��������. �� ������� ��������� ���
 * ������������ ������� � ������ \00 - \37 (� ������������ �������) ����� ���������: \11 - ������ ���������,
 * \12 - ����� ������(������� �������, line feed), \15 - ������ ����� ������ (form-feed), \40 - ������ ������� -
 * ������ ������������� ������������ ������. ����� ������� [�, �, '] ����� �������� �� ������ ["].
 *
 * 02.04.08 �.�. ������ ������������ �������� ��������� ���, �� ��� ������ ������� (DEBUG) �� ������� ����� �����
 * ���������� ��������� - ������� ���������� ��������� � ������� ������ ��������� (����������������).
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 16.03.2011)
*/

// todo: ������ \ (�������� ����� ��������) ����� �������� ���� sql-�������. ��� ������� ���� �������/��������???
 
// todo: ������ ������� � �������� - �� ��������. ������: "ddfds gdfsdfg "dwfsdf" dfsdf". �.�. ��� ������� ���
// todo: ������� ������ ������ ������� ������ ���� �������, ��������: "asfgsf gsdf gsdf g 'dfgdfg' sfgsfg". ���?

public class SqlFilter
 {
  /** ���������-������ ������� ������. */
  //private static Logger logger = Logger.getLogger(SqlFilter.class.getName());

  /**
   * ����� �������� �� ����� ������ str, �� ������� ��������� ��� ������� �� ������ ����������� (SQL_DEPRECATED_SYMBOLS).
   * @param sqlStr String ������, �� ������� ��������� ������������� �������.
   * @return String ������ ��� ����������� ��������.
  */
  public static String removeDeprecated(String sqlStr)
   {
    //logger.debug("WORKING SqlFilter.removeDeprecated().");
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      // ������� ��� ����������� ������� �� sql-�������
      for (String aDeprecated : DBConsts.SQL_DEPRECATED_SYMBOLS) {result = result.replaceAll(aDeprecated, "");}
     }
    return result;
   }

  /**
   * ����� �������� �� ����� ������ sqlStr, � ������� ��� ������� �������, ������� ���� � ������ SQL_DEPRECATED_QUOTES,
   * ���������� �� ������ ������� �� ��������� - SQL_DEFAULT_QUOTE. ���� �� ����� ����� �������� ������ ������ ��� null, ��
   * ������������ �������� ����� null.
   * @param sqlStr String ������, � ������� ���������� ������� �� �����������.
   * @return String ������ � ����������� �� ����������� ��������� (��� �������� null).
  */
  public static String changeQuotes(String sqlStr)
   {
    //logger.debug("WORKING SqlFilter.changeQuotes().");
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      // �������� ��� "��������" ������� �� "������"
      for (String aQuote : DBConsts.SQL_DEPRECATED_QUOTES) {result = result.replaceAll(aQuote, DBConsts.SQL_DEFAULT_QUOTE);}
     }
    return result;
   }

  /**
   * ����� ������� �� ��������� ������ ������� - � "�����������" � "�����������". ���� �� ����� ������ ������ ��� null, ��
   * ������������ �������� ����� null.
   * @param sqlStr String ������, � ������� ��������� �������.
   * @return String ������ ��� ������� ��� �������� null.
  */
  public static String removeQuotes(String sqlStr)
   {
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      // ������� ��� "�����������" ������� �� ������
      for (String aDeprecated : DBConsts.SQL_DEPRECATED_QUOTES) {result = result.replaceAll(aDeprecated, "");}
      // ������� ��� "�������" ������� �� ������
      result = result.replaceAll(DBConsts.SQL_DEFAULT_QUOTE, "");
     }
    return result;
   }


  /**
   * ����� �������� ������� ������� (������) (������������ ��� HTML-�����) �� �� ���������� ����. ���� �� ����� ������ ������
   * ��� null, �� ������������ �������� ����� null.
   * @param sqlStr String ������, � ������� ���������� (������).
   * @return String ������ � ����������� �������� ��� �������� null.
  */
  public static String changeChevronsToSymbols(String sqlStr)
   {
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      result = result.replaceAll("<", "&lt;");
      result = result.replaceAll(">", "&gt;");
     }
    return result;
   }

  /**
   * ����� �������� ������� ������� (������) (������������ ��� HTML-�����) �� �� �������� ����. ���� �� ����� ������ ������
   * ��� null, �� ������������ �������� ����� null.
   * @param sqlStr String ������, � ������� ���������� (������).
   * @return String ������ � ����������� �������� ��� �������� null.
  */
  public static String changeChevronsToCode(String sqlStr)
   {
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      result = result.replaceAll("<", "&#060;");
      result = result.replaceAll(">", "&#062;");
     }
    return result;
   }

  /**
   *  ����� ������ ��� ������������.
   * @param args �������� ������.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");
    String sql    = "<script>FFFF</script>";
    logger.debug(SqlFilter.changeChevronsToSymbols(sql));
    logger.debug(SqlFilter.changeChevronsToCode(sql));
   }

 }