package jlib.utils.common;

import jlib.common.Consts;
import java.io.*;
import java.util.*;

/**
 * ������ ����� ��������� ������ ���������������� ������ �� ������������ ini-����� �� Windows.
 * @author Gusev Dmitry
 * @version 2.0
*/

public class IniFileReader
 {
  /** ��������� ���� */
  final static private int      NAME           = 0;
  /** ��������� ���� */
  final static private int      VALUE          = 1;
  /** ��������� - ������� �������� ������ � ���-����� */
  final static private String   SECTION_PREFIX = "[";
  /** ��������� - �������� �������� ������ � ���-����� */
  final static private String   SECTION_SUFFIX = "]";
  /** ��������� - ������, ���������� ������� ������������ */
  final static private String[] COMMENT_SYMS   = {"//",";","#"};

  /** ���������� ��� �������� ����� ���-�����. */
  private String                ini_file       = null;
  /** ����������-������ ��� �������� ������ ��������-����������� ��� ������������ � ���-�����. */
  private List<String>          comment_syms   = null;
  /** ���������� ��� �������� �������-�������� ��� ������������ ������ � ���-�����. */
  private String                section_prefix = SECTION_PREFIX;
  /** ���������� ��� �������� �������-��������� ��� ������������ ������ � ���-�����. */
  private String                section_suffix = SECTION_SUFFIX;

  /**
   * �����������. ����������� ������������� ������-����� iniFileName.
   * @param iniFileName String ��� ����������������� �����. ���� ����� ���
   * null - ������.
   * @throws Exception �� ���������, ���� ���� iniFileName �� ���������� ���
   * ���������� ����� ��� ����� null.
  */
  public IniFileReader(String iniFileName) throws Exception
   {
    // �������� ������������� ������. �����
    if ((iniFileName != null) && new File(iniFileName).exists()) this.ini_file = iniFileName;
    else throw new Exception("CONFIG FILE <" + iniFileName + "> DOESN'T EXISTS!");
    // ������� ������ ��������-����������� ��� ������������ � ��������� ���
    this.comment_syms = new ArrayList<String>();
    this.comment_syms.addAll(Arrays.asList(IniFileReader.COMMENT_SYMS));
   }

  /**
   *
   * @param prefix String
  */
  public void setSectionPrefix(String prefix)
   {if ((prefix != null) && (!prefix.trim().equals(""))) this.section_prefix = prefix;}

  /**
   *
   * @param suffix String
  */
  public void setSectionSuffix(String suffix)
   {if ((suffix != null) && (!suffix.trim().equals(""))) this.section_suffix = suffix;}

  /**
   *
   * @param symbol String
  */
  public void addCommentSymbol(String symbol)
   {if ((symbol != null) && (!symbol.trim().equals(""))) this.comment_syms.add(symbol);}

  /**
   * ���������� ������ ��������� �������� ��� ����� key �� ������ section.
   * �������� ����� ����������� ��������� patternStr2, ���� �� ��������
   * ���������� �������� patternStr.
   *
   * @param section String
   * @param key String
   * @throws Exception ��
   * @return String[]
  */
  public String[] getArray(String section, String key) throws Exception
   {return this.getString(section, key).split(Consts.VALUES_DELIM);}

  /**
   * ����� ������ ��������� ��������, ��������������� ����� key �� �����������������
   * �����, �������� ������� �� ������ section.
   * @param section String ������������ ������ ������. ����� ��� ������ ���������.
   * ����������� ��� �����-���� �������� ������������� ([,{ � �.�.).
   * @param key String ����, �������� �������� �������� �� �����.
   * @throws Exception �� ��������� ��� ������ �� ������� ���������� ��� � ������� ������
   * @return String ��������� �������� ���������, �����. ����� key.
  */
  public String getString(String section, String key) throws Exception
   {
     String value    = null;
     String   str;
     BufferedReader rd = null;
     if (
         (key != null)     && (!key.trim().equals("")) &&
         (section != null) && (!section.trim().equals(""))
        )
      {
       try
        {
         // ��������� ���-���� ��� ������
         rd = new BufferedReader(new FileReader(this.ini_file));
         // ������ ������ �� �������� ����� ���� �� ��������� ��� �����
         while ((str = rd.readLine()) != null)
          {
           if (!isComment(str)) // ���� ��������� ������ �� ����������� - ��������
            {
             // ��������� ���������� ������ �� ����� (����������� - LIBC.KEY_VALUE_DELIM)
             String[] fields = str.trim().split(Consts.KEY_VALUE_DELIM);
             // ���� � ������ 1 ����� � ��� ������������� �������� ������,
             // �� ������ ������ ������ �� ���������� ������� �����
             // ��� �� ����� ������(��� �����)
             if (
                 (fields.length == 1) &&
                 (fields[0].trim().equals(section_prefix + section + section_suffix))
                )
              {
               // ���������������� ������ ������
               while ((str = rd.readLine()) != null)
                {
                 // ��� ���������� �������� �������� �����, ������� ��������� ������
                 // �� ������ ������� ����� ����� LIBC.KEY_VALUE_DELIM
                 // (� ������ 1=2=3;4=5;6=7 �� ������ ��������: key={1}, value={2=3;4=5;6=7}).
                 // ��� ����� � ������ split ����������� ������ �������� - N. ������ ��������
                 // (���� > 0) ��������� �� ��, ��� ������-����������� (������) ����� ��������
                 // � ������ (N-1) ���. �.�. ��� ����� ���� N=2.
                 fields = str.trim().split(Consts.KEY_VALUE_DELIM, 2);
                 // ���� �� ����� ���� - ����� ��� ��������(���� ��� ����),
                 // � ����� ������� �� ����� ������ ������
                 if ((fields.length > 1) && (fields[NAME].trim().compareTo(key) == 0))
                  {value = fields[VALUE].trim(); break;}
                 // ���� ����� �� ��������� ������ - �� ������� �� ����� ������ ������
                 if ((fields.length == 1) && (fields[NAME].trim().startsWith("["))) {break;}
                }// ����� ����� ������ ������ ���-�����
              }
            }
           // ���� �� ����� ������ �������� - ������� �� ����� ������ �������� ������
           if (value != null) {break;}
          }// ����� ����� ������ �������� �����
        }// ����� ������ try {}
       // ���������� �������������� ��������
       catch (Exception e) {throw new Exception("ERROR WHILE READING INI-FILE (" + e.getMessage() + ")");}
       // ���� ����� ���������� ������� � ����� ������
       finally {if (rd != null) rd.close();}
      }
     return value;
    }

  /**
   * ������ ����� ���������� ������, ���� ������(������) sectionName ����������
   * � ������ ���������������� �����. ���� �������� sectionName ���� ��� ����� null,
   * �� ����� ������ ����.
   * @param sectionName String ������������ ������ ��� ������ � ������-�����
   * @return boolean ������/���� � ����������� �� ���������� ������
   * @throws Exception �� ����� ���������� ��� ������ ������-�����
  */
  public boolean isSectionExists(String sectionName) throws Exception
   {
    String         str;
    BufferedReader rd = null;
    if ((sectionName != null) && (!sectionName.trim().equals("")))
     try
      {
       // ��������� ���-���� ��� ������
       rd = new BufferedReader(new FileReader(this.ini_file));
       // ������ ������ �� �������� ����� ���� �� ��������� ��� �����
       while ((str = rd.readLine()) != null)
        if (!isComment(str)) // ���� ��������� ������ �� ����������� - ��������
         {
          // ��������� ���������� ������ �� ����� (����������� - LIBC.KEY_VALUE_DELIM)
          String[] fields = str.trim().split(Consts.KEY_VALUE_DELIM);
          // ���� � ������ 1 ����� � ��� ������������� �������� ������ -> BINGO!
          if ((fields.length == 1) && (fields[0].trim().equals(section_prefix + sectionName + section_suffix)))
           return true;
         }
      }// ����� ������ try {}
     // ���������� �������������� ��������
     catch (Exception e) {throw new Exception("ERROR WHILE READING INI-FILE (" + e.getMessage() + ")");}
     // ���� ����� ���������� ������� � ����� ������
     finally {if (rd != null) rd.close();}
    return false;
   }

  /**
   * ���������� ������, ���� ������ str �������� ������������ (���������� �
   * ������� �����������). ������� ������������ ��������� � �������
   * comment_syms.
   *
   * @param str String
   * @return boolean
  */
  private boolean isComment(String str)
   {
    boolean result = false;
    // ���� ���������� ��� ������ �� ����� - ��������
    if ((str != null) && (!str.trim().equals("")))
     // �������� � ����� �� ���� ��������� ������
     for (Object commentSymbol : this.comment_syms)
      if (str.trim().startsWith((String) commentSymbol)) result = true;
    return result;
   }

 /**
  * ����� main ������������ ������ ��� ������������ ������� ������.
  * @param args String[]
  */
 public static void main(String[] args)
   {}

 } // END OF CLASS INI_READER
