package jlib.web.base64encoding;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;

/**
 * ������ ����� ��������� base64-����������� ����� ��� �������� �� ��������� HTTP.
 * @author Gusev Dmitry
 * @version 1.0
*/

public class Encoding
 {
  /** ���������-������ ��� ������� ������. ������������ ������ ����������. */
  public static Logger logger = Logger.getLogger(Encoding.class.getName());

  /**
   * ������ ������ ������ ����������� ����� ������ �� ������ ��������� ����������� Base64. ��������
   * ��������� Base64 ������ 3 ����� ������������� � 4 ������� �� ��������� �����:
   * |11111122|22223333|33444444|
   * ������ ����� �� 6 ����� ���������� � ������������ � �������� ����������� toBase64. ����
   * ���������� ������� ������ �� ������ 3, �� ��������� ������ �� 4 �������� �����������
   * ����� ��� ����� ��������� =. ������ �������� ������ ����� ����� �� ����� 76 ��������.
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
     * ����������� �� ���������. �������� ������-�������.
     * @param out ��������� �����-������.
    */
    public Base64OutputStream(OutputStream out)
     {
      super(out);
      logger.debug("WORKING Base64OutputStream.CONSTRUCTOR().");
     }

    /**
     * ����� ���������� � �������� ����� ��������� ����.
     * @param c int ������������ � ����� ����������.
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
   * ���������� ������������� ������������� (Base64) ������.
   * @param s String ������
   * @return Base64 ������������� s
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