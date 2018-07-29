package jlib.utils;

import jlib.JLibConsts;
import jlib.exceptions.EmptyObjectException;
import jlib.logging.InitLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ����� ��������� ��������� ������� ��� ������ � �������� �������� (FSUtils -> File System Utils).
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 25.04.2011)
*/

public class FSUtils
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(FSUtils.class.getName());

  /**
   * ����� ������� ������ ���������, ������� � ���������� ���� dir (���� ������� ����� ����� ������). ���� ������ ��
   * �������, � ���� - �� ������ ����� ������. ���� ������ �������������� ���� - ������ �� ����������. ���� ���������
   * ������� ��� ���� ����� ������ ����������, �� ����� ������ ��������� �� ������ � ������ ������ ����� �������� (�� ��
   * ����� ����������).
   * @param dir String ������� ��� ���� ��� ��������.
  */
  public static void delTree(String dir)
   {
    // ������� ������ "����" ��� ����������� � �������� ��������� ���� � ��������
    File pathName = new File(dir);
    
    // ���� ��������� ������� ���������� - ��������
    if (pathName.exists())
     {
      // ���� ������ "����" - ������� - ����� ������� � ��� ��� ����� � ���������� ��������
      if (pathName.isDirectory())
       {
        // �������� ������ ���� ������ � ������ ��������
        String[] fileNames = pathName.list();
        // ���� ������ ������ ���� - ������� �������
        if ((fileNames == null) || (fileNames.length <= 0))
         {
          logger.debug("DELTREE: deleting dir [" + pathName.getPath() + "].");
          if (!pathName.delete()) logger.error("Can't delete dir [" + pathName.getPath() + "]!");
         }
        else
         {
          // � ����� �������� �� ����� ������ ���������� ������
          for (String fileName : fileNames)
           {
            // ����� ������� ������ "����"
            File file = new File(pathName.getPath(), fileName);
            // ���� ���������� ������ "����" - �������� ���������, ���������� �������� ������ �����
            if (file.isDirectory()) FSUtils.delTree(file.getPath());
            // ���� �� ���������� ������ "����" - ����, �� ������� ���
            else if (file.isFile()) if (!file.delete()) logger.error("Can't delete file [" + file.getPath() + "]!");
           }
          // ������� ������� ������� ����� �������� �� ���� ���� ������
          logger.debug("DELTREE: deleting dir [" + pathName.getPath() + "].");
          if (!pathName.delete()) logger.error("Can't delete dir [" + pathName.getPath() + "]!");
         }
       }
      // ���� �� "����" - ������ ���� - ������� ���
      else if (!pathName.delete()) logger.error("Can't delete file [" + pathName.getPath() + "]!");
     }
    else logger.warn("Specifyed path [" + dir + "] doesn't exists!");
   }

  /**
   * ����� ������� ��������� ������� dir �� ����������� (������������ ����� delTree() ������� ������). ���� ������
   * �������������� ������� - ������ ��������� �� �����. ���� ������ ���� - ������������ ��� ��� - ����� ������
   * ��������� �� �����.
   * @param dir String �������, ��������� �� �����������.
  */
  public static void clearDir(String dir)
   {
    logger.info("Clearing catalog [" + dir + "].");
    // ���� ������� �� ���������� ��� ��� ���� - ������ �� ������
    if ((new File(dir).exists()) && (new File(dir).isDirectory()))
    {
     // �������� ��������� ������ ��������� ������ � ������������
     FSUtils.delTree(dir);
     // ����������� ���������� ������������� ��������
     if (!new File(dir).mkdirs()) logger.error("Can't re-create catalog [" + dir + "]!");
    }
    else logger.warn("Path [" + dir + "] doesn't exists or not a directory!");
   }

  /**
   * ����� �������� � ���������� ��� ���� � ����� ��� ����������� �� ����������� - DEFAULT_DIR_DELIMITER
   * (��. JLibConsts). ���� ���������� ���� ���� (null ��� ������ ������) - ����� ���������� ���� ����� ��������
   * (null ��� ������ ������). ���� ���� ���������� � �������-����������� - ���� ������ �� ��������, �� �����
   * �������������� � ���������� (���� ��� ����������).
   * @param fPath String ���� � �����, ���������� ��� ��������� ������������.
   * @param appendSlash boolean ��������� ��� ��� � ����� ������������������� ���� ������ "����". 
   * @return ������������������ ���� � ����� ��� �������� null.
  */
  public static String fixFPath(String fPath, boolean appendSlash) 
   {
    String result = fPath;
    // ���� ���� �� ���� - ��������
    if (!StringUtils.isBlank(fPath))
     {
      // � ����� �������� �� ����� ������� ����������� �������� � �������� �� �� ����������� �����������
      for (char aDeprecated : JLibConsts.DEPRECATED_DELIMITERS)
       {result = result.replace(aDeprecated, JLibConsts.DEFAULT_DIR_DELIMITER);}
      // ���� �� ��� �� ����� ������� ���� ������-�����������
      boolean isEndsWithDelimiter = (result.endsWith(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER)));

      // ������ �������� ���������� ���� �� �����, ����������� - �����������. ��� ���������� ��� �����������
      // ����������� ���� / // /// //// � �.�.
      String[] splittedResult = result.split(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER));
      StringBuilder resultPath = new StringBuilder();
      // ���� �������� ���� ��������� � �������-�����������, �� ���� ������ �� ������ ��������
      if (result.startsWith(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER)))
       {resultPath.append(JLibConsts.DEFAULT_DIR_DELIMITER);}
      // ���� ���� ��� ������������ �� ������-�����������, �� ������� ���������� ������������ ���������� �� 1
      int delimiterBoundary;
      if (isEndsWithDelimiter) {delimiterBoundary = splittedResult.length;}
      else                     {delimiterBoundary = splittedResult.length - 1;}
      // � ����� �������� ������� ����������� ����
      for (int i = 0; i < splittedResult.length; i++)
       {
        // ���� ������� ������� �� ������ - ��������� ��� � ��������������� ����
        if (!StringUtils.isBlank(splittedResult[i]))
         {
          // ��������� �������
          resultPath.append(splittedResult[i]);
          // ��������� �����������
          if (i < delimiterBoundary) {resultPath.append(JLibConsts.DEFAULT_DIR_DELIMITER);}
         }
       }
      result = resultPath.toString();
      // ���� ������� ����� - ��������� ���� - ��������� ��� (���� ���� ��� �� �������� "/" � �����). �����
      // ���������� ��������� - �� ���� �� ���? ���� ��� ���� � ����� - ���� ��������� �����!!!
      if (appendSlash && (!result.endsWith(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER))) &&
                         (!new File(result).isFile()))
       {result += String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER);}
     }
    //logger.debug("WORKING FSUtils.fixFPath(). RESULT: " + result);
    return result;
   }

  /**
   * ����� �������� � ���������� ��� ���� � ����� ��� ����������� �� ����������� - DEFAULT_DIR_DELIMITER
   * (��. JLibConsts). ���� ���������� ���� ���� - ����� ���������� �������� null. ������ ����� ��� ������
   * ���������� ������ ����� - FSUtils.fixFPath(String, boolean).
   * @param fPath String ���� � �����, ���������� ��� ��������� ������������.
   * @return ������������������ ���� � ����� ��� �������� null.
  */
  public static String fixFPath(String fPath) {return FSUtils.fixFPath(fPath, false);}
  
  /**
   * ����� ����������� ��������� ������ object � ���� � ������ fileName � �����������
   * JLibConsts.SERIALIZED_OBJECT_EXTENSION. ���������� ���� ����� �������� � ������� fullPath, ���� ������
   * �������� ��� - �� ����� ������. ����� ���� ����� ������������� (� ���� �� ��������) � ������� ���
   * fileName � ���������� JLibConsts.ZIPPED_OBJECT_EXTENSION. ���� � ��������������� �������� �����
   * ������. ��������� ����� ������ ���� � ����������� ��������� ����� (���� + ��� �����).<br>
   * <b>�����!</b> ������������� ������ ����������� ������ ������������� ��������� Serializable!
   * @param object Object ������������� ������.
   * @param fullPath String ���� � �������� ��� ������������ �������. ���� ���� ���� - ������ ����� ������������ �
   * ������� �������.
   * @param fileName String ��� ����� ��� ���������������� ������� � ��� ������ (����������� ��� ����������!).
   * @param fileExt String ���������� ��������� ����� � ��������. ���� �� ������� - ������������ ����������
   * �� ��������� - JLibConsts.ZIPPED_OBJECT_EXTENSION. ���������� ����������� ��� �����!
   * @param useFilePathCorrection boolean ������������ ��� ��� ������� ��������� ��������� ���� ��� ����������
   * ������� ���� � �������������� �����. ���� ������� ������������, �� �� �������� UNC-���� (\\server\folder).
   * @return String ������ ���� � ���������������� � ����������������� �������.
   * @throws jlib.exceptions.EmptyObjectException ������ - ���������� ��� ������������ ������ ����.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static String serializeObject(Object object, String fullPath, String fileName,
   String fileExt, boolean useFilePathCorrection) throws EmptyObjectException, IOException
   {
    logger.debug("WORKING FSUtils.serializeObject().");
    // �������� ����������� ������� - �� ���� �� ��
    if (object == null) {throw new EmptyObjectException("Current object is NULL!");}

    // �������� ����������� ����� ����� ��� ���������� �������. ���� ��� ����� �� �������, ������ ���� �����
    // ������������ ��� ������(�������).
    String localFileName;
    if (StringUtils.isBlank(fileName)) {localFileName = object.getClass().getSimpleName();} else {localFileName = fileName;}

    // �������� ������� ��� ������������ - ���� ���� ���������, ����������� � �������!
    String localFullPath;
    if (StringUtils.isBlank(fullPath)) {localFullPath = FSUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath());}
    else
     {
      // ���� ���������� ��������� ��������� ���� - ��������� ��
      if (useFilePathCorrection) {localFullPath = FSUtils.fixFPath(fullPath);}
      // ���� ��������� �� ������������ - ����� ���� �� ���������� ��� ���������
      else                       {localFullPath = fullPath;}
     }
    
    // ���� ��������� ������� ��� ������������ �� ���������� - ������� ���
    if (!new File(localFullPath).exists())
     {
      logger.debug("Creating catalog [" + localFullPath + "].");
      boolean result = new File(localFullPath).mkdirs();
      // ���� �� ������� ������� ������� ��� ������������ - ������!
      if (!result) {throw new IOException("Can't create catalog [" + localFullPath + "] for object!");}
     }
    // ���� �� ������� ����������, �� ��� �� ������� (���� ��������) - ������ (��)!
    else if (!new File(localFullPath).isDirectory())
     {throw new IOException("Path [" + localFullPath + "] is not directory!");}

    // ������ ���� � ����� � ��������������� ��������
    StringBuilder fullPathSerialized = new StringBuilder(localFullPath);
    // ���� ���� �� ������������ �� ������ �� ��������� (/) - ������� � ����� ���� ���� ������
    if (!fullPathSerialized.toString().endsWith(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER)))
     fullPathSerialized.append(JLibConsts.DEFAULT_DIR_DELIMITER);

    // ���������� ��� ����� � ��������������� �������� �� ������ ��������� � ��������� ��� �����������
    // ��� ��������� �����, ������� ���� ���������� ��� �������, �� ���� ��������� - �� ��������� �� ���
    // � ����������� �� ��������� � ������� ���� �� ���� ����������, ������� �� ���������.
    String serializedExt = ".";
    if ((fileExt != null) && (!fileExt.trim().equals("")))
     {
      if (JLibConsts.SERIALIZED_OBJECT_EXTENSION_1.equals(fileExt))
       {serializedExt += JLibConsts.SERIALIZED_OBJECT_EXTENSION_2;}
      else
       {serializedExt += JLibConsts.SERIALIZED_OBJECT_EXTENSION_1;}
     }
    // ���� ���������� �� ������� - ���������� ������ ���������� �� ���������.
    else {serializedExt += JLibConsts.SERIALIZED_OBJECT_EXTENSION_1;}
    // ��������������� ��������� ���������� � ������� ���� � �����
    fullPathSerialized.append(localFileName).append(serializedExt);

    // ������ ���� � ����� � ������� ���������������� �������.
    StringBuilder fullPathZipped = new StringBuilder(localFullPath);
    if (!fullPathZipped.toString().endsWith("/")) {fullPathZipped.append("/");}
    fullPathZipped.append(localFileName);
    
    // ���� ��� ������� ���������� - ���������� ���, ���� �� ��� - ���������� ���������� �� ���������.
    if (!StringUtils.isBlank(fileExt))
     {
      // ���� ��������� ���������� ���������� � ����� - ��������� ��� ��� ����, ���� �� �� � ����� -
      // ������� ������� ����� ����� ����������� �����
      if (fileExt.startsWith(".")) {fullPathZipped.append(fileExt);}
      else                         {fullPathZipped.append(".").append(fileExt);}
     }
    else {fullPathZipped.append(JLibConsts.ZIPPED_OBJECT_EXTENSION);}

    // ���������� ����� ����������� ����������� �������� ������ ObjectOutputStream ��� ������������� ��
    // �� ����� ������ ������� � ���� (������������)
    ObjectOutputStream out = null;
    try
     {
      // ������ ������� � ���� (������������)
      logger.debug("Writing object to disk.");
      out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fullPathSerialized.toString())));
      out.writeObject(object);
     }
    // �� - ���-�� �� ��� � ������������� �������
    catch (InvalidClassException e)
     {logger.error("Something is wrong with a class " + object.getClass().getName() + " [" + e.getMessage() + "]");}
    // �� - ������������� ����� �� ��������� ��������� Serializable
    catch (NotSerializableException e)
     {logger.error("Class " + object.getClass().getName() + " doesn't implement the java.io.Serializable " +
                   "interface! [" + e.getMessage() + "]");}
    catch (IOException e) {logger.error("I/O error! [" + e.getMessage() + "]");}
    finally {logger.debug("Trying to close ObjectOutputStream..."); if (out != null) out.close();}

    // ���������� ����� ����������� ����������� �������� ������� ��� ������������� �� �� ����� ������ � ����
    FileOutputStream fout = null;
    ZipOutputStream  zout = null;
    FileInputStream  fin  = null;
    try
     {
      // ��������� ���������� �����
      fout = new FileOutputStream(fullPathZipped.toString());
      zout = new ZipOutputStream(new BufferedOutputStream(fout));
      // ������� ���������� ������ � ������
      zout.setLevel(Deflater.BEST_COMPRESSION);
      // ������ � ����� ���� �� ������������ �����
      ZipEntry ze = new ZipEntry(localFileName + serializedExt);
      zout.putNextEntry(ze);
      // ��������������� ������ ������������� ����� � �����
      fin = new FileInputStream(fullPathSerialized.toString());
      byte ipBuf[] = new byte[JLibConsts.FILE_BUFFER];
      int lenRead;
      while ((lenRead = fin.read(ipBuf)) > 0) {zout.write(ipBuf, 0, lenRead);}
      zout.closeEntry();
     }
    finally
     {
      logger.debug("Trying to close zip and file streams...");
      if (fin != null) fin.close(); if (zout != null) zout.close(); if (fout != null) fout.close();
     }

    // �������� ��������� ����� � �������
    if (!new File(fullPathSerialized.toString()).delete()) logger.warn("Can't delete source file [" + fullPathSerialized + "]!");
    
    return fullPathZipped.toString();
   }

  /**
   * ����� ����������� ��������� ������ object � ���� � ������ fileName � �����������
   * JLibConsts.SERIALIZED_OBJECT_EXTENSION. ���������� ���� ����� �������� � ������� fullPath, ���� ������
   * �������� ��� - �� ����� ������. ����� ���� ����� ������������� (� ���� �� ��������) � ������� ���
   * fileName � ���������� JLibConsts.ZIPPED_OBJECT_EXTENSION. ���� � ��������������� �������� �����
   * ������. ��������� ����� ������ ���� � ����������� ��������� ����� (���� + ��� �����).<br>
   * <b>�����!</b> ������������� ������ ����������� ������ ������������� ��������� Serializable!
   * @param object Object ������������� ������.
   * @param fullPath String ���� � �������� ��� ������������ �������. ���� ���� ���� - ������ ����� ������������ �
   * ������� �������.
   * @param fileName String ��� ����� ��� ���������������� ������� � ��� ������ (����������� ��� ����������!).
   * @param fileExt String ���������� ��������� ����� � ��������. ���� �� ������� - ������������ ����������
   * �� ��������� - JLibConsts.ZIPPED_OBJECT_EXTENSION. ���������� ����������� ��� �����!
   * @return String ������ ���� � ���������������� � ����������������� �������.
   * @throws jlib.exceptions.EmptyObjectException ������ - ���������� ��� ������������ ������ ����.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static String serializeObject(Object object, String fullPath, String fileName, String fileExt)
   throws EmptyObjectException, IOException {return FSUtils.serializeObject(object, fullPath, fileName, fileExt, true);}

  /**
   * ����� ����������� ��������� ������ object � ���� � ������ fileName � �����������
   * JLibConsts.SERIALIZED_OBJECT_EXTENSION. ���������� ���� ����� �������� � ������� fullPath, ���� ������
   * �������� ��� - �� ����� ������. ����� ���� ����� ������������� (� ���� �� ��������) � ������� ���
   * fileName � ���������� JLibConsts.ZIPPED_OBJECT_EXTENSION. ���� � ��������������� �������� �����
   * ������. ��������� ����� ������ ���� � ����������� ��������� ����� (���� + ��� �����).<br>
   * <b>�����!</b> ������������� ������ ����������� ������ ������������� ��������� Serializable!
   * @param object Object ������������� ������.
   * @param fullPath String ���� � �������� ��� ������������ �������. ���� ���� ���� - ������ ����� ������������ �
   * ������� �������.
   * @param fileName String ��� ����� ��� ���������������� ������� � ��� ������ (����������� ��� ����������!).
   * @return String ������ ���� � ���������������� � ����������������� �������.
   * @throws jlib.exceptions.EmptyObjectException ������ - ���������� ��� ������������ ������ ����.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
  */
  public static String serializeObject(Object object, String fullPath, String fileName)
   throws EmptyObjectException, IOException {return FSUtils.serializeObject(object, fullPath, fileName, null);}

  /**
   * ����� ����������� ��������� ������ object � ���� � ������ fileName � �����������
   * JLibConsts.SERIALIZED_OBJECT_EXTENSION. ���������� ���� ����� �������� � ������� fullPath, ���� ������
   * �������� ��� - �� ����� ������. ����� ���� ����� ������������� (� ���� �� ��������) � ������� ���
   * fileName � ���������� JLibConsts.ZIPPED_OBJECT_EXTENSION. ���� � ��������������� �������� �����
   * ������. ��������� ����� ������ ���� � ����������� ��������� ����� (���� + ��� �����).<br>
   * <b>�����!</b> ������������� ������ ����������� ������ ������������� ��������� Serializable!
   * @param object Object ������������� ������.
   * @param fullPath String ���� � �������� ��� ������������ �������. ���� ���� ���� - ������ ����� ������������ �
   * ������� �������.
   * @return String ������ ���� � ���������������� � ����������������� �������.
   * @throws jlib.exceptions.EmptyObjectException ������ - ���������� ��� ������������ ������ ����.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
  */
  public static String serializeObject(Object object, String fullPath) throws EmptyObjectException, IOException
   {return FSUtils.serializeObject(object, fullPath, null);}

  /**
   * ����� ����������� ��������� ������ object � ���� � ������ fileName � ����������� JLibConsts.SERIALIZED_OBJECT_EXTENSION.
   * ���������� ���� ����� �������� � ������� �������. ����� ���� ����� ������������� (� ���� �� ��������) � ������� ���
   * fileName � ���������� JLibConsts.ZIPPED_OBJECT_EXTENSION. ���� � ��������������� �������� ����� ������. ���������
   * ����� ������ ���� � ����������� ��������� ����� (���� + ��� �����).<br>
   * <b>�����!</b> ������������� ������ ����������� ������ ������������� ��������� Serializable!
   * @param object Object ������������� ������.
   * @return String ������ ���� � ���������������� � ����������������� �������.
   * @throws jlib.exceptions.EmptyObjectException ������ - ���������� ��� ������������ ������ ����.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
  */
  public static String serializeObject(Object object) throws EmptyObjectException, IOException
   {return FSUtils.serializeObject(object, null);}

  /**
   * ����� ������������� � ������������� ������, ������� ��� ������������ � ��������� ������� serializeObject
   * ������� ������. ������ ������ - ZIP - [���� ����� - ���� ���� � ��������], ���� ������ � ������ ������ ������, ��
   * ���������� ����� ������ ������. �������� fullFilePath �������� ������ ���� � ����� ������.
   * @param filePath String ������ ���� � ����� ������.
   * @param deleteSource boolean ������� ��� ��� �������� ���� ����� ������� �������������� �������.
   * @param useFilePathCorrection boolean ������������ ��� ��� ������� ��������� ��������� ���� ��� ����������
   * ������� ���� � �������������� �����. ���� ������� ������������, �� �� �������� UNC-���� (\\server\folder).
   * @return Object ������������� � ����������������� ������.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
   * @throws ClassNotFoundException ������ ��� �������������� ������� �� �����.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static Object deserializeObject(String filePath, boolean deleteSource, boolean useFilePathCorrection)
   throws IOException, ClassNotFoundException
   {
    logger.debug("WORKING FSUtils.deserializeObject().");

    // ����������������� � ������������� ������
    Object object = null;

    // �������� ����������� ���� � ����� � �������� ������������� �����
    if (StringUtils.isBlank(filePath)) {throw new IOException("Received path is empty!");}
    // �������� �������������
    else if (!new File(filePath).exists()) {throw new IOException("File [" + filePath + "] doesn't exists!");}
    // �������� ����, ��� ���� ��������� ������ �� ����
    else if (!new File(filePath).isFile()) {throw new IOException("Path [" + filePath + "] not a file!");}

    // ��������� ���������� � ������ ����� � ����� (��� ���� ��������� ��� �������-����������� � ��������� ����).
    // ������������� ���� ������������ ������ � ����������� �� �������� ��������� useFilePathCorrection.
    String localFilePath;
    if (useFilePathCorrection) {localFilePath = FSUtils.fixFPath(filePath, true);}
    else                       {localFilePath = filePath;}

    // �������� ���� � �������� ��������, � ������� ����� ������������� ������ �� �����. ������� ������� - �������.
    String tempFilePath = FSUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath(), true);

    // ���������� ����� ����������� ����������� �������� ������� ��� ������������� �� �� ����� ������ � ����
    ZipInputStream zin = null;
    String unpackedFileName = null; // <- ��� �������������� �����
    try
     {
      // ���������� ������� ����� �� ������ (������ ��������� ���� ������)
      zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(localFilePath)));
      ZipEntry entry;
      int counter = 0;
      while(((entry = zin.getNextEntry()) != null) && (counter < 1))
       {
        logger.debug("Extracting from archive -> " + entry.getName());
        unpackedFileName = entry.getName();
        int count;
        byte data[] = new byte[JLibConsts.FILE_BUFFER];
        // ���������� ����� ����������� ����������� �������� ������� ��� ������������� �� �� ����� ������ � ����
        BufferedOutputStream dest = null;
        try
         {
          // ����� �� ���� ������������� ����. ����� � ��� �� �������, ��� ��������� �������� �����
          if (!StringUtils.isBlank(unpackedFileName)) // <- ������ �� NullPointerException
           {
            dest = new BufferedOutputStream(new FileOutputStream(tempFilePath + unpackedFileName), JLibConsts.FILE_BUFFER);
            while ((count = zin.read(data, 0, JLibConsts.FILE_BUFFER)) != -1) {dest.write(data, 0, count);}
            dest.flush();
           }
          // ���� ��� �������������� ����� �������� ����� - �������������� ������ (���������)! 
          else {throw new IOException("Unpacked file name is blank!");}
         }
        // �������� ���������� �������
        finally {if (dest != null) dest.close();}
        zin.closeEntry();
        counter++;
       }
     }
    // �������� ���������� �������
    finally {if (zin != null) zin.close();}

    // ���������� ����� ����������� ����������� �������� ������� ��� ������������� �� �� ����� ������ � ����
    ObjectInputStream in = null;
    try
     {
      // �������������� �� ����� �������������� �������
      if (!StringUtils.isBlank(unpackedFileName)) // <- ������ �� NullPointerException
       {
        in     = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tempFilePath + unpackedFileName)));
        object = in.readObject();
       }
      // ���� ��� �������������� ����� �������� ����� - �������������� ������ (���������)! 
      else {throw new IOException("Unpacked file name is blank!");}
     }
    // �������� ���������� �������. � � ����� ������ ������� ������������� ��������� ����.
    finally
     {
      if (in != null) in.close();
      // �������� �������������� ����� (����������), ���� ��� ��� �� �����.
      if (!StringUtils.isBlank(unpackedFileName)) // <- ������ �� NullPointerException
       {
        if (!new File(tempFilePath + unpackedFileName).delete())
         {logger.warn("Can't delete file [" + (tempFilePath + unpackedFileName) + "]!");}
        else
         {logger.debug("Deleted unpacked file [" + (tempFilePath + unpackedFileName) + "].");}
       }
     }
    
    // ���� ������� �������� ��������� ��������� ����� � ��������������� �������� - �������
    if (deleteSource)
     {
      logger.debug("Trying to delete source file [" + filePath + "].");
      if (!new File(filePath).delete()) {logger.warn("Can't delete source file [" + filePath + "]!");}
      else {logger.debug("Source file [" + filePath + "] deleted successfully.");}
     }
    else
     {logger.debug("No deleting source file [" + filePath + "].");}
    // ���������� ���������
    return object;
   }

  /**
   * ����� ������������� � ������������� ������, ������� ��� ������������ � ��������� ������� serializeObject
   * ������� ������. ������ ������ - ZIP - [���� ����� - ���� ���� � ��������], ���� ������ � ������ ������ ������, ��
   * ���������� ����� ������ ������. �������� fullFilePath �������� ������ ���� � ����� ������.
   * @param filePath String ������ ���� � ����� ������.
   * @param deleteSource boolean ������� ��� ��� �������� ���� ����� ������� �������������� �������.
   * @return Object ������������� � ����������������� ������.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
   * @throws ClassNotFoundException ������ ��� �������������� ������� �� �����.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static Object deserializeObject(String filePath, boolean deleteSource)
   throws IOException, ClassNotFoundException {return FSUtils.deserializeObject(filePath, deleteSource, true);}

  /**
   * ����� ���������� �������� ������/���� � ����������� �� ����, �������� �� ��������� ������� ����� (���� ��� ������
   * �������). ����������� � ������ �������� ����������� �� �����������, ����� ��������� ������� ������ ������. ����
   * ��������� �������� ������ ����� - ����� ���������� �������� ����. ���� ���� ��������� �� ���� (� �� �� �������),
   * �� ����� ���������� �������� ����.������ ������, ����� ��������� ������� ������ � ����������� � ������ ��������,
   * � �� ������, ���������� ������ ����� (��� � ���� ��������).
   * @param path String ���������� ���� � ��������, ������� ������ � ������� ���������.
   * @return boolean ������/���� - � ����������� �� ������� ������ � ��������� ��������.
  */
  public static boolean containFiles(String path)
   {
    logger.debug("FSUtils.containFiles(). Checking path [" + path + "].");
    boolean result = false;
    // ���� ��������� ���� �� ���� - ��������
    if (!StringUtils.isBlank(path))
     {
      // ��������� ������������� �������� � ��, ��� ��� ������ �������
      File dir = new File(FSUtils.fixFPath(path));
      if (dir.exists() && dir.isDirectory())
       {
        // �������� ������ ����������� �������� � ������� - ���� �� ��� �����
        File[] fileList = dir.listFiles();
        int counter = 0;
        while ((counter < fileList.length) && !result)
         {
          if (fileList[counter].isFile()) {result = true;}
          counter++;
         }
       }
      // ������ - � ���!
      else {logger.error("Path [" + path + "] doesn't exists or not a directory!");}
     }
    // ���� �� ���� ���� - ������� �� ���� � ���
    else {logger.error("Empty path!");}
    return result;
   }

  /**
   * ����� ���������� �������� ������/���� � ����������� �� ����, ���� �� ��������� ������� (�������� �� ��������� �������
   * ����� ��� ������ �������� (���� ������ ������ �������). ������ - ������� ����, ���� - � �������� ���� �����������/�����.
   * ���� ��������� �������� ����� - ����� ���������� �������� ������. ���� ���� ��������� �� ���� (� �� �� �������), �� �����
   * ���������� �������� ������.
   * @param path String ���������� ���� � ��������, ������� �������� ���������.
   * @return boolean ������/���� - � ����������� �� ������� ������/������������ � ��������� ��������.
  */
  public static boolean isEmptyDir(String path)
   {
    logger.debug("FSUtils.isEmptyDir(). Checking path [" + path + "].");
    boolean result = true;
    // ���� ��������� ���� �� ���� - ��������
    if (!StringUtils.isBlank(path))
     {
      // ��������� ������������� �������� � ��, ��� ��� ������ �������
      File dir = new File(FSUtils.fixFPath(path));
      if (dir.exists() && dir.isDirectory())
       {
        // �������� ������ ����������� �������� � ������� - ���� �� ��� �����/�����������. ���� �� ��������
        // ������ = NULL, ��� �������� ������ � ����� ������ ����.
        String[] filesList = dir.list();
        if ((filesList != null) && (filesList.length > 0)) {result = false;}
       }
      // ������ - � ���!
      else {logger.error("Path [" + path + "] doesn't exists or not a directory!");}
     }
    // ���� �� ���� ���� - ������� �� ���� � ���
    else {logger.error("Empty path!");}
    return result;
   }

  /**
   * ����� ��������� ������� (��� ��� ������ �������) catalogPath � ���� ���� (�������� clearPath) ������� ���. ����
   * �������� �� ���������� - �� ����� ������. ���� ������� ������� (���� �� �� �����������) �� ������� - ��������� ��.
   * ���� ���� ��������� �� �� ������� - ��������� ��. ������ ����� ����� ������������ � ���-����������� ��� �������������
   * ��� "��������"/�������/�������� ����������� ���������.
   * @param catalogPath String ���� � "������������" ��������.
   * @param clearPath boolean ������� ��� ��� ��������� �������.
   * @throws IOException �� ��������� ���� ���� ��������� �� �� �������, ���� �� ������� ������� �������, ����
   * ��������� ���� ����.
  */
  public static void processPath(String catalogPath, boolean clearPath) throws IOException
   {
    // ���� ���� �� ���� - ������������ ���
    if (!StringUtils.isBlank(catalogPath))
     {
      File catalog = new File(catalogPath);
      // ���� ���� ���������� - ��������� ��� � ������� (�����������)
      if (catalog.exists())
       {
        // ���� ��� �� ������� - ��
        if (!catalog.isDirectory()) {throw new IOException("Path [" + catalogPath + "] is not a directory!");}
        // ���� ������� - ������� (� ����������� �� ���������)
        else if (clearPath) {FSUtils.clearDir(catalogPath);}
       }
      // ���� ���� �� ���������� - �������. ��� ������� �������� - ��
      else
       {if (!catalog.mkdirs()) {throw new IOException("Can't create catalog [" + catalogPath + "]!");}}
     }
    // ���� ���� ���� - ��
    else {throw new IOException("Path is empty!");}
   }

  /**
   * ����� ������������ ��������� ���� (fileName) ��� ��������� � ������� �� ���� ������ ������ (���� �������� ���������
   * � ���������������� ��� ������ ����� � �����, ��������� �� ����� ��������/���������). ��� ������� � ������ ������
   * ������������ �� IOException (������ ���, ��� �����, ������ ����, ���� ��������� �� �� ���� � �.�.).
   * @param fileName String �������������� ����
   * @throws IOException ������ ��������� �����.
  */
  public static void removeEmptyLines(String fileName) throws IOException
   {
    // ��� ����� ������ ���� ��������
    if (!StringUtils.isBlank(fileName))
     {
      List list = FileUtils.readLines(new File(fileName));
      if ((list != null) && (!list.isEmpty()))
       {
        ArrayList<String> newList = new ArrayList<String>();
        for (Object listElement : list)
         {if ((listElement != null) && !StringUtils.isBlank((String) listElement)) {newList.add((String) listElement);}}
        FileUtils.writeLines(new File(fileName), newList);
       }
     }
    // ��� ����� �����
    else {throw new IOException("Empty file name!");}
   }

  /**
   * ������ ����� ������������ ������ ��� ������������ ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(FSUtils.class.getName());
    Logger logger = Logger.getLogger(FSUtils.class.getName());
    //logger.debug(FSUtils.isEmptyDir("c:\\temp\\"));

    try
     {
      FSUtils.removeEmptyLines("c://temp//config.xml");
     }
    catch (IOException e) {logger.error(e.getMessage());} 
    //catch (EmptyObjectException e) {logger.error(e.getMessage());}
    //catch (ClassNotFoundException e) {logger.error(e.getMessage());}

   }

 }