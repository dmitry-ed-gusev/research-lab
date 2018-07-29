package jlib.graphics2D;

import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * ����� ��������� ������ ��� ��������������� (���������� ��� ����������) �����������. � �������� �������� JPEG
 * ������� �����. � ���������� - ���� �� ���������. :) ��� ��������� ������� ����������� (�������� ����� 3-5 ��������)
 * ����� ��������� �� OutOfMemory - ������� ����������� ������ ��� ����-������ (� �������� ��� � ������ resize()).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.06.2009)
*/

public class ImageResizer
 {
  /***/
  private static Logger logger = Logger.getLogger(ImageResizer.class.getName());

  /**
   * ��������� ������� �����������.
   * @deprecated �������������... (��� �� �������������)
  */
  public static BufferedImage resize(BufferedImage imageToResize, int width,int height, boolean isSmooth)
   {
    float dx = ((float)width)/imageToResize.getWidth();
    float dy = ((float)height)/imageToResize.getHeight();
    int genX,genY;
    int startX,startY;
    if(imageToResize.getWidth() <= width && imageToResize.getHeight() <= height)
     {
      genX = imageToResize.getWidth();
      genY = imageToResize.getHeight();
     }
    else
     {
      if(dx<=dy) {genX = width; genY = (int) (dx*imageToResize.getHeight());}
      else       {genX = (int) (dy *imageToResize.getWidth()); genY = height;}
     }
    startX = (width - genX ) / 2;
    startY = (height - genY)  / 2;
    BufferedImage bufferedImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = null;
    try
     {
      graphics2D = bufferedImage.createGraphics();
      graphics2D.fillRect(0, 0, width, height);
      // ���� ������ �������� isSmooth=true, �� ���������� ������������ (��� ��������)
      if (isSmooth) {graphics2D.drawImage(imageToResize.getScaledInstance(genX, genY, Image.SCALE_SMOOTH), startX, startY, null);}
      // ���� ������ isSMooth=false, �� ��� �������������
      else {graphics2D.drawImage(imageToResize, startX, startY, genX, genY, null);}
     }
    finally {if(graphics2D != null) {graphics2D.dispose();}}
    return bufferedImage;
   }

  /**
   * ����� � ���������� ��������������.
   * @deprecated �������������... (��� �� �������������)
  */
  public static BufferedImage resize(BufferedImage imageToResize, int width,int height)
   {return ImageResizer.resize(imageToResize, width, height, true);}

  /**
   * ��������� �������� �����������. �������������� ����������� ���������� ������� (smooth). ������ ���������������
   * ����������� ����������� ��� ��������, ���� �� �� ������ (null), �� �� ��������� �������������� ����� ������ JPG.
   * @param input InputStream ������� ����� (�����������)
   * @param output OutputStream �������� ����� (�������������� �����������)
   * @param destWidth int ������ ��������������� ����������� (� ��������)
   * @param destHeight int ������ ��������������� ����������� (� ��������)
   * @param outputFormat ImageFormat ������ ��������������� �����������.
   * @throws IOException ������ ��� ������ � �������.
  */
  public static void resizeImageSmooth(InputStream input, OutputStream output, int destWidth, int destHeight,
   GraphicsConsts.ImageFormat outputFormat) throws IOException
   {
    // ����� ��� ��������� �������
    Graphics2D    graphics = null;
    // ����� ��� �������� ��������������� �����������
    BufferedImage destinationImage;
    try
     {
      // ��������� �������� �����������
      BufferedImage sourceImage = ImageIO.read(input);

      // todo: input � output �������� ������ ����� ������ �����... ��� �������������� �������� ����? 
      // todo: ������ (01.04.2010) ���� �������� ����� ���������� ��� - ��������� NullPointerException

      // �������� ������� ��������� �����������
      int sourceWidth  = sourceImage.getWidth();
      int sourceHeight = sourceImage.getHeight();
      logger.debug("SOURCE: width=" + sourceWidth + "; height=" + sourceHeight);

      // ��������� ������� (���������) ��������� ��������� ����������� (������������ �� ������ ��������� ���������������
      // �����������). ��� ����, ���� ������ ��������������� ����������� �� �������, �� ������� ����� �� ������.
      double scale;
      if (destHeight > 0) {scale = Math.min((double)destWidth/(double)sourceWidth, (double)destHeight/(double)sourceHeight);}
      else                {scale = (double)destWidth/(double)sourceWidth;}

      // ������� ����������������� (����������) �����������
      int destinationWidth  = (int)(sourceWidth * scale);
      int destinationHeight = (int)(sourceHeight * scale); 
      logger.debug("DESTINATION: width=" + destinationWidth + "; height=" + destinationHeight);

      // ��������� ���������� (���������� ������ ���� �������� ����������� ����� ������ �������, ��� ������� �� �������
      // ���������� ������� ������, �� ����� ����� ��������� ������ ���� �� �����. ��� ��������������� �����������
      // �� ������������ �������� - ���������� �� �����)
      //int x = (int)(((double)destWidth - (double)sourceWidth * scale) / 2.0d);
      //int y = (int)(((double)destHeight - (double)sourceHeight * scale) / 2.0d);

      // ������� ������ ��� �������� ����������� (������ �������� ��� � ��������������� �����������)
      destinationImage = new BufferedImage(destinationWidth, destinationHeight, BufferedImage.TYPE_INT_RGB);

      // ������� ������ ������� - ��� ���������
      graphics = destinationImage.createGraphics();

      // ������������� ��������� ��������� ����������� (rendering hints)
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
      graphics.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
      graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      
      // �������� ���������������� ����� ��������� �����������
      Image scaled = sourceImage.getScaledInstance(destinationWidth, destinationHeight, Image.SCALE_SMOOTH);
      
      // ������������� ���� � ������ ������������� ������ ������������ �������, � ������� ����� �������� ����������������
      // �����������. ���� ����� �� �������, �� ����� �� ����������� ����� ���������� (��� ����� ����� ��� �������).
      // (�� ������������, �.�. ������ ���������� ��� �������� ����������������� ����������� ��������� - ��� ������ ������)
      //if (isWhiteBorder)
      // {
      //  graphics.setColor(Color.WHITE);
      //  graphics.fillRect(0, 0, destWidth, destHeight);
      // }

      // ��������������� ��������� ���������������� ����� ����������� � ����������� ������
      //graphics.drawImage(scaled, x, y, null); // <- ��� ���������� �� ������ ������������ ������� (�� ����������)
      graphics.drawImage(scaled, 0, 0, null); // <- ����������� ��������� � ����� ������� ���� ������������ �������

      // ������ ��������� �������� ��� ������ ����������� (���������� �����)
      //logger.debug("FORMATS->");
      //for (String formatName : ImageIO.getWriterFormatNames()) {logger.debug("-> " + formatName);}
      
     }
    // ������������ ��������
    finally {if (graphics != null) {graphics.dispose();}}
    
    // ��������������� ������ �� ���� ������� � ���������������� ������ �����������
    String format;
    // ���� ������ ������ - ���������� ���
    if (outputFormat != null) {format = outputFormat.strValue();}
    // ���� ������ �� ������, �� ���������� �� ��������� JPG
    else                      {format = GraphicsConsts.ImageFormat.JPG.strValue();}
    ImageIO.write(destinationImage, format, output);
    
    // �.�. ��� ������ ������� �������� ����� �� ����������� - ��������� ����� �������
    output.flush();
    output.close();

    // ����� ������� ������� ����� (����� ����� ��� ����������)
    input.close();
   }

  /***/
  public static void resizeImageSmooth(InputStream input, OutputStream output, int destWidth,
   GraphicsConsts.ImageFormat outputFormat) throws IOException
   {ImageResizer.resizeImageSmooth(input, output, destWidth, -1, outputFormat);}

  /**
   *
   * @deprecated ��� ������ �� ����������...
  */
  public static void resizeImageSmooth(String sourceImage, String destImage, int destWidth, int destHeight,
   GraphicsConsts.ImageFormat outputFormat)
   {
    
   }

  /**
   * @param args
   * @throws IOException
  */
  public static void main(String[] args) throws IOException
   {
    /**
    BufferedImage  in = ImageIO.read(new File("c:/temp/1.jpg"));
    BufferedImage out = ImageResizer.resize(in, 3400, 3400);
    ImageIO.write(out, "jpg", new File("c:/temp/img1_2.jpg"));
    out = ImageResizer.resize(in, 800, 600);
    ImageIO.write(out, "jpg", new File("c:/temp/img2_2.jpg"));
    out = ImageResizer.resize(in, 100, 100);
    ImageIO.write(out, "jpg", new File("c:/temp/img3_2.jpg"));
    */
    InitLogger.initLogger("jlib");
    FileInputStream  inImage  = new FileInputStream("c:/temp/1.jpg");
    FileOutputStream outImage = new FileOutputStream("c:/temp/1a.jpg");
    ImageResizer.resizeImageSmooth(inImage, outImage, 200, GraphicsConsts.ImageFormat.JPG);
    inImage  = new FileInputStream("c:/temp/2.jpg");
    outImage = new FileOutputStream("c:/temp/2a.jpg");
    ImageResizer.resizeImageSmooth(inImage, outImage, 200, GraphicsConsts.ImageFormat.JPG);
   }

 }