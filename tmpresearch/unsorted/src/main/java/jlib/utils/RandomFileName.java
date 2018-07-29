package jlib.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Random;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 18.11.2009)
*/

public class RandomFileName
 {
  /** ������ ������� ������. */
  private static Logger logger = Logger.getLogger(RandomFileName.class.getName());

  /**
   * ����� ������� � ���������� ���������� ��� �������� catalogPath ��� ����� � ����������� fileExtension. ����������
   * ����������� ��� �����. �������� usePathCorrection ���������, ������������ �� ��������� (����� fixFPath) ����
   * (��� catalogPath) ��� ���. ���� ������ ����� ��� ��������� ����, �� ���������� �������, ����� ��������� ����
   * ����������� �� ������-����������� ����. ���� � ��������� �������� ����� ������ - ������ ������ ����� ������
   * ��������� �����. ���� ������ ������ ���� � �������� ��� �������� �� ���������� - ����� ������ �������� null.
   * ���� �� ������� ����������, �� ����� ������� ���������� ��� ��� ����� ��� ����������.
   * @param catalogPath String ���� � ��������.
   * @param fileExtension String ���������� ��� ����� (��� �����!).
   * @param usePathCorrection boolean ������������ ��� ��� ��������� ����.
   * @return String ��������� ���������� ��� �����.
  */
  public static String find(String catalogPath, String fileExtension, boolean usePathCorrection)
   {
    String result = null;
    // ���� ��������� ������� ��� ������ ����� ���������� - ���������
    if ((!StringUtils.isBlank(catalogPath)) && (new File(catalogPath).exists()))
     {
      logger.debug("Path [" + catalogPath + "] exists! Processing.");
      // ���� ������������ ��������� ����� ����� - ��������� ��
      String localPath;
      if (usePathCorrection) {localPath = FSUtils.fixFPath(catalogPath, true);}
      else                   {localPath = catalogPath;}
      // ������������ ���������� �����
      String localExt;
      if (!StringUtils.isBlank(fileExtension)) {localExt = "." + fileExtension;}
      else                                     {localExt = "";}

      // ��������� ���������� ����� �����
      Random random = new Random();
      int randomFileName;
      File destFile;
      boolean nameFound = false;
      // � ����� ���������� ��� ����� �� ��� ���, ���� �� ������ ���������� (� ������ �� ��� ��������� ���� ������).
      do
       {
        randomFileName = random.nextInt(Integer.MAX_VALUE); // <- ��������� �������� ���������� �����
        // ���� �� ��������� ������
        destFile = new File(localPath + randomFileName + localExt);
        // ���� ������ ����� �� ���������� - �� ����� ������� ���.
        if (!destFile.exists()) {nameFound = true;}
       }
      while (!nameFound);
      logger.debug("Found random file name [" + randomFileName + "].");
      // ��������� ���������
      result = String.valueOf(randomFileName);
     }
    // ���� �� ������ ������ ������� ��� ������� ������ �� ���������� - ������� �� ���� � ���
    else {logger.warn("Path [" + catalogPath + "] is empty or doesn't exists!");}
    // ���������� ���������
    return result;
   }

 }