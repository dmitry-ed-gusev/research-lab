package jlib.serialization;

import jlib.exceptions.JLibException;
import org.apache.log4j.Logger;

/**
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 03.06.11)
*/

public class Serializer
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(Serializer.class.getName());

  /**
   *
   * @param object Object
   * @param fullPath String
   * @param fileName String
   * @return String
   * @throws JLibException
  */
  /*
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static String writeObject(Object object, String fullPath, String fileName)
   throws EmptyObjectException, IOException, JLibException
   {
    logger.debug("WORKING Serializer.writeObject().");
    // �������� ����������� ������� - �� ���� �� ��. ���� ���� - ������!
    if (object == null)                     {throw new JLibException("Object for writing is NULL!");}
    // �������� ����� ����� - ��� ����������� ������ ���� �������!
    else if (StringUtils.isBlank(fileName)) {throw new JLibException("File name for serialized object is empty!");}

    // �������� ������� ��� ������������ - ���� ���� ��������� ����, ������� ������ � ������� ������� �������
    String localFullPath;
    if (StringUtils.isBlank(fullPath)) {localFullPath = FSUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath());}
    else                               {localFullPath = FSUtils.fixFPath(fullPath);}

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

    // ������� ���������� ���������� ����� �����
    String fileExtension =

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
      ZipOutputStream zout = null;
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
   */
 }