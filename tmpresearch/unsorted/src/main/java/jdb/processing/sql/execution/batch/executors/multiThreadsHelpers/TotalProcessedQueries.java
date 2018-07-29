package jdb.processing.sql.execution.batch.executors.multiThreadsHelpers;

/**
 * ����� ������������ ��� ������������� � ������ ��������������� ���������� sql-������.
 * ������ ������� ������ - ������� � �������� ������ ���������� ���� ����������� sql-�������� �� ���� �������.
 * @author Gusev Dmitry (�������)
 * @version 1.0 (DATE: 06.05.2010)
*/

public class TotalProcessedQueries
 {
  // ����� ������ ����� ����� ������������ ��������
  private int total = 0;

  // ���������� ������ ����� ������������ ��������
  public void addTotal(int count) {total += count;}

  // ��������� ������ ����� ������������ ��������
  public int  getTotal() {return total;}
 }