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
  /** Логгер данного класса. */
  private static Logger logger = Logger.getLogger(RandomFileName.class.getName());

  /**
   * Метод находит и возвращает уникальное для каталога catalogPath имя файла с расширением fileExtension. Расширение
   * указывается БЕЗ точки. Параметр usePathCorrection указывает, использовать ли коррекцию (метод fixFPath) пути
   * (для catalogPath) или нет. Если указан режим без коррекции пути, то необходимо следить, чтобы указанный путь
   * оканчивался на символ-разделитель пути. Если в указанном каталоге много файлов - работа метода может занять
   * некоторое время. Если указан пустой путь к каталогу или каталога не существует - метод вернет значение null.
   * Если не указано расширение, то будет найдено уникальное имя для файла без расширения.
   * @param catalogPath String путь к каталогу.
   * @param fileExtension String расширение для файла (БЕЗ ТОЧКИ!).
   * @param usePathCorrection boolean использовать или нет коррекцию пути.
   * @return String найденное уникальное имя файла.
  */
  public static String find(String catalogPath, String fileExtension, boolean usePathCorrection)
   {
    String result = null;
    // Если указанный каталог для поиска имени существует - работаемс
    if ((!StringUtils.isBlank(catalogPath)) && (new File(catalogPath).exists()))
     {
      logger.debug("Path [" + catalogPath + "] exists! Processing.");
      // Если используется коррекция имени файла - выполняем ее
      String localPath;
      if (usePathCorrection) {localPath = FSUtils.fixFPath(catalogPath, true);}
      else                   {localPath = catalogPath;}
      // Используемое расширение файла
      String localExt;
      if (!StringUtils.isBlank(fileExtension)) {localExt = "." + fileExtension;}
      else                                     {localExt = "";}

      // Генерация случайного имени файла
      Random random = new Random();
      int randomFileName;
      File destFile;
      boolean nameFound = false;
      // В цикле генерируем имя файла до тех пор, пока не найдем уникальное (в идеале на это необходим один проход).
      do
       {
        randomFileName = random.nextInt(Integer.MAX_VALUE); // <- генерация большого случайного числа
        // Файл со случайным именем
        destFile = new File(localPath + randomFileName + localExt);
        // Если такого файла не существует - мы нашли искомое имя.
        if (!destFile.exists()) {nameFound = true;}
       }
      while (!nameFound);
      logger.debug("Found random file name [" + randomFileName + "].");
      // Сохраняем результат
      result = String.valueOf(randomFileName);
     }
    // Если же указан пустой каталог или каталог просто не существует - сообщим об этом в лог
    else {logger.warn("Path [" + catalogPath + "] is empty or doesn't exists!");}
    // Возвращаем результат
    return result;
   }

 }