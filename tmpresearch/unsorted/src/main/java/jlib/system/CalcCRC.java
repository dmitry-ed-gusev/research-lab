package jlib.system;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.zip.CRC32;

/**
 * В данном классе реализована функция вычисления контрольной суммы CRC32 для переданного файла.
 * @author Gusev Dmitry
 * @version 1.2 
*/

public class CalcCRC
 {

  /**
   * Метод возвращает контрольную сумму CRC32 для файла fileName. Если файла не существует или подсчет не
   * удался (возникла ИС), метод возвращает значение 0.
   * @param fileName файл, для которого считаем CRC32.
   * @return long значение CRC32 или значение 0.
  */
  @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
  public static long getChecksum(String fileName)
   {
    long result = 0;
    Logger logger = Logger.getLogger(CalcCRC.class.getName());
    BufferedInputStream in = null;
    try
     {
      // Если полученное имя файла пусто - вообще ничего не делаем
      if ((fileName != null) && (!fileName.trim().equals("")))
       {
        in = new BufferedInputStream(new FileInputStream(fileName));
        CRC32 crc = new CRC32();
        int iByte;
        // Непосредственно цикл вычисления контрольной суммы файла
        while ((iByte = in.read()) != -1) {crc.update(iByte);}
        result = crc.getValue();
        logger.debug("CalcCRC: file [" + fileName + "]; result [" + result + "]");
       }
      // Сообщение о пустом имени файла
      else {logger.warn("Received file name is EMPTY!");}
     }
    // Перехватываем ИС
    catch (FileNotFoundException e) {logger.error("ERROR occured while CRC32 calculating: " + e.getMessage());}
    catch (IOException e)           {logger.error("ERROR occured while CRC32 calculating: " + e.getMessage());}
    // Обязательно необходимо закрыть за собой потоки
    finally
     {
      try{if (in != null) {in.close();}}
      catch (IOException e) {logger.error("Can't close stream for file [" + fileName + "]! Reason: [" + e.getMessage() + "].");}
     }
    return result;
   }

 }