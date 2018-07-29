package jdb.processing.integrity.helpers;

import jdb.model.integrity.TableIntegrityModel;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * ����� �������� ��������������� ������ ��� ������ �������� ����������� ��.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.09.2009)
*/

public class IntegrityHelper
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(IntegrityHelper.class.getName());

  /**
   * ����� ���������� ������ �������� ��������� ����, ������� ���� � ������� foreignTable, �� ��� � ������� currentTable.
   * �������� lightCheck ���������, ��������� ��� ��� ����� ���������� ������ ������ ��������� ���������� �������. ����
   * ����� ����� ���������� ���������� �������, �� ��� ���������� �� ���������� ��������� ������ ����������� �� �����
   * (��� �������� ��������� lightCheck=true).
   * @param foreignTable TableIntegrityModel
   * @param currentTable TableIntegrityModel
   * @param lightCheck boolean
   * @return ArrayList[Integer]
  */
  public static ArrayList<Integer> getMissingKeys(TableIntegrityModel foreignTable,
                                                  TableIntegrityModel currentTable, boolean lightCheck)
   {
    ArrayList<Integer> keysList = null;

    // ���� ��� ��������� ������� �� ����� - ��������

    // ���� ���� �� ���� ������� �����

    return keysList;
   }

 }