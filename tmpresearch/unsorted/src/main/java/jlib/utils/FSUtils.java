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
 * Класс реализует некоторые утилиты для работы с файловой системой (FSUtils -> File System Utils).
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 25.04.2011)
*/

public class FSUtils
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(FSUtils.class.getName());

  /**
   * Метод удаляет дерево каталогов, начиная с указанного пути dir (этот каталог также будет удален). Если указан не
   * каталог, а файл - он просто будет удален. Если указан несуществующий путь - ничего не произойдет. Если удаляемый
   * каталог или файл занят другой программой, то будет выдано сообщение об ошибке и данный объект будет пропущен (ИС не
   * будет возбуждена).
   * @param dir String каталог или файл для удаления.
  */
  public static void delTree(String dir)
   {
    // Создали объект "файл" для переданного в качестве параметра пути к каталогу
    File pathName = new File(dir);
    
    // Если указанный каталог существует - работаем
    if (pathName.exists())
     {
      // Если данный "файл" - каталог - нужно удалить в нем все файлы и продолжить рекурсию
      if (pathName.isDirectory())
       {
        // Получаем список всех файлов в данном каталоге
        String[] fileNames = pathName.list();
        // Если список файлов пуст - удаляем каталог
        if ((fileNames == null) || (fileNames.length <= 0))
         {
          logger.debug("DELTREE: deleting dir [" + pathName.getPath() + "].");
          if (!pathName.delete()) logger.error("Can't delete dir [" + pathName.getPath() + "]!");
         }
        else
         {
          // В цикле проходим по всему списку полученных файлов
          for (String fileName : fileNames)
           {
            // Опять создаем объект "файл"
            File file = new File(pathName.getPath(), fileName);
            // Если полученный объект "файл" - является каталогом, рекурсивно вызываем данный метод
            if (file.isDirectory()) FSUtils.delTree(file.getPath());
            // Если же полученный объект "файл" - файл, то удаляем его
            else if (file.isFile()) if (!file.delete()) logger.error("Can't delete file [" + file.getPath() + "]!");
           }
          // Удаляем текущий каталог после удаления из него всех файлов
          logger.debug("DELTREE: deleting dir [" + pathName.getPath() + "].");
          if (!pathName.delete()) logger.error("Can't delete dir [" + pathName.getPath() + "]!");
         }
       }
      // Если же "файл" - просто файл - удаляем его
      else if (!pathName.delete()) logger.error("Can't delete file [" + pathName.getPath() + "]!");
     }
    else logger.warn("Specifyed path [" + dir + "] doesn't exists!");
   }

  /**
   * Метод очищает указанный каталог dir от содержимого (используется метод delTree() данного класса). Если указан
   * несуществующий каталог - ничего выполнено не будет. Если указан файл - существующий или нет - также ничего
   * выполнено не будет.
   * @param dir String каталог, очищаемый от содержимого.
  */
  public static void clearDir(String dir)
   {
    logger.info("Clearing catalog [" + dir + "].");
    // Если каталог не существует или это файл - ничего не делаем
    if ((new File(dir).exists()) && (new File(dir).isDirectory()))
    {
     // Удаление полностью дерева каталогов вместе с родительским
     FSUtils.delTree(dir);
     // Воссоздание удаленного родительского каталога
     if (!new File(dir).mkdirs()) logger.error("Can't re-create catalog [" + dir + "]!");
    }
    else logger.warn("Path [" + dir + "] doesn't exists or not a directory!");
   }

  /**
   * Метод заменяет в переданном ему пути к файлу все разделители на стандартный - DEFAULT_DIR_DELIMITER
   * (см. JLibConsts). Если переданный путь пуст (null или пустая строка) - метод возвращает тоже самое значение
   * (null или пустая строка). Если путь начинается с символа-разделителя - этот символ не пропадет, он будет
   * сконвертирован в правильный (если это необходимо).
   * @param fPath String путь к файлу, переданный для коррекции разделителей.
   * @param appendSlash boolean добавлять или нет в конец откорректированного пути символ "слэш". 
   * @return откорректированный путь к файлу или значение null.
  */
  public static String fixFPath(String fPath, boolean appendSlash) 
   {
    String result = fPath;
    // Если путь не пуст - работаем
    if (!StringUtils.isBlank(fPath))
     {
      // В цикле проходим по всему массиву запрещенных символов и заменяем их на стандартный разделитель
      for (char aDeprecated : JLibConsts.DEPRECATED_DELIMITERS)
       {result = result.replace(aDeprecated, JLibConsts.DEFAULT_DIR_DELIMITER);}
      // Есть ли уже на конце данного пути символ-разделитель
      boolean isEndsWithDelimiter = (result.endsWith(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER)));

      // Теперь разбирем полученный путь на части, разделитель - стандартный. Это необходимо для уничтожения
      // конструкций вида / // /// //// и т.п.
      String[] splittedResult = result.split(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER));
      StringBuilder resultPath = new StringBuilder();
      // Если исходный путь начинался с символа-разделителя, то этот символ не должен пропасть
      if (result.startsWith(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER)))
       {resultPath.append(JLibConsts.DEFAULT_DIR_DELIMITER);}
      // Если путь уже оканчивается на символ-разделитель, то граница добавления разделителей сдвигается на 1
      int delimiterBoundary;
      if (isEndsWithDelimiter) {delimiterBoundary = splittedResult.length;}
      else                     {delimiterBoundary = splittedResult.length - 1;}
      // В цикле собираем обратно разобранный путь
      for (int i = 0; i < splittedResult.length; i++)
       {
        // Если текущий каталог не пустой - добавляем его к результирующему пути
        if (!StringUtils.isBlank(splittedResult[i]))
         {
          // Добавляем каталог
          resultPath.append(splittedResult[i]);
          // Добавляем разделитель
          if (i < delimiterBoundary) {resultPath.append(JLibConsts.DEFAULT_DIR_DELIMITER);}
         }
       }
      result = resultPath.toString();
      // Если указана опция - добавлять слэш - добавляем его (если путь еще не содержит "/" в конце). Также
      // необходимо проверить - не файл ли это? Если это путь к файлу - слэш добавлять низзя!!!
      if (appendSlash && (!result.endsWith(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER))) &&
                         (!new File(result).isFile()))
       {result += String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER);}
     }
    //logger.debug("WORKING FSUtils.fixFPath(). RESULT: " + result);
    return result;
   }

  /**
   * Метод заменяет в переданном ему пути к файлу все разделители на стандартный - DEFAULT_DIR_DELIMITER
   * (см. JLibConsts). Если переданный путь пуст - метод возвращает значение null. Данный метод для работы
   * использует другой метод - FSUtils.fixFPath(String, boolean).
   * @param fPath String путь к файлу, переданный для коррекции разделителей.
   * @return откорректированный путь к файлу или значение null.
  */
  public static String fixFPath(String fPath) {return FSUtils.fixFPath(fPath, false);}
  
  /**
   * Метод сериализует указанный объект object в файл с именем fileName и расширением
   * JLibConsts.SERIALIZED_OBJECT_EXTENSION. Полученный файл будет сохранен в каталог fullPath, если такого
   * каталога нет - он будет создан. Затем файл будет заархивирован (в этом же каталоге) и получит имя
   * fileName и расширение JLibConsts.ZIPPED_OBJECT_EXTENSION. Файл с сериализованным объектом будет
   * удален. Возвратит метод полный путь к полученному архивному файлу (путь + имя файла).<br>
   * <b>ВАЖНО!</b> Сериализуемый объект обязательно должен реализовывать интерфейс Serializable!
   * @param object Object сериализуемый объект.
   * @param fullPath String путь к каталогу для сериализации объекта. Если путь пуст - объект будет сериализован в
   * текущий каталог.
   * @param fileName String имя файла для сериализованного объекта и его архива (УКАЗЫВАЕТСЯ БЕЗ РАСШИРЕНИЯ!).
   * @param fileExt String расширение архивного файла с объектом. Если не указано - используется расширение
   * по умолчанию - JLibConsts.ZIPPED_OBJECT_EXTENSION. Расширение указывается БЕЗ точки!
   * @param useFilePathCorrection boolean использовать или нет функцию коррекции файлового пути для указанного
   * полного пути к сериализуемому файлу. Если функция используется, то не работают UNC-пути (\\server\folder).
   * @return String полный путь к сериализованному и заархивированному объекту.
   * @throws jlib.exceptions.EmptyObjectException ошибка - переданный для сериализации объект пуст.
   * @throws IOException ошибка ввода/вывода при работе с файловой системой.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static String serializeObject(Object object, String fullPath, String fileName,
   String fileExt, boolean useFilePathCorrection) throws EmptyObjectException, IOException
   {
    logger.debug("WORKING FSUtils.serializeObject().");
    // Проверка полученного объекта - не пуст ли он
    if (object == null) {throw new EmptyObjectException("Current object is NULL!");}

    // Проверка полученного имени файла для сохранения объекта. Если имя файла не указано, вместо него будет
    // использовано имя класса(объекта).
    String localFileName;
    if (StringUtils.isBlank(fileName)) {localFileName = object.getClass().getSimpleName();} else {localFileName = fileName;}

    // Выбираем каталог для сериализации - если пуст указанный, сериализуем в текущий!
    String localFullPath;
    if (StringUtils.isBlank(fullPath)) {localFullPath = FSUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath());}
    else
     {
      // Если используем коррекцию файлового пути - выполняем ее
      if (useFilePathCorrection) {localFullPath = FSUtils.fixFPath(fullPath);}
      // Если коррекция не используется - берем путь из параметров без коррекции
      else                       {localFullPath = fullPath;}
     }
    
    // Если указанный каталог для сериализации не существует - создаем его
    if (!new File(localFullPath).exists())
     {
      logger.debug("Creating catalog [" + localFullPath + "].");
      boolean result = new File(localFullPath).mkdirs();
      // Если не удалось создать каталог для сериализации - ошибка!
      if (!result) {throw new IOException("Can't create catalog [" + localFullPath + "] for object!");}
     }
    // Если же каталог существует, но это не каталог (файл например) - ошибка (ИС)!
    else if (!new File(localFullPath).isDirectory())
     {throw new IOException("Path [" + localFullPath + "] is not directory!");}

    // Полный путь к файлу с сериализованным объектом
    StringBuilder fullPathSerialized = new StringBuilder(localFullPath);
    // Если путь не оканчивается на символ по умолчанию (/) - добавим в конец пути этот символ
    if (!fullPathSerialized.toString().endsWith(String.valueOf(JLibConsts.DEFAULT_DIR_DELIMITER)))
     fullPathSerialized.append(JLibConsts.DEFAULT_DIR_DELIMITER);

    // Расширение для файла с сериализованным объектом не должно совпадать с указанным нам расширением
    // для конечного файла, поэтому если расширение нам указано, то надо проверить - не совпадает ли оно
    // с расширением по умолчанию и выбрать одно из двух расширений, которое не совпадает.
    String serializedExt = ".";
    if ((fileExt != null) && (!fileExt.trim().equals("")))
     {
      if (JLibConsts.SERIALIZED_OBJECT_EXTENSION_1.equals(fileExt))
       {serializedExt += JLibConsts.SERIALIZED_OBJECT_EXTENSION_2;}
      else
       {serializedExt += JLibConsts.SERIALIZED_OBJECT_EXTENSION_1;}
     }
    // Если расширение не указано - используем первое расширение по умолчанию.
    else {serializedExt += JLibConsts.SERIALIZED_OBJECT_EXTENSION_1;}
    // Непосредственно добавляем расширение к полному пути к файлу
    fullPathSerialized.append(localFileName).append(serializedExt);

    // Полный путь к файлу с архивом сериализованного объекта.
    StringBuilder fullPathZipped = new StringBuilder(localFullPath);
    if (!fullPathZipped.toString().endsWith("/")) {fullPathZipped.append("/");}
    fullPathZipped.append(localFileName);
    
    // Если нам указали расширение - используем его, если же нет - используем расширение по умолчанию.
    if (!StringUtils.isBlank(fileExt))
     {
      // Если указанное расширение начинается с точки - добавляем его как есть, если же не с точки -
      // сначала добавим точку перед расширением файла
      if (fileExt.startsWith(".")) {fullPathZipped.append(fileExt);}
      else                         {fullPathZipped.append(".").append(fileExt);}
     }
    else {fullPathZipped.append(JLibConsts.ZIPPED_OBJECT_EXTENSION);}

    // Применение такой конструкции гарантирует закрытие потока ObjectOutputStream при возникновении ИС
    // во время записи объекта в файл (сериализации)
    ObjectOutputStream out = null;
    try
     {
      // Запись объекта в файл (сериализация)
      logger.debug("Writing object to disk.");
      out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fullPathSerialized.toString())));
      out.writeObject(object);
     }
    // ИС - что-то не так с сериализуемым классом
    catch (InvalidClassException e)
     {logger.error("Something is wrong with a class " + object.getClass().getName() + " [" + e.getMessage() + "]");}
    // ИС - сериализуемый класс не реализует интерфейс Serializable
    catch (NotSerializableException e)
     {logger.error("Class " + object.getClass().getName() + " doesn't implement the java.io.Serializable " +
                   "interface! [" + e.getMessage() + "]");}
    catch (IOException e) {logger.error("I/O error! [" + e.getMessage() + "]");}
    finally {logger.debug("Trying to close ObjectOutputStream..."); if (out != null) out.close();}

    // Применение такой конструкции гарантирует закрытие потоков при возникновении ИС во время работы с ними
    FileOutputStream fout = null;
    ZipOutputStream  zout = null;
    FileInputStream  fin  = null;
    try
     {
      // Архивация объектного файла
      fout = new FileOutputStream(fullPathZipped.toString());
      zout = new ZipOutputStream(new BufferedOutputStream(fout));
      // Уровень компрессии файлов в архиве
      zout.setLevel(Deflater.BEST_COMPRESSION);
      // Запись в архив инфы об архивируемом файле
      ZipEntry ze = new ZipEntry(localFileName + serializedExt);
      zout.putNextEntry(ze);
      // Непосредственно запись архивируемого файла в архив
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

    // Удаление исходного файла с данными
    if (!new File(fullPathSerialized.toString()).delete()) logger.warn("Can't delete source file [" + fullPathSerialized + "]!");
    
    return fullPathZipped.toString();
   }

  /**
   * Метод сериализует указанный объект object в файл с именем fileName и расширением
   * JLibConsts.SERIALIZED_OBJECT_EXTENSION. Полученный файл будет сохранен в каталог fullPath, если такого
   * каталога нет - он будет создан. Затем файл будет заархивирован (в этом же каталоге) и получит имя
   * fileName и расширение JLibConsts.ZIPPED_OBJECT_EXTENSION. Файл с сериализованным объектом будет
   * удален. Возвратит метод полный путь к полученному архивному файлу (путь + имя файла).<br>
   * <b>ВАЖНО!</b> Сериализуемый объект обязательно должен реализовывать интерфейс Serializable!
   * @param object Object сериализуемый объект.
   * @param fullPath String путь к каталогу для сериализации объекта. Если путь пуст - объект будет сериализован в
   * текущий каталог.
   * @param fileName String имя файла для сериализованного объекта и его архива (УКАЗЫВАЕТСЯ БЕЗ РАСШИРЕНИЯ!).
   * @param fileExt String расширение архивного файла с объектом. Если не указано - используется расширение
   * по умолчанию - JLibConsts.ZIPPED_OBJECT_EXTENSION. Расширение указывается БЕЗ точки!
   * @return String полный путь к сериализованному и заархивированному объекту.
   * @throws jlib.exceptions.EmptyObjectException ошибка - переданный для сериализации объект пуст.
   * @throws IOException ошибка ввода/вывода при работе с файловой системой.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static String serializeObject(Object object, String fullPath, String fileName, String fileExt)
   throws EmptyObjectException, IOException {return FSUtils.serializeObject(object, fullPath, fileName, fileExt, true);}

  /**
   * Метод сериализует указанный объект object в файл с именем fileName и расширением
   * JLibConsts.SERIALIZED_OBJECT_EXTENSION. Полученный файл будет сохранен в каталог fullPath, если такого
   * каталога нет - он будет создан. Затем файл будет заархивирован (в этом же каталоге) и получит имя
   * fileName и расширение JLibConsts.ZIPPED_OBJECT_EXTENSION. Файл с сериализованным объектом будет
   * удален. Возвратит метод полный путь к полученному архивному файлу (путь + имя файла).<br>
   * <b>ВАЖНО!</b> Сериализуемый объект обязательно должен реализовывать интерфейс Serializable!
   * @param object Object сериализуемый объект.
   * @param fullPath String путь к каталогу для сериализации объекта. Если путь пуст - объект будет сериализован в
   * текущий каталог.
   * @param fileName String имя файла для сериализованного объекта и его архива (УКАЗЫВАЕТСЯ БЕЗ РАСШИРЕНИЯ!).
   * @return String полный путь к сериализованному и заархивированному объекту.
   * @throws jlib.exceptions.EmptyObjectException ошибка - переданный для сериализации объект пуст.
   * @throws IOException ошибка ввода/вывода при работе с файловой системой.
  */
  public static String serializeObject(Object object, String fullPath, String fileName)
   throws EmptyObjectException, IOException {return FSUtils.serializeObject(object, fullPath, fileName, null);}

  /**
   * Метод сериализует указанный объект object в файл с именем fileName и расширением
   * JLibConsts.SERIALIZED_OBJECT_EXTENSION. Полученный файл будет сохранен в каталог fullPath, если такого
   * каталога нет - он будет создан. Затем файл будет заархивирован (в этом же каталоге) и получит имя
   * fileName и расширение JLibConsts.ZIPPED_OBJECT_EXTENSION. Файл с сериализованным объектом будет
   * удален. Возвратит метод полный путь к полученному архивному файлу (путь + имя файла).<br>
   * <b>ВАЖНО!</b> Сериализуемый объект обязательно должен реализовывать интерфейс Serializable!
   * @param object Object сериализуемый объект.
   * @param fullPath String путь к каталогу для сериализации объекта. Если путь пуст - объект будет сериализован в
   * текущий каталог.
   * @return String полный путь к сериализованному и заархивированному объекту.
   * @throws jlib.exceptions.EmptyObjectException ошибка - переданный для сериализации объект пуст.
   * @throws IOException ошибка ввода/вывода при работе с файловой системой.
  */
  public static String serializeObject(Object object, String fullPath) throws EmptyObjectException, IOException
   {return FSUtils.serializeObject(object, fullPath, null);}

  /**
   * Метод сериализует указанный объект object в файл с именем fileName и расширением JLibConsts.SERIALIZED_OBJECT_EXTENSION.
   * Полученный файл будет сохранен в текущий каталог. Затем файл будет заархивирован (в этом же каталоге) и получит имя
   * fileName и расширение JLibConsts.ZIPPED_OBJECT_EXTENSION. Файл с сериализованным объектом будет удален. Возвратит
   * метод полный путь к полученному архивному файлу (путь + имя файла).<br>
   * <b>ВАЖНО!</b> Сериализуемый объект обязательно должен реализовывать интерфейс Serializable!
   * @param object Object сериализуемый объект.
   * @return String полный путь к сериализованному и заархивированному объекту.
   * @throws jlib.exceptions.EmptyObjectException ошибка - переданный для сериализации объект пуст.
   * @throws IOException ошибка ввода/вывода при работе с файловой системой.
  */
  public static String serializeObject(Object object) throws EmptyObjectException, IOException
   {return FSUtils.serializeObject(object, null);}

  /**
   * Метод распаковывает и десериализует объект, который был сериализован и запакован методом serializeObject
   * данного класса. Формат архива - ZIP - [один архив - один файл с объектом], если файлов в архиве больше одного, то
   * распакован будет только первый. Параметр fullFilePath содержит полный путь к файлу архива.
   * @param filePath String полный путь к файлу архива.
   * @param deleteSource boolean удалять или нет исходный файл после удачной десериализации объекта.
   * @param useFilePathCorrection boolean использовать или нет функцию коррекции файлового пути для указанного
   * полного пути к сериализуемому файлу. Если функция используется, то не работают UNC-пути (\\server\folder).
   * @return Object распакованный и десериализованный объект.
   * @throws IOException ошибка ввода/вывода при работе с файловой системой.
   * @throws ClassNotFoundException ошибка при десериализации объекта из файла.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static Object deserializeObject(String filePath, boolean deleteSource, boolean useFilePathCorrection)
   throws IOException, ClassNotFoundException
   {
    logger.debug("WORKING FSUtils.deserializeObject().");

    // Десериализованный и распакованный объект
    Object object = null;

    // Проверка переданного пути к файлу и проверка существования файла
    if (StringUtils.isBlank(filePath)) {throw new IOException("Received path is empty!");}
    // Проверка существования
    else if (!new File(filePath).exists()) {throw new IOException("File [" + filePath + "] doesn't exists!");}
    // Проверка того, что путь указывает именно на файл
    else if (!new File(filePath).isFile()) {throw new IOException("Path [" + filePath + "] not a file!");}

    // Локальная переменная с полным путем к файлу (при этом исправили все символы-разделители в указанном пути).
    // Корректировка пути производится только в зависимости от значения параметра useFilePathCorrection.
    String localFilePath;
    if (useFilePathCorrection) {localFilePath = FSUtils.fixFPath(filePath, true);}
    else                       {localFilePath = filePath;}

    // Получаем путь к рабочему каталогу, в который будем распаковывать объект из файла. Рабочий каталог - текущий.
    String tempFilePath = FSUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath(), true);

    // Применение такой конструкции гарантирует закрытие потоков при возникновении ИС во время работы с ними
    ZipInputStream zin = null;
    String unpackedFileName = null; // <- имя распакованного файла
    try
     {
      // Распаковка первого файла из архива (читаем указанный файл архива)
      zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(localFilePath)));
      ZipEntry entry;
      int counter = 0;
      while(((entry = zin.getNextEntry()) != null) && (counter < 1))
       {
        logger.debug("Extracting from archive -> " + entry.getName());
        unpackedFileName = entry.getName();
        int count;
        byte data[] = new byte[JLibConsts.FILE_BUFFER];
        // Применение такой конструкции гарантирует закрытие потоков при возникновении ИС во время работы с ними
        BufferedOutputStream dest = null;
        try
         {
          // Пишем на диск распакованный файл. Пишем в тот же каталог, где находится исходный архив
          if (!StringUtils.isBlank(unpackedFileName)) // <- защита от NullPointerException
           {
            dest = new BufferedOutputStream(new FileOutputStream(tempFilePath + unpackedFileName), JLibConsts.FILE_BUFFER);
            while ((count = zin.read(data, 0, JLibConsts.FILE_BUFFER)) != -1) {dest.write(data, 0, count);}
            dest.flush();
           }
          // Если имя распакованного файла осталось пусто - непредвиденная ошибка (фатальная)! 
          else {throw new IOException("Unpacked file name is blank!");}
         }
        // Пытаемся освободить ресурсы
        finally {if (dest != null) dest.close();}
        zin.closeEntry();
        counter++;
       }
     }
    // Пытаемся освободить ресурсы
    finally {if (zin != null) zin.close();}

    // Применение такой конструкции гарантирует закрытие потоков при возникновении ИС во время работы с ними
    ObjectInputStream in = null;
    try
     {
      // Десериализация из файла распакованного объекта
      if (!StringUtils.isBlank(unpackedFileName)) // <- защита от NullPointerException
       {
        in     = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tempFilePath + unpackedFileName)));
        object = in.readObject();
       }
      // Если имя распакованного файла осталось пусто - непредвиденная ошибка (фатальная)! 
      else {throw new IOException("Unpacked file name is blank!");}
     }
    // Пытаемся освободить ресурсы. И в любом случае удаляем распакованный временный файл.
    finally
     {
      if (in != null) in.close();
      // Удаление распакованного файла (временного), если его имя не пусто.
      if (!StringUtils.isBlank(unpackedFileName)) // <- защита от NullPointerException
       {
        if (!new File(tempFilePath + unpackedFileName).delete())
         {logger.warn("Can't delete file [" + (tempFilePath + unpackedFileName) + "]!");}
        else
         {logger.debug("Deleted unpacked file [" + (tempFilePath + unpackedFileName) + "].");}
       }
     }
    
    // Если указано удаление исходного архивного файла с сериализованным объектом - удаляем
    if (deleteSource)
     {
      logger.debug("Trying to delete source file [" + filePath + "].");
      if (!new File(filePath).delete()) {logger.warn("Can't delete source file [" + filePath + "]!");}
      else {logger.debug("Source file [" + filePath + "] deleted successfully.");}
     }
    else
     {logger.debug("No deleting source file [" + filePath + "].");}
    // Возвращаем результат
    return object;
   }

  /**
   * Метод распаковывает и десериализует объект, который был сериализован и запакован методом serializeObject
   * данного класса. Формат архива - ZIP - [один архив - один файл с объектом], если файлов в архиве больше одного, то
   * распакован будет только первый. Параметр fullFilePath содержит полный путь к файлу архива.
   * @param filePath String полный путь к файлу архива.
   * @param deleteSource boolean удалять или нет исходный файл после удачной десериализации объекта.
   * @return Object распакованный и десериализованный объект.
   * @throws IOException ошибка ввода/вывода при работе с файловой системой.
   * @throws ClassNotFoundException ошибка при десериализации объекта из файла.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static Object deserializeObject(String filePath, boolean deleteSource)
   throws IOException, ClassNotFoundException {return FSUtils.deserializeObject(filePath, deleteSource, true);}

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, содержит ли указанный каталог файлы (если это вообще
   * каталог). Находящиеся в данном каталоге подкаталоги не учитываются, метод проверяет наличие ТОЛЬКО файлов. Если
   * указанное значение вообще пусто - метод возвращает значение ЛОЖЬ. Если путь указывает на файл (а не на каталог),
   * то метод возвращает значение ЛОЖЬ.Строго говоря, метод проверяет наличие файлов с информацией в данном каталоге,
   * а не файлов, содержащих другие файлы (это и есть каталоги).
   * @param path String абсолютный путь к каталогу, наличие файлов в котором проверяем.
   * @return boolean ИСТИНА/ЛОЖЬ - в зависимости от наличия файлов в указанном каталоге.
  */
  public static boolean containFiles(String path)
   {
    logger.debug("FSUtils.containFiles(). Checking path [" + path + "].");
    boolean result = false;
    // Если указанный путь не пуст - работаем
    if (!StringUtils.isBlank(path))
     {
      // Проверяем существование каталога и то, что это именно каталог
      File dir = new File(FSUtils.fixFPath(path));
      if (dir.exists() && dir.isDirectory())
       {
        // Получаем список содержимого каталога и смотрим - есть ли там файлы
        File[] fileList = dir.listFiles();
        int counter = 0;
        while ((counter < fileList.length) && !result)
         {
          if (fileList[counter].isFile()) {result = true;}
          counter++;
         }
       }
      // Ошибку - в лог!
      else {logger.error("Path [" + path + "] doesn't exists or not a directory!");}
     }
    // Если же путь пуст - сообщим об этом в лог
    else {logger.error("Empty path!");}
    return result;
   }

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, пуст ли указанный каталог (содержит ли указанный каталог
   * файлы или другие каталоги (если указан именно каталог). ИСТИНА - каталог пуст, ЛОЖЬ - в каталоге есть подкаталоги/файлы.
   * Если указанное значение пусто - метод возвращает значение ИСТИНА. Если путь указывает на файл (а не на каталог), то метод
   * возвращает значение ИСТИНА.
   * @param path String абсолютный путь к каталогу, пустоту которого проверяем.
   * @return boolean ИСТИНА/ЛОЖЬ - в зависимости от наличия файлов/подкаталогов в указанном каталоге.
  */
  public static boolean isEmptyDir(String path)
   {
    logger.debug("FSUtils.isEmptyDir(). Checking path [" + path + "].");
    boolean result = true;
    // Если указанный путь не пуст - работаем
    if (!StringUtils.isBlank(path))
     {
      // Проверяем существование каталога и то, что это именно каталог
      File dir = new File(FSUtils.fixFPath(path));
      if (dir.exists() && dir.isDirectory())
       {
        // Получаем список содержимого каталога и смотрим - есть ли там файлы/подкаталоги. Если мы получили
        // список = NULL, это означает ошибку и метод вернет ЛОЖЬ.
        String[] filesList = dir.list();
        if ((filesList != null) && (filesList.length > 0)) {result = false;}
       }
      // Ошибку - в лог!
      else {logger.error("Path [" + path + "] doesn't exists or not a directory!");}
     }
    // Если же путь пуст - сообщим об этом в лог
    else {logger.error("Empty path!");}
    return result;
   }

  /**
   * Метод проверяет каталог (что это именно каталог) catalogPath и если надо (параметр clearPath) очищает его. Если
   * каталога не существует - он будет создан. Если создать каталог (если он не существовал) не удалось - возникает ИС.
   * Если путь указывает не на каталог - возникает ИС. Данный метод часто используется в веб-приложениях при инициализации
   * для "проверки"/очистки/создания необходимых каталогов.
   * @param catalogPath String путь к "проверяемому" каталогу.
   * @param clearPath boolean очищать или нет указанный каталог.
   * @throws IOException ИС возникает если путь указывает не на каталог, если не удается создать каталог, если
   * указанный путь пуст.
  */
  public static void processPath(String catalogPath, boolean clearPath) throws IOException
   {
    // Если путь не пуст - обрабатываем его
    if (!StringUtils.isBlank(catalogPath))
     {
      File catalog = new File(catalogPath);
      // Если путь существует - проверяем его и очищаем (опционально)
      if (catalog.exists())
       {
        // Если это не каталог - ИС
        if (!catalog.isDirectory()) {throw new IOException("Path [" + catalogPath + "] is not a directory!");}
        // Если каталог - очищаем (в зависимости от параметра)
        else if (clearPath) {FSUtils.clearDir(catalogPath);}
       }
      // Если пути не существует - создаем. При неудаче создания - ИС
      else
       {if (!catalog.mkdirs()) {throw new IOException("Can't create catalog [" + catalogPath + "]!");}}
     }
    // Если путь пуст - ИС
    else {throw new IOException("Path is empty!");}
   }

  /**
   * Метод обрабатывает указанный файл (fileName) как текстовый и удаляет из него пустые строки (файл читается построчно
   * и перезаписывается без пустых строк и строк, состоящих из одних пробелов/табуляций). При ошибках в работе метода
   * возбуждается ИС IOException (пустое имя, нет файла, пустой файл, путь указывает не на файл и т.п.).
   * @param fileName String обрабатываемый файл
   * @throws IOException ошибки обработки файла.
  */
  public static void removeEmptyLines(String fileName) throws IOException
   {
    // Имя файла должно быть непустым
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
    // Имя файла пусто
    else {throw new IOException("Empty file name!");}
   }

  /**
   * Данный метод предназначен только для тестирования класса.
   * @param args String[] параметры метода main.
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