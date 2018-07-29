package spammer.mailsList.interfaces;

import java.util.TreeMap;

/**
 * ���������, ������� ������ ������������� ������ ��������� email-������� ��� �������� �����. ������
 * �� ���������� ������� ���������� �������� ����� ���������������� ������ �������� �������� (�����
 * MailerConfig).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 24.08.2010)
*/

public interface EmailsListInterface
 {
  public TreeMap<String, Integer> getEmailsList();
 }