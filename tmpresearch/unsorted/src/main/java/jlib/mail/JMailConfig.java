package jlib.mail;

import jlib.JLibConsts;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 09.12.2010)
*/

public class JMailConfig
 {
  /** ���� "����" - ������ �������/�����. ������������ ����. */
  private String            to        = null;
  /** ���� ��� �������� ����� ����-�������. ������ �����: host[:port] ������������ ����. */
  private String            mailHost  = null;
  /** ���� ��� �������� ����� ����-�������. �������������. */
  private int               mailPort  = 0;
  /** ��������� ��� ���� � ������ ������. �� ��������� - windows-1251 (��. ��������� � ������ JLibConsts). */
  private String            encoding  = JLibConsts.JMAIL_ENCODING;
  /** ���� ��� �������� ���������� ��� ���� FROM ������. ������������ ����. */
  private String            from      = null;
  /** ���� ��� �������� ������ ���������. ������������ ����. */
  private String            text      = null;
  /** ���� ��� �������� ���������� ��� ���� SUBJECT ������. */
  private String            subject   = null;
  /** ������ ������������� � ������ ������ (������ ����� � ������� - ������). */
  private ArrayList<String> filesList = null;

  public String getTo() {
   return to;
  }

  public void setTo(String to) {
   this.to = to;
  }

  public String getMailHost() {
   return mailHost;
  }

  public void setMailHost(String mailHost) {
   this.mailHost = mailHost;
  }

  public int getMailPort() {
   return mailPort;
  }

  public void setMailPort(int mailPort) {
   this.mailPort = mailPort;
  }

  public String getFrom() {
   return from;
  }

  public void setFrom(String from) {
   this.from = from;
  }

  public String getText() {
   return text;
  }

  public void setText(String text) {
   this.text = text;
  }

  public String getSubject() {
   return subject;
  }

  public void setSubject(String subject) {
   this.subject = subject;
  }

  public ArrayList<String> getFilesList() {
   return filesList;
  }

  public void setFilesList(ArrayList<String> filesList) {
   this.filesList = filesList;
  }

  public String getEncoding() {
   return encoding;
  }

  public void setEncoding(String encoding) {
   this.encoding = encoding;
  }

  /** ��������� ������ ��������� ������, ���� ����� ���� �� ����� (��� ������): to, mailHost, from, text. */
  public boolean isEmpty()
   {return (StringUtils.isBlank(to) || StringUtils.isBlank(mailHost) || StringUtils.isBlank(from) || StringUtils.isBlank(text));}

  public void addFile(String file)
   {
    if (!StringUtils.isBlank(file))
     {if (filesList == null) {filesList = new ArrayList<String>();} filesList.add(file);}
   }

 }