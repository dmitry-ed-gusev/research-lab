package jlib.web.base64encoding;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;

/**
 * Данный класс реализует base64-кодирование строк для отправки по протоколу HTTP.
 * @author Gusev Dmitry
 * @version 1.0
*/

public class Encoding
 {
  /** Компонент-логгер для данного класса. Используется логгер приложения. */
  public static Logger logger = Logger.getLogger(Encoding.class.getName());

  /**
   * Данный фильтр потока преобразует поток байтов на основе алгоритма кодирования Base64. Согласно
   * алгоритму Base64 каждые 3 байта преобразуются в 4 символа по следующей схеме:
   * |11111122|22223333|33444444|
   * Каждый набор из 6 битов кодируется в соответствии с таблицей кодирования toBase64. Если
   * количество входных данных не кратно 3, то последняя группа из 4 символов дополняется
   * одним или двумя символами =. Каждая выходная строка имеет длину не более 76 символов.
   * @author Gusev Dmitry
   * @version 1.0
  */
  static class Base64OutputStream extends FilterOutputStream
   {
    /***/
    private int col         = 0;
    /***/
    private int i           = 0;
    /***/
    private int[] inbuf     = new int[3];
    /***/
    private char[] toBase64 =
     {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
     };

    /**
     * Конструктор по умолчанию. Создание потока-фильтра.
     * @param out созданный поток-фильтр.
    */
    public Base64OutputStream(OutputStream out)
     {
      super(out);
      logger.debug("WORKING Base64OutputStream.CONSTRUCTOR().");
     }

    /**
     * Метод записывает в выходной поток следующий байт.
     * @param c int записываемая в поток информация.
    */
    public void write(int c) throws IOException
     {
      logger.debug("ENTERING Base64OutputStream.write().");
      inbuf[i] = c;
      i++;
      if (i == 3)
       {
        super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
        super.write(toBase64[((inbuf[0] & 0x03) << 4) | ((inbuf[1] & 0xF0) >> 4)]);
        super.write(toBase64[((inbuf[1] & 0x0F) << 2) | ((inbuf[2] & 0xC0) >> 6)]);
        super.write(toBase64[inbuf[2] & 0x3F]);
        col += 4;
        i = 0;
        if (col >= 76) { super.write('\n'); col = 0; }
       }
      logger.debug("LEAVING Base64OutputStream.write().");
     }

    /***/
    public void flush() throws IOException
     {
      logger.debug("ENTERING Base64OutputStream.flush().");
      if (i == 1)
       {
        super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
        super.write(toBase64[(inbuf[0] & 0x03) << 4]);
        super.write('=');
        super.write('=');
       }
      else if (i == 2)
       {
        super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
        super.write(toBase64[((inbuf[0] & 0x03) << 4) | ((inbuf[1] & 0xF0) >> 4)]);
        super.write(toBase64[(inbuf[1] & 0x0F) << 2]);
        super.write('=');
       }
      logger.debug("LEAVING Base64OutputStream.flush().");
     }
   }

  /**
   * Вычилсение кодированного представления (Base64) строки.
   * @param s String строка
   * @return Base64 представление s
  */
  public static String base64Encode(String s)
   {
    logger.debug("ENETRING Encoding.base64Encode().");
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    Base64OutputStream out     = new Base64OutputStream(bOut);
    logger.debug("Trying to encode input string.");
    try {out.write(s.getBytes()); out.flush();}
    catch (IOException e) {logger.error("ERROR: " + e.getMessage());}
    logger.debug("LEAVING Encoding.base64Encode().");
    return bOut.toString();
   }

 }