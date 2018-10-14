package jlib.serialization;

import jlib.exceptions.JLibException;
import org.apache.log4j.Logger;

/**
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 03.06.11)
*/

public class Serializer
 {
  /** Компонент-логгер данного класса. */
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
    // Проверка полученного объекта - не пуст ли он. Если пуст - ошибка!
    if (object == null)                     {throw new JLibException("Object for writing is NULL!");}
    // Проверка имени файла - оно обязательно должно быть указано!
    else if (StringUtils.isBlank(fileName)) {throw new JLibException("File name for serialized object is empty!");}

    // Выбираем каталог для сериализации - если пуст указанный путь, запишем объект в текущий рабочий каталог
    String localFullPath;
    if (StringUtils.isBlank(fullPath)) {localFullPath = FSUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath());}
    else                               {localFullPath = FSUtils.fixFPath(fullPath);}

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

    // Получим расширение указанного имени файла
    String fileExtension =

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
      ZipOutputStream zout = null;
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
   */
 }