package jlib.fileModel;

import jlib.logging.InitLogger;
import jlib.system.CalcCRC;
import jlib.utils.FSUtils;
import jlib.utils.FSUtilsConsts;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ����������� ������ �������� ������� �� ������� �����. �� ���������� ����� �������� ��������� ������� ������
 * �������� ������� (����������) � ������ ������ � ������ ���� � ������� �����.
 * ��������� ������� ��������� ���� � ������ �������� ���������� �� ����� �����. �� ������ �������� ����������
 * ������ ���� ������� ������ ������������. ���� � �������� ����������� ������ ������������ �� ����������� "/".
 * ������ ������ ����������� �������������� ������� �������� DiskFile. � ������ ����� ������� �����������
 * ������������� ���� � ������� ����� - ������������ ���������� �������� ������ �������� �������.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 20.11.2007)
*/

// todo: �������� ���� ������ � ��������� ����� ��������� � ������� ������� �������� - ��� ���������� ���������
 
public class FileSystem implements Serializable
 {
  /** �������� ����� ��� ������������� ����� ������ ������ � ����������� ��� ������������/��������������. */
  static final long serialVersionUID = 955774573752161639L;

  /** ���������-������ ������� ������. */
  private transient Logger              logger             = Logger.getLogger(getClass().getName());
  /** ������ �������� ���� "����". */
  private           ArrayList<DiskFile> filesList          = null;
  /** �������� ���������� ������ ����� �������� �������. �������� ���������� ������ ������������ - ����� ������! */
  private           String              homeDir            = null;
  /** 
   * ������ ���������, ������� ����� ���������� �������� ������������ �������� �������. ���� ������� - ��� ��������� ���,
   * ��� �������� ��������������/����������� ���� � ���� (����.: libs, upload, ...etc).
  */
  private           ArrayList<String>   deprecatedDirsList = null;

  /**
   * ������ ����������� �������������� ���� ������ homeDir (�������� ������� ������ �������� �������) � ���������
   * ������ deprecatedDirsList ����������� ����������. ���� ������� homeDir �� ���������� �� ������� ����� - ������.
   * ���������� ������ deprecatedDirsList ������������ ������ ����� �������� ���������� ��������� homeDir.
   * @param homeDir String �������� ���������� - ���� � ��������� (���������) �������� ������ �������� �������.
   * @param deprecatedDirsList String[] ������ ��������������� ���������.
   * @throws IOException �� ���������, ���� �������� ������� �� ���������� �� �����.
  */
  public FileSystem(String homeDir, String[] deprecatedDirsList) throws IOException
   {
    logger.debug("WORKING FileSystem(String, String[]) constructor. Homedir [" + homeDir + "].");
    // �������� ���������� �������� ��������� homeDir �� ���������� (�� �����, ���� ���������� � ��� ����������)
    if ((homeDir != null) && (!homeDir.trim().equals("")))
     {
      // ������������� �������� � ����������� ������� "/" � �����
      String dir = FSUtils.fixFPath(homeDir, true);
      // �������� ������������� �������� � ��� ��� ������������� �������
      if ((!new File(dir).exists()) || (!new File(dir).isDirectory())) 
       throw new IOException("Home catalog [" + dir + "] doesn't exists or not a directory!");
      else
       {
        // ���������� ���� "�������� �������" - homeDir
        logger.debug("Home catalog [" + dir + "] is OK! Processing.");
        this.homeDir = dir;
        // ���������� ������ ��������������� ��������� - deprecatedDirsList - ���� �������� �� ����
        if ((deprecatedDirsList != null) && (deprecatedDirsList.length > 0))
         {
          logger.debug("Parameter deprecatedDirsList not empty. Processing.");
          // ���� ����(������) �� ���������������� - ��������������
          if ((this.deprecatedDirsList == null) || (this.deprecatedDirsList.isEmpty())) this.deprecatedDirsList = new ArrayList<String>();
          // � ����� �������� �������� �� ����������� ��������� � ����-������ ���������������
          for (String deprecatedDir : deprecatedDirsList)
           {logger.debug("Adding deprecated catalog: [" + deprecatedDir + "]."); this.deprecatedDirsList.add(deprecatedDir);}
         }
        else logger.debug("No deprecated dirs for this filesystem (parameter deprecatedDirsList is empty).");
       }
     }
    else throw new IOException("Home catalog path [" + homeDir + "] is empty!");
   }

  /**
   * �����������, ���������������� ���� �� ����� ������ - homeDir (�������� ������� �������� �������) � ���������
   * ������ ������ (�������������� �������� �������). ���� ������� homeDir �� ���������� �� ������� ����� - ������.
   * ����� ��������� �������� �� ������������� � �������������� ���� ������ ���������� �������� �������� �������� �
   * ��������� (����� correctFilePath). ���� ���������� �������� ���������, ����������� �������� ����� ����������
   * �������� �������. �������������� ���������(������ ���������) ���. �.�. ����� ���������� ������� ������������
   * (�������� ���������� ������) ��������� ��������� ����� ��������� ��������(���������������).
   * @param homeDir String �������� ���������� - ���� � ��������� (���������) �������� ������ �������� �������.
   * @throws IOException �� ���������, ���� �������� ������� �� ���������� �� �����.
  */
  public FileSystem(String homeDir) throws IOException {this(homeDir, null);}

  /**
   * ����� ��������� ���� ���� � ������ ������. ���� �����������, ������ ���� �� ���������� �� �����. ����� ����������� -
   * ��������� �� ���� ������ ��������� �������� ������ �������� ������� (�������� ������� ������ ��������� ������� �
   * ������ ���� � ������� �����). ��� ���������� ����� (����� �������� ��� �������������) ������������� ��� CRC.
   * ����� ����� ����������� ����� � ������ �����������, ��� �� � ������ ������ ����� (��������� ���� � ��� CRC), ����
   * ����� ���� � ������ ��� ���� - ������ ���� �� �����������.
   * ���� ���� �� ��������� � ���� � ����� ������ � ������ deprecatedDirsList (��������������� ���������) - ���� ��
   * ����������� � ������.
   * @param filePath String ����������� � ������ ����. �������� ������ ��������� ���������� ���� � �����, �.�. ��
   * ����� ���� ����� ����������� ������������� ����� �� �����.
  */
  private void addFile(String filePath)
   {
    String path = FSUtils.fixFPath(filePath);
    logger.debug("WORKING addFile(). Processing path [" + path + "].");
    // �������� ���������� ���� � ����� (���� �� ����, ���� ���������� � ��� ������������� ����, ���� � ����� ��������
    // ���� � ��������� �������� ������ �������� ������� � �� �������� ��������� �� ������ ���������������)
    if ((path != null) && (new File(path).exists()) && (new File(path).isFile()) &&
        (path.indexOf(this.homeDir) > -1) && (!this.containsDeprecated(path)))
     {
      //logger.debug("Home: [" + this.homeDir + "]. File: [" + path + "] is OK! Adding file.");
      // �������� ������ ������� "����"
      DiskFile file = new DiskFile();
      // ���������� ����� �����: (������ ���� � �����) ����� (�������� �������)
      String savedFilePath = path.substring(path.indexOf(this.homeDir) + this.homeDir.length());
      //logger.debug("FileName [" + savedFilePath + "].");
      file.setFileName(savedFilePath);
      // ���������� ����������� ����� ��� ������� �����
      file.setCrcCode(CalcCRC.getChecksum(path));
      // ���� ������ ������ �� ��������������� - �������������� ���
      if (this.filesList == null) this.filesList = new ArrayList<DiskFile>();
      // ���������� ������� � ������. ����� ����������� ������� ���������� ���������, ��� ������ ������� � ������ ���.
      if (!this.isFileExists(file))
       {
        this.filesList.add(file);
        //logger.debug("FILESYSTEM [addFile]: file [" + file.getFileName() + "] added to list.");
       }
      else
       {logger.debug("addFile: file [" + file.getFileName() + "] already exists in the list! Can't add!");}
     }
    else {logger.warn("addFile: file path [" + path + "] is not valid or exists in deprecated list or not a file! Can't add file!");}
   }

  /**
   * ����� �������� �� ������ ���������, ������� � ��������� ����� � ������ ������ ������ ���� ��������� ������.
   * �.�. ����� ����������� - ����� ������, �� ��������� � �������� ��������� ���� � ������ ���� ������ - �������
   * ������ ����� �������� ��� �������� (private) � ��� ����� �������������� ����� ������ ����� - ��� ����������.
   * �.�. ������ ����� ����������� - ����� ����������� ������ � ���� ��������� �� �������.
   * ��� ���������� ���������� ����� � ������ ������������ ����� addFile ������� ������.
   * ���� ������� ������� ���� � ������ ��������������� - ��� ����� �� ���������(�������), ����� ��� � ��� ��������
   * ������� ���� ���� �� ������ �������� �������.
   * @param path String ���� � ���� ������ �������� �������. � ������� ���� �������� �������� ����������� ������. 
  */
  private void scanFileSystem(String path)
   {
    try
     {
      // ������� ������ "����" ��� ����������� � �������� ��������� ���� � ��������
      File pathName = new File(path);
      logger.debug("FILESYSTEM [scanFS]: processing [" + path + "][" + pathName.getName() + "].");
      // ���� ��������� ������� ����������, ������������� ������� � �� �������� ��������������� - ��������
      if (pathName.exists() && pathName.isDirectory() && (!this.containsDeprecated(pathName.getName())))
       {
        // �������� ������ ���� ������ � ������ ��������
        String[] fileNames = pathName.list();
        // � ����� �������� �� ����� ������ ���������� ������
        for (String fileName : fileNames)
         {
          // ����� ������� ������ "����"
          File file = new File(pathName.getPath(), fileName);
          // ���� ���������� "����" - ����� �������� ���������, ���������� �������� ������ �����
          if (file.isDirectory()) {scanFileSystem(file.getPath());}
          // ���� �� ���������� "����" - ����, �� ��������� ��� � ������
          else if (file.isFile()) {this.addFile(file.getPath());}
         }
       }
     }
    // �������� ��
    catch(Exception e) {logger.error("Error while scanning filesystem: " + e.getMessage());}
   }

  /**
   * ����� ��������� ���� ������� � ������ ��������������� �������. ��� �������� �� ����� ���� ������ �������, �� �����
   * ����� �������� null ��� ��������� ����������/������������� ���� � ��������, ������ ��� ������� ��������.
   * ���� �������� ������ ��� �������� - �� ����� �������� � ������. ��� ���������� � ��������� ���������� �������.
   * ����� ������ �������� � ������ ��������������� �������, ������� ��������� � ���� � ����� ������� �������
   * "�������� �������" - �.�. ������ �������� �������, ������������ � ���� homeDir ������� �������.
   * ����� ����������� ������������, ���� �� ��� � ������ ����� ������� - ���� ����, ������� �� ����������� -
   * �� ����������� ������������ ������� � ������.
   * @param dir String ������������ ��������, ������������ � ������ ���������������.
  */
  public void addDeprecatedDir(String dir)
   {
    logger.debug("WORKING addDeprecatedDir(). Trying to add deprecated [" + dir + "].");
    // ���������� ��������� ������ � ��� ������, ���� �������� �� ���� � �������� ���������� ��������
    if ((dir != null) && (!dir.trim().equals("")) /*&& (this.homeDir.indexOf(dir) == -1)*/)
     {
      // ���� ����������� ������� ������� �� ���������� - ���������� ���������������� ����
      String localDir = FSUtils.fixFPath(dir);
      // ���� ������ �� ������������������ - �������������
      if (this.deprecatedDirsList == null) {this.deprecatedDirsList = new ArrayList<String>();}
      // ��������������� ���������� �������� � ������ ��������������� (���� ������ ��� ��� � ������)
      if (!this.deprecatedDirsList.contains(localDir)) 
       {
        this.deprecatedDirsList.add(localDir.trim());
        logger.debug("addDeprecatedDir: deprecated dir [" + localDir + "] successfully added.");
       }
      else
       {logger.debug("addDeprecatedDir: deprecated dir [" + localDir + "] already exists in deprecated list!");}
     }
    else
     {logger.error("Empty path! Can't add deprecated: [" + dir + "]. Home: [" + homeDir + "].");}
   }

  /**
   * ����� ���������� �������� ������/����, � ����������� �� ����, ���� �� � ��������� ���� path �������(�) ��
   * ������ ���������������, ���� ���� - ����� ���������� �������� ������, � ��������� ������ - ����. ���� ��������
   * ���� - ����� ����� ������ �������� ����.
   * @param path String ����, ����������� �� ������� ��������������� ���������.
   * @return boolean ������/���� � ����������� �� ���������� ������ ��� ���������� ��������� �� ���� path � ������
   * ���������������.
  */
  private boolean containsDeprecated(String path)
   {
    boolean result = false;
    // ���� ������ ��������������� ��������� �� ���� � ���� �� ���� - ��������
    if ((this.deprecatedDirsList != null) && (!this.deprecatedDirsList.isEmpty()) && (path != null) && (!path.trim().equals("")))
     {
      // ��������� ����� ���������� ���� (�������� � ��������� ���� ��� �����������)
      String localPath = FSUtils.fixFPath(path);
      // ��������� ��������� ���� �� �������� (��� ������� ������ ����������� ��������� � ���)
      ArrayList<String> catList = new ArrayList<String>();
      catList.addAll(Arrays.asList(localPath.split(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER))));

      // � ����� �������������� ������ ��������������� � ���������, ���� �� ��� � ��������� ����
      for (String deprecatedDir : deprecatedDirsList)
       {
        // ���� �������������� ������� �������� ������-����������� "/", �� ���������� ��������� ������ ���
        // ��������� � ��������� ��� ���� (����������� � ���� � ������ � � ����� �� ������� "/" � ���������)
        if (deprecatedDir.indexOf(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER)) > -1)
         {
          StringBuilder localDeprecated = new StringBuilder();
          // ���� ��������������� ������� �������� ������-����������� ����� ����� � ���� - ":" (OS WIN), ��
          // ��� �������� ���������� �������� "/" ������ � ����� ����.
          if (deprecatedDir.indexOf(String.valueOf(FSUtilsConsts.DEFAULT_DRIVE_LETTER_DELIMITER)) > -1)
           {
            if (deprecatedDir.endsWith(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER))) {localDeprecated.append(deprecatedDir);}
            else {localDeprecated.append(deprecatedDir).append(FSUtilsConsts.DEFAULT_DIR_DELIMITER);}
           }
          // ���� �� ��������������� ������� �� �������� ":", �� ��� �������� ���������� ��������� �������
          // "/" � ������ � � ����� ����
          else
           {
            // ������ "/" � ������ ���� (���� ��� ��� - ���������)
            if (!deprecatedDir.startsWith(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER)))
             {localDeprecated.append(FSUtilsConsts.DEFAULT_DIR_DELIMITER);}
            localDeprecated.append(deprecatedDir); 
            // ������ "/" � ����� ���� (���� ��� ��� - ���������)
            if (!deprecatedDir.endsWith(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER)))
             {localDeprecated.append(FSUtilsConsts.DEFAULT_DIR_DELIMITER);}
           }
          // ������ ��������� ��������������� ��������� ��������� ����� ���� � ����������� ����
          if (localPath.indexOf(localDeprecated.toString()) > -1) {result = true;}
         }
        // ���� �� �������������� ������� �� �������� ������������ - ��������� ������ ��������� ��������
        else
         {if (catList.contains(deprecatedDir)) {result = true;}}
       }
     }
    // ���� ���� �������� ����������� �������� - ��������� �� ����
    if (result) {logger.warn("Path [" + path + "] contains deprecated. SKIPPED!");}
    //logger.debug("FILESYSTEM [containsDeprecated]: path [" + path + "] contains deprecated -> " + result);
    return result;
   }

  public ArrayList<String> getDeprecatedDirsList() {return this.deprecatedDirsList;}
  public void setDeprecatedDirsList(ArrayList<String> deprecatedDirsList) {this.deprecatedDirsList = deprecatedDirsList;}

  /**
   * ������ ����� ������ ������ ������ ������ �������� ������� (������� � ��������� �������� - homeDir). �����
   * �������� ��� �� ��������� - �� �������� ������ ����� ������ - scanFileSystem(). ����� ������ ������� ������
   * ���������� �������� ������� (scanFileSystem()), ������ ����� ���������� ������� ������ ������ (���� filesList)
   * � �������� null. 
  */
  public void buildFileSystem()
   {
    logger.debug("WORKING FileSystem.buildFileSystem().");
    // ���������� ������� ���������� �������� �������
    this.filesList = null;
    // ������ ������ ������ ������ �������� ������� ������
    this.scanFileSystem(this.homeDir);
   }

  /**
   * ����� ���������� ������ ������ ������ ������ �������� ������� - �������� ����������� ���� ������ filesList.
   * @return ArrayList<DiskFile> ������ ������ ������ ������ �������� ������� (����������).
  */
  public ArrayList<DiskFile> getFilesList() {return this.filesList;}

  /**
   * ����� ���������� �������� (��������) ������� ������ �������� ������� (����������) - �������� �����������
   * ���� ������� ������ homeDir.
   * @return String �������� ������� ������ �������� ������� (����������).
  */
  public String getHomeDir() {return this.homeDir;}

  /**
   * ����� ���������� ������ ������ ������ filesList ������ �������� �������. ���� ������ ���� (null) ��� � ���
   * ��� ������ - ����� ���������� �������� 0.
   * @return int ���������� ������ � ������ �������� �������.
  */
  public int getListSize()
   {
    int result;
    if ((this.filesList == null) || (this.filesList.size() <= 0)) {result = 0;}
    else {result = this.filesList.size();}
    return result;
   }

  /**
   * ����� ���������� �������� ������, ���� ���� diskFile (������) ���������� � ������ �������� ������� (�������
   * FileSystem). ���� �� ������ ����� ��� - ����� ������ �������� ����. ������ ����� ������� �� ������ equals()
   * ������� ������ - ����� ������������ �� ������������ ���� ����� � ��� ������������ �������� ���� �� ������ ���� -
   * ��� ���������� ����������.
   * @param diskFile DiskFile ������ "����", ������� ����������� �� ������� � ������� �������� �������.
   * @return boolean �������� ������/����, � ����������� �� ������� ����������� ������� "����" � ������� ��������
   * �������.
  */
  public boolean isFileExists(DiskFile diskFile)
   {
    boolean result = false;
    // ���������� ��� ������ ������ ���� �� ������
    if ((diskFile != null) && (!diskFile.isEmpty()))
     // � ����� �������� �� ���� �������� "����" ������ ������� � ���������� �� � ���������� ����������
     for (DiskFile currentFile : this.filesList) if (currentFile.equals(diskFile)) result = true;
    //logger.debug("FILESYSTEM [isFileExists]: file " + diskFile + " exists in current filesystem -> " + result);
    return result;
   }

  /**
   * ������ ����� ���������� ������ �������� ���� "����" (DiskFile), ������� ���� � ������� �������� ������� (�
   * ������� ��������� �����), �� ��� � �������� ������� fs (� ������� ����������). ���� ��� ����� �� ������� ��������
   * ������� ���� � ������� fs - ����� ���������� �������� null.
   * �.� ����� �������� �� ������ ������ ������� �������� ������� � ���������� ��� ����� � ������� ������ �������� �������.
   * ���� � ������� fs ���� ��� ����� ������� �������� ������� + ��� ��������� ������, �� ����� ������ �������� null.
   * @param foreignFS FileSystem ������ "�������� �������", � ������� ���������� ������� �������� �������.
   * @return ArrayList<String> ������ ������������� ���� ������, ������� ���� � ������� �������� �������, �� ��� � ������� fs.
  */
  public ArrayList<String> getDifferencesTo(FileSystem foreignFS)
   {
    logger.debug("WORKING FileSystem.getDifferences().");
    ArrayList<String> filesList = null;

    // todo: ���� ���� ������� ������ - ���������� ��� ��� ������ ������� �������� ������� (���� �� �� ������)?

    // ���� �������� ������ ���� - ���������� ������ ���
    if (this.getListSize() > 0)
     {
      logger.debug("Current filesystem not empty. Processing.");
      // ���� ������ �������� ������� fs ���� - ���������� ������ ������ ������� �������
      if (foreignFS.getListSize() > 0)
       {
        logger.debug("Foreign filesystem is not empty. Processing.");

        // � ����� �������� �� ������ ������ ������ �������� ������� � ���������, ���� �� ����� ����
        // � �������� ������� fs (������� �������� �������).
        /*
        for (DiskFile currentFile : this.filesList)
         {
          // �������� ������� �������� ����� � ������� fs, ���� ��� ��� ��� - ������� ���� � �������������� ������
          if (!foreignFS.isFileExists(currentFile))
           {
            logger.debug("Adding file [" + currentFile.getFileName() + "] to result list.");
            // ���� �������������� ������ ���� - �������������� ���
            if (filesList == null) {filesList = new ArrayList<String>();} filesList.add(currentFile.getFileName());
           }
         }
        */
        
        // �������� �� ������� �������� ������� fs � ������� �� ����� � ������� ������� �������
        for (DiskFile foreignFile : foreignFS.filesList)
         {
          if (!this.isFileExists(foreignFile))
           {
            logger.debug("Adding file [" + foreignFile.getFileName() + "] to result list.");
            // ���� �������������� ������ ���� - �������������� ���
            if (filesList == null) {filesList = new ArrayList<String>();}
            // ���������� ������ � �������������� ������ ��� ��� ���� �������� ���� � ����� �� ������ -
            // ������� ����� ����������� - ��������, ��� �� ��� ���� ������ �����
            if (!filesList.contains(foreignFile.getFileName())) {filesList.add(foreignFile.getFileName());}
           }
         }

       }
      else
       {
        logger.debug("Foreign filesystem is empty. No differences to current!");
        // ������������� ��������������� ������
        //filesList = new ArrayList<String>();
        //for (DiskFile file : this.filesList) {filesList.add(file.getFileName());}
       }
     }
    else
     {
      logger.debug("Current filesystem empty! Nothig compare.");
     }
    return filesList;
   }

  /**
   * ����� ��������� �������� ������� �������� ������� � ������. ������������ ������ ������ ������, ����� ������������
   * �� ������ � CRC �����. �������� �������� (home dir) �� ������������ - �������� ������� ����� ���������� �
   * ������ ������������. ����� �� ������������ ������ ����������� - ������ ����������� ������� ������.
  */
  @Override
  public boolean equals(Object obj)
   {
    // ��������� ��������� ����������� ������� ������
    boolean result = false;
    // ������� �������� ������������ �����������
    if (this == obj) result = true;
    // ���� ������� �������� �� ������ - ��������� ����� - ���� ����� �������� null ��� ������ �� ���������
    // (������ ���������� �� ������ �������) - ������������ �������� false � �������� ������������. ���� �� ���
    // ���������� ������ ������ - �������� ������� ������ � ������� ������ � ��������� ������������ ���� �����.
    else if (obj != null && this.getClass() == obj.getClass())
     {
      // ������ �� �����, ��� ������ obj ����� ��� FileSystem � �� �������� �������
      FileSystem fileSystem = (FileSystem) obj;
      if (this.filesList.equals(fileSystem.filesList)) result = true;
     }
    return result;
   }

  @Override
  public int hashCode() {return filesList.hashCode();}

  /** ����� ���������� ��������� ������������� ������� "�������� �������". */
  public String toString()
   {
    StringBuilder result = new StringBuilder("\nFILE SYSTEM OBJECT");
    result.append("\nFile init root: ").append(this.homeDir).append("\nFILES:");
    // ���������� ������ ������ � ���������� ������������� �������
    if ((this.filesList != null) && (!this.filesList.isEmpty()))
     for (DiskFile file : this.filesList) result.append(file);
    else result.append(" [NO FILES!]");
    return result.toString();
   }

  /**
   * ������ ����� ������������ ������ ��� ������������ ������ Filesystem.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jlib");
    Logger logger = Logger.getLogger(FileSystem.class.getName());
    try
     {
      FileSystem clientFS = new FileSystem("c://////temp/////\\\\\\");
      clientFS.addDeprecatedDir("ff/ff");
      clientFS.addDeprecatedDir("c:/tem");
      clientFS.buildFileSystem();
      logger.info("-->\n\n" + clientFS);
     }
    catch (IOException e) {logger.error("PROCESSING ERROR: " + e.getMessage());}

   }
  
 }