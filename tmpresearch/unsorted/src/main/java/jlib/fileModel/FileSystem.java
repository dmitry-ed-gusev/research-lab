package jlib.fileModel;

import gusev.dmitry.utils.MyIOUtils;
import jlib.system.CalcCRC;
import jlib.utils.FSUtilsConsts;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Программная модель файловой системы на жестком диске. Во внутренних полях хранятся начальный каталог данной
 * файловой состемы (подсистемы) и список файлов с полным путём к каждому файлу.
 * Начальный каталог указывает путь к данной файловой подсистеме от корня диска. На момент создания экземпляра
 * класса этот каталог должен существовать. Путь к каталогу обязательно должен оканчиваться на разделитель "/".
 * Список файлов представлен типизированным списком объектов DiskFile. В каждом таком объекте указывается
 * относительный путь к данному файлу - относительно начального каталога данной файловой системы.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 20.11.2007)
*/

// todo: хранение имен файлов и каталогов лучше перевести в верхний регистр символов - для унификации сравнения
 
public class FileSystem implements Serializable
 {
  /** Параметр нужен для совместимости новых версий класса с предыдущими при сериализации/десериализации. */
  static final long serialVersionUID = 955774573752161639L;

  /** Компонент-логгер данного класса. */
  private transient Logger              logger             = Logger.getLogger(getClass().getName());
  /** Список объектов типа "файл". */
  private           ArrayList<DiskFile> filesList          = null;
  /** Корневая директория данной части файловой системы. Корневая директория должна существовать - иначе ошибка! */
  private           String              homeDir            = null;
  /** 
   * Список каталогов, которые будут обходиться функцией сканирования файловой системы. Один каталог - это строковое имя,
   * без указания относительного/абсолютного пути к нему (напр.: libs, upload, ...etc).
  */
  private           ArrayList<String>   deprecatedDirsList = null;

  /**
   * Данный конструктор инициализирует поле класса homeDir (домашний каталог данной файловой системы) и заполняет
   * список deprecatedDirsList переданными значениями. Если каталог homeDir не существует на жестком диске - ошибка.
   * Заполнение списка deprecatedDirsList производится только после проверки валидности параметра homeDir.
   * @param homeDir String значение переменной - путь к корневому (домашнему) каталогу данной файловой системы.
   * @param deprecatedDirsList String[] список неиндексируемых каталогов.
   * @throws IOException ИС возникает, если корневой каталог не существует на диске.
  */
  public FileSystem(String homeDir, String[] deprecatedDirsList) throws IOException
   {
    logger.debug("WORKING FileSystem(String, String[]) constructor. Homedir [" + homeDir + "].");
    // Проверка указанного значения параметра homeDir на валидность (не пусто, путь существует и это директория)
    if ((homeDir != null) && (!homeDir.trim().equals("")))
     {
      // Корректоровка значения с добавлением символа "/" в конец
      String dir = MyIOUtils.fixFPath(homeDir, true);
      // Проверка существования каталога и что это действительно каталог
      if ((!new File(dir).exists()) || (!new File(dir).isDirectory())) 
       throw new IOException("Home catalog [" + dir + "] doesn't exists or not a directory!");
      else
       {
        // Заполнение поля "корневой каталог" - homeDir
        logger.debug("Home catalog [" + dir + "] is OK! Processing.");
        this.homeDir = dir;
        // Заполнение списка неиндексируемых каталогов - deprecatedDirsList - если параметр не пуст
        if ((deprecatedDirsList != null) && (deprecatedDirsList.length > 0))
         {
          logger.debug("Parameter deprecatedDirsList not empty. Processing.");
          // Если поле(список) не инициализировано - инициализируем
          if ((this.deprecatedDirsList == null) || (this.deprecatedDirsList.isEmpty())) this.deprecatedDirsList = new ArrayList<String>();
          // В цикле копируем каталоги из переданного параметра в поле-список неиндексируемых
          for (String deprecatedDir : deprecatedDirsList)
           {logger.debug("Adding deprecated catalog: [" + deprecatedDir + "]."); this.deprecatedDirsList.add(deprecatedDir);}
         }
        else logger.debug("No deprecated dirs for this filesystem (parameter deprecatedDirsList is empty).");
       }
     }
    else throw new IOException("Home catalog path [" + homeDir + "] is empty!");
   }

  /**
   * Конструктор, инициализирующий одно из полей класса - homeDir (корневой каталог файловой системы) и создающий
   * список файлов (инедксирование файловой системы). Если каталог homeDir не существует на жестком диске - ошибка.
   * Перед проверкой каталога на существование и инициализацией поля класса переданное значение проходит проверку и
   * коррекцию (метод correctFilePath). Если переданное значение корректно, конструктор вызывает метод построения
   * файловой системы. Неидексируемых каталогов(внутри корневого) нет. Т.о. после выполнения данного конструктора
   * (создания экземпляра класса) созданный экземпляр будет полностью настроен(инициализирован).
   * @param homeDir String значение переменной - путь к корневому (домашнему) каталогу данной файловой системы.
   * @throws IOException ИС возникает, если корневой каталог не существует на диске.
  */
  public FileSystem(String homeDir) throws IOException {this(homeDir, null);}

  /**
   * Метод добавляет один файл к списку файлов. Файл добавляется, только если он существует на диске. Также проверяется -
   * находится ли файл внутри корневого каталога данной файловой системы (корневой каталог должен полностью входить в
   * полный путь к данному файлу). Для указанного файла (после проверки его существования) высчитывается код CRC.
   * Также перед добавлением файла к списку проверяется, нет ли в списке такого файла (совпадают путь и код CRC), если
   * такой файл в списке уже есть - данный файл не добавляется.
   * Если один из каталогов в пути к файлу входит в список deprecatedDirsList (неиндексируемых каталогов) - файл не
   * добавляется в список.
   * @param filePath String добавляемый к списку файл. Параметр должен содержать АБСОЛЮТНЫЙ путь к файлу, т.к. по
   * этому пути будет проверяться существование файла на диске.
  */
  private void addFile(String filePath)
   {
    String path = MyIOUtils.fixFPath(filePath, true);
    logger.debug("WORKING addFile(). Processing path [" + path + "].");
    // Проверка валидности пути к файлу (путь не пуст, файл существует и это действительно файл, путь к файлу содержит
    // путь к корневому каталогу данной файловой системы и не содержит каталогов из списка неиндексируемых)
    if ((path != null) && (new File(path).exists()) && (new File(path).isFile()) &&
        (path.indexOf(this.homeDir) > -1) && (!this.containsDeprecated(path)))
     {
      //logger.debug("Home: [" + this.homeDir + "]. File: [" + path + "] is OK! Adding file.");
      // Создание нового объекта "файл"
      DiskFile file = new DiskFile();
      // Вычисление имени файла: (полный путь к файлу) минус (корневой каталог)
      String savedFilePath = path.substring(path.indexOf(this.homeDir) + this.homeDir.length());
      //logger.debug("FileName [" + savedFilePath + "].");
      file.setFileName(savedFilePath);
      // Вычисление контрольной суммы для данного файла
      file.setCrcCode(CalcCRC.getChecksum(path));
      // Если список файлов не инициализирован - инициализируем его
      if (this.filesList == null) this.filesList = new ArrayList<DiskFile>();
      // Добавление объекта в список. Перед добавлением объекта необходимо убедиться, что такого объекта в списке нет.
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
   * Метод проходит по дереву каталогов, начиная с указанной точки и строит полный список всех найденных файлов.
   * Т.к. метод рекурсивный - обход дерева, он принимает в качестве параметра путь к новуму узлу дерева - поэтому
   * данный метод объявлен как закрытый (private) и его вызов осуществляется через другой метод - без параметров.
   * Т.к. данный метод рекурсивный - много отладочного вывода в него добавлять не следует.
   * Для добавления найденного файла в список используется метод addFile данного класса.
   * Если текущий каталог есть в списке неиндексируемых - его метод не сканирует(обходит), также как и все каталоги
   * лежащие ниже него на дереве файловой системы.
   * @param path String путь к узлу дерева файловой системы. С данного узла начнется просмотр нижележащих ветвей. 
  */
  private void scanFileSystem(String path)
   {
    try
     {
      // Создали объект "файл" для переданного в качестве параметра пути к каталогу
      File pathName = new File(path);
      logger.debug("FILESYSTEM [scanFS]: processing [" + path + "][" + pathName.getName() + "].");
      // Если указанный каталог существует, действительно каталог и не является неиндексируемым - работаем
      if (pathName.exists() && pathName.isDirectory() && (!this.containsDeprecated(pathName.getName())))
       {
        // Получаем список всех файлов в данном каталоге
        String[] fileNames = pathName.list();
        // В цикле проходим по всему списку полученных файлов
        for (String fileName : fileNames)
         {
          // Опять создаем объект "файл"
          File file = new File(pathName.getPath(), fileName);
          // Если полученный "файл" - снова является каталогом, рекурсивно вызываем данный метод
          if (file.isDirectory()) {scanFileSystem(file.getPath());}
          // Если же полученный "файл" - файл, то добавляем его в список
          else if (file.isFile()) {this.addFile(file.getPath());}
         }
       }
     }
    // Перехват ИС
    catch(Exception e) {logger.error("Error while scanning filesystem: " + e.getMessage());}
   }

  /**
   * Метод добавляет один каталог к списку неиндексируемых модулем. Имя каталога не может быть пустой строкой, не может
   * иметь значение null или содержать абсолютный/относительный путь к каталогу, ТОЛЬКО имя нужного каталога.
   * Если параметр прошел все проверки - он будет добавлен в список. При добавлении у параметра обрезаются пробелы.
   * Также нельзя добавить в список неиндексируемых каталог, который находится в пути к корню данного объекта
   * "файловая система" - т.е. нельзя добавить каталог, содержащийся в пути homeDir данного объекта.
   * Перед добавлением ппроверяется, есть ли уже в списке такой каталог - если есть, каталог не добавляется -
   * не допускается дублирование записей в списке.
   * @param dir String наименование каталога, добавляемого в список неиндексируемых.
  */
  public void addDeprecatedDir(String dir)
   {
    logger.debug("WORKING addDeprecatedDir(). Trying to add deprecated [" + dir + "].");
    // Добавление выполняем только в том случае, если параметр не пуст и содержит допустимое значение
    if ((dir != null) && (!dir.trim().equals("")) /*&& (this.homeDir.indexOf(dir) == -1)*/)
     {
      // Если запрещенный каталог состоит из нескольких - необходимо откорректировать путь
      String localDir = MyIOUtils.fixFPath(dir, false);
      // Если список не проинициализирован - инициализация
      if (this.deprecatedDirsList == null) {this.deprecatedDirsList = new ArrayList<String>();}
      // Непосредственно добавление каталога в список неиндексируемых (если такого еще нет в списке)
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
   * Метод возвращает значение ИСТИНА/ЛОЖЬ, в зависимости от того, есть ли в указанном пути path каталог(и) из
   * списка неиндексируемых, если есть - метод возвращает значение ИСТИНА, в противном случае - ЛОЖЬ. Если параметр
   * пуст - метод также вернет значение ЛОЖЬ.
   * @param path String путь, проверяемый на наличие неиндексируемых каталогов.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от нахождения одного или нескольких каталогов из пути path в списке
   * неиндексируемых.
  */
  private boolean containsDeprecated(String path)
   {
    boolean result = false;
    // Если список неиндексируемых каталогов не пуст и путь не пуст - работаем
    if ((this.deprecatedDirsList != null) && (!this.deprecatedDirsList.isEmpty()) && (path != null) && (!path.trim().equals("")))
     {
      // Локальная копия указанного пути (пофиксим в указанном пути все разделители)
      String localPath = MyIOUtils.fixFPath(path, false);
      // Разбиваем строковый путь на каталоги (для точного поиска запрещенных каталогов в нем)
      ArrayList<String> catList = new ArrayList<String>();
      catList.addAll(Arrays.asList(localPath.split(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER))));

      // В цикле пересматриваем список неиндексируемых и проверяем, есть ли они в указанном пути
      for (String deprecatedDir : deprecatedDirsList)
       {
        // Если неидексируемый каталог содержит символ-разделитель "/", то необходимо проверять полное его
        // вхождение в указанный нам путь (присобачить к нему в начало и в конец по символу "/" и проверить)
        if (deprecatedDir.indexOf(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER)) > -1)
         {
          StringBuilder localDeprecated = new StringBuilder();
          // Если неиндексируемый каталог содержит символ-разделитель буквы диска и пути - ":" (OS WIN), то
          // для проверки необходимо добавить "/" только в конец пути.
          if (deprecatedDir.indexOf(String.valueOf(FSUtilsConsts.DEFAULT_DRIVE_LETTER_DELIMITER)) > -1)
           {
            if (deprecatedDir.endsWith(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER))) {localDeprecated.append(deprecatedDir);}
            else {localDeprecated.append(deprecatedDir).append(FSUtilsConsts.DEFAULT_DIR_DELIMITER);}
           }
          // Если же неиндексируемый каталог не содержит ":", то для проверки необходимо добавлять символы
          // "/" в начало и в конец пути
          else
           {
            // Символ "/" в начале пути (если его нет - добавляем)
            if (!deprecatedDir.startsWith(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER)))
             {localDeprecated.append(FSUtilsConsts.DEFAULT_DIR_DELIMITER);}
            localDeprecated.append(deprecatedDir); 
            // Символ "/" в конце пути (если его нет - добавляем)
            if (!deprecatedDir.endsWith(String.valueOf(FSUtilsConsts.DEFAULT_DIR_DELIMITER)))
             {localDeprecated.append(FSUtilsConsts.DEFAULT_DIR_DELIMITER);}
           }
          // Теперь проверяем непосредственно вхождение указанной части пути в проверяемый путь
          if (localPath.indexOf(localDeprecated.toString()) > -1) {result = true;}
         }
        // Если же неидексируемый каталог не содержит разделителей - проверяем точное вхождение каталога
        else
         {if (catList.contains(deprecatedDir)) {result = true;}}
       }
     }
    // Если путь содержал запрещенные каталоги - оповестим об этом
    if (result) {logger.warn("Path [" + path + "] contains deprecated. SKIPPED!");}
    //logger.debug("FILESYSTEM [containsDeprecated]: path [" + path + "] contains deprecated -> " + result);
    return result;
   }

  public ArrayList<String> getDeprecatedDirsList() {return this.deprecatedDirsList;}
  public void setDeprecatedDirsList(ArrayList<String> deprecatedDirsList) {this.deprecatedDirsList = deprecatedDirsList;}

  /**
   * Данный метод строит список файлов данной файловой системы (начиная с корневого каталога - homeDir). Метод
   * действия сам не выполняет - он вызывает другой метод класса - scanFileSystem(). Перед каждым вызовом метода
   * индексации файловой системы (scanFileSystem()), данный метод сбрасывает текущий список файлов (поле filesList)
   * в значение null. 
  */
  public void buildFileSystem()
   {
    logger.debug("WORKING FileSystem.buildFileSystem().");
    // Сбрасываем текущее содержимое файловой системы
    this.filesList = null;
    // Строим список файлов данной файловой системы заново
    this.scanFileSystem(this.homeDir);
   }

  /**
   * Метод возвращает полный список файлов данной файловой системы - значение внутреннего поля класса filesList.
   * @return ArrayList<DiskFile> полный список файлов данной файловой системы (подсистемы).
  */
  public ArrayList<DiskFile> getFilesList() {return this.filesList;}

  /**
   * Метод возвращает корневой (домашний) каталог данной файловой системы (подсистемы) - значение внутреннего
   * поля данного класса homeDir.
   * @return String корневой каталог данной файловой системы (подсистемы).
  */
  public String getHomeDir() {return this.homeDir;}

  /**
   * Метод возвращает размер списка файлов filesList данной файловой системы. Если список пуст (null) или в нем
   * нет файлов - метод возвращает значение 0.
   * @return int количество файлов в данной файловой системе.
  */
  public int getListSize()
   {
    int result;
    if ((this.filesList == null) || (this.filesList.size() <= 0)) {result = 0;}
    else {result = this.filesList.size();}
    return result;
   }

  /**
   * Метод возвращает значение ИСТИНА, если файл diskFile (объект) существует в данной файловой системе (объекте
   * FileSystem). Если же такого файла нет - метод вернет значение ЛОЖЬ. Данный метод основан на методе equals()
   * данного класса - файлы сравниваются по идентичности всех полей и при несовпадении значения хотя бы одного поля -
   * они признаются различными.
   * @param diskFile DiskFile объект "файл", который проверяется на наличие в текущей файловой системе.
   * @return boolean значение ИСТИНА/ЛОЖЬ, в зависимости от наличия переданного объекта "файл" в текущей файловой
   * системе.
  */
  public boolean isFileExists(DiskFile diskFile)
   {
    boolean result = false;
    // Переданный нам объект должен быть не пустым
    if ((diskFile != null) && (!diskFile.isEmpty()))
     // В цикле проходим по всем объектам "файл" данной системы и сравниваем их с переданным параметром
     for (DiskFile currentFile : this.filesList) if (currentFile.equals(diskFile)) result = true;
    //logger.debug("FILESYSTEM [isFileExists]: file " + diskFile + " exists in current filesystem -> " + result);
    return result;
   }

  /**
   * Данный метод возвращает список объектов типа "файл" (DiskFile), которые есть в текущей файловой системе (к
   * которой относится метод), но нет в файловой системе fs (с которой сравниваем). Если все файлы из текущей файловой
   * системы есть в системе fs - метод возвращает значение null.
   * Т.е метод проходит по списку файлов текущей файловой системы и сравнивает эти файлы с файлами другой файловой системы.
   * Если в системе fs есть все файлы текущей файловой системы + еще несколько файлов, то метод вернет значение null.
   * @param foreignFS FileSystem объект "файловая система", с которым сравниваем текущую файловую систему.
   * @return ArrayList<String> список относительных имен файлов, которые есть в текущей файловой системе, но нет в системе fs.
  */
  public ArrayList<String> getDifferencesTo(FileSystem foreignFS)
   {
    logger.debug("WORKING FileSystem.getDifferences().");
    ArrayList<String> filesList = null;

    // todo: если пуст текущий список - возвращать или нет список внешней файловой системы (если он не пустой)?

    // Если исходный список пуст - сравнивать смысла нет
    if (this.getListSize() > 0)
     {
      logger.debug("Current filesystem not empty. Processing.");
      // Если список файловой системы fs пуст - возвращаем список файлов текущей системы
      if (foreignFS.getListSize() > 0)
       {
        logger.debug("Foreign filesystem is not empty. Processing.");

        // В цикле проходим по списку файлов данной файловой системы и проверяем, есть ли такой файл
        // в файловой системе fs (внешняя файловая система).
        /*
        for (DiskFile currentFile : this.filesList)
         {
          // Проверка наличия текущего файла в системе fs, если его там нет - добавим файл в результирующий список
          if (!foreignFS.isFileExists(currentFile))
           {
            logger.debug("Adding file [" + currentFile.getFileName() + "] to result list.");
            // Если результирующий список пуст - инициализируем его
            if (filesList == null) {filesList = new ArrayList<String>();} filesList.add(currentFile.getFileName());
           }
         }
        */
        
        // Проходим по внешней файловой системе fs и сверяем ее файлы с файлами текущей системы
        for (DiskFile foreignFile : foreignFS.filesList)
         {
          if (!this.isFileExists(foreignFile))
           {
            logger.debug("Adding file [" + foreignFile.getFileName() + "] to result list.");
            // Если результирующий список пуст - инициализируем его
            if (filesList == null) {filesList = new ArrayList<String>();}
            // Предыдущим циклом в результирующий список уже мог быть добавлен файл с таким же именем -
            // поэтому перед добавлением - проверим, нет ли уже тама такого файла
            if (!filesList.contains(foreignFile.getFileName())) {filesList.add(foreignFile.getFileName());}
           }
         }

       }
      else
       {
        logger.debug("Foreign filesystem is empty. No differences to current!");
        // Инициализация результирующего списка
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
   * Метод позволяет сравнить текущую файловую систему с другой. Сравниваются только списки файлов, файлы сравниваются
   * по именам и CRC кодам. Домашние каталоги (home dir) не сравниваются - файловые системы могут находиться в
   * разных подкаталогах. Также не сравниваются списки ограничений - ТОЛЬКО содеражимое списков файлов.
  */
  @Override
  public boolean equals(Object obj)
   {
    // Результат сравнения экземпляров данного класса
    boolean result = false;
    // Быстрая проверка идентичности экземпляров
    if (this == obj) result = true;
    // Если быстрая проверка не прошла - проверяем далее - если явный параметр null или классы не совпадают
    // (данные экземпляры от разных классов) - возвращается значение false и проверки прекращаются. Если же это
    // экземпляры одного класса - приводим внешний объект к данному классу и проверяем соответствие имен полей.
    else if (obj != null && this.getClass() == obj.getClass())
     {
      // Теперь мы знаем, что объект obj имеет тип FileSystem и не является нулевым
      FileSystem fileSystem = (FileSystem) obj;
      if (this.filesList.equals(fileSystem.filesList)) result = true;
     }
    return result;
   }

  @Override
  public int hashCode() {return filesList.hashCode();}

  /** Метод возвращает строковое представление объекта "файловая система". */
  public String toString()
   {
    StringBuilder result = new StringBuilder("\nFILE SYSTEM OBJECT");
    result.append("\nFile init root: ").append(this.homeDir).append("\nFILES:");
    // Добавление списка файлов к строковому представлению объекта
    if ((this.filesList != null) && (!this.filesList.isEmpty()))
     for (DiskFile file : this.filesList) result.append(file);
    else result.append(" [NO FILES!]");
    return result.toString();
   }

}