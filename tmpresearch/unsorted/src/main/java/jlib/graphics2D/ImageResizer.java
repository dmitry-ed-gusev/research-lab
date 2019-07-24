package jlib.graphics2D;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Класс реализует методы для масштабирования (увеличения или уменьшения) изображений. С форматом картинок JPEG
 * работет точно. С остальными - пока не проверено. :) При обработке больших изображений (размером более 3-5 мегабайт)
 * может возникать ИС OutOfMemory - лечится увеличением памяти для Жава-машины (в основном это у метода resize()).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.06.2009)
*/

public class ImageResizer
 {
  /***/
  private static Logger logger = Logger.getLogger(ImageResizer.class.getName());

  /**
   * Изменение размера изображения.
   * @deprecated потестировать... (еще не тестировалось)
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
      // Если указан параметр isSmooth=true, то используем антиалиасинг (как возможно)
      if (isSmooth) {graphics2D.drawImage(imageToResize.getScaledInstance(genX, genY, Image.SCALE_SMOOTH), startX, startY, null);}
      // Если указан isSMooth=false, то без антиалиасинга
      else {graphics2D.drawImage(imageToResize, startX, startY, genX, genY, null);}
     }
    finally {if(graphics2D != null) {graphics2D.dispose();}}
    return bufferedImage;
   }

  /**
   * Метод с включенным антиалиасингом.
   * @deprecated потестировать... (еще не тестировалось)
  */
  public static BufferedImage resize(BufferedImage imageToResize, int width,int height)
   {return ImageResizer.resize(imageToResize, width, height, true);}

  /**
   * Изменение размеров изображения. Результирующее изображение получается гладким (smooth). Формат результирующего
   * изображения указывается как параметр, если он не указан (null), то по умолчанию результирующим будет формат JPG.
   * @param input InputStream входной поток (изображение)
   * @param output OutputStream выходной поток (результирующее изображение)
   * @param destWidth int ширина результирующего изображения (в пикселах)
   * @param destHeight int высота результирующего изображения (в пикселах)
   * @param outputFormat ImageFormat формат результирующего изображения.
   * @throws IOException ошибки при работе с файлами.
  */
  public static void resizeImageSmooth(InputStream input, OutputStream output, int destWidth, int destHeight,
   GraphicsConsts.ImageFormat outputFormat) throws IOException
   {
    // Класс для обработки графики
    Graphics2D    graphics = null;
    // Класс для хранения результирующего изображения
    BufferedImage destinationImage;
    try
     {
      // Открываем исходное изображение
      BufferedImage sourceImage = ImageIO.read(input);

      // todo: input и output картинки должны иметь разные имена... или перезаписывать исходный файл? 
      // todo: сейчас (01.04.2010) если картинки имеют одинаковое имя - возникает NullPointerException

      // Получаем размеры исходного изображения
      int sourceWidth  = sourceImage.getWidth();
      int sourceHeight = sourceImage.getHeight();
      logger.debug("SOURCE: width=" + sourceWidth + "; height=" + sourceHeight);

      // Вычисляем масштаб (пропорцию) изменения исходного изображения (ориентируясь на данные параметры результирующего
      // изображения). При этом, если высота результирующего изображения не указана, то масштаб берем по ширине.
      double scale;
      if (destHeight > 0) {scale = Math.min((double)destWidth/(double)sourceWidth, (double)destHeight/(double)sourceHeight);}
      else                {scale = (double)destWidth/(double)sourceWidth;}

      // Размеры масштабированного (изменнного) изображения
      int destinationWidth  = (int)(sourceWidth * scale);
      int destinationHeight = (int)(sourceHeight * scale); 
      logger.debug("DESTINATION: width=" + destinationWidth + "; height=" + destinationHeight);

      // Вычисляем координаты (необходимо только если конечное изображение будет такого размера, как указано во входных
      // параметрах данного метода, но тогда могут появиться лишние поля по краям. При масштабировании изображения
      // по вычисленному масштабу - координаты не нужны)
      //int x = (int)(((double)destWidth - (double)sourceWidth * scale) / 2.0d);
      //int y = (int)(((double)destHeight - (double)sourceHeight * scale) / 2.0d);

      // Создаем объект для хранения изображения (объект размером уже с маштабированное изображение)
      destinationImage = new BufferedImage(destinationWidth, destinationHeight, BufferedImage.TYPE_INT_RGB);

      // Создаем объект графики - для обработки
      graphics = destinationImage.createGraphics();

      // Устанавливаем параметры обработки изображения (rendering hints)
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
      graphics.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
      graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      
      // Получаем масштабированную копию исходного изображения
      Image scaled = sourceImage.getScaledInstance(destinationWidth, destinationHeight, Image.SCALE_SMOOTH);
      
      // Устанавливаем цвет и рисуем прямоугольник внутри графического объекта, в который будет помещено масштабированное
      // изображение. Если этого не сделать, то вроде бы изображение будет прозрачным (без белых краев при обрезке).
      // (не используется, т.к. размер контейнера для хранения масштабированного изображения совпадают - нет границ вокруг)
      //if (isWhiteBorder)
      // {
      //  graphics.setColor(Color.WHITE);
      //  graphics.fillRect(0, 0, destWidth, destHeight);
      // }

      // Непосредственно вставляем масштабированную копию изображения в графический объект
      //graphics.drawImage(scaled, x, y, null); // <- для вставления по центру графического объекта (не используем)
      graphics.drawImage(scaled, 0, 0, null); // <- изображение вставляем в левый верхний угол графического объекта

      // Список доступных форматов для записи изображений (отладочный вывод)
      //logger.debug("FORMATS->");
      //for (String formatName : ImageIO.getWriterFormatNames()) {logger.debug("-> " + formatName);}
      
     }
    // Освобождение ресурсов
    finally {if (graphics != null) {graphics.dispose();}}
    
    // Непосредственно запись на диск объекта с масштабированной копией изображения
    String format;
    // Если указан формат - используем его
    if (outputFormat != null) {format = outputFormat.strValue();}
    // Если формат не указан, то используем по умолчанию JPG
    else                      {format = GraphicsConsts.ImageFormat.JPG.strValue();}
    ImageIO.write(destinationImage, format, output);
    
    // Т.к. при записи объекта выходной поток не закрывается - закрываем поток вручную
    output.flush();
    output.close();

    // Также закроем входной поток (чтобы точно его освободить)
    input.close();
   }

  /***/
  public static void resizeImageSmooth(InputStream input, OutputStream output, int destWidth,
   GraphicsConsts.ImageFormat outputFormat) throws IOException
   {ImageResizer.resizeImageSmooth(input, output, destWidth, -1, outputFormat);}

  /**
   *
   * @deprecated код метода не реализован...
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
    FileInputStream  inImage  = new FileInputStream("c:/temp/1.jpg");
    FileOutputStream outImage = new FileOutputStream("c:/temp/1a.jpg");
    ImageResizer.resizeImageSmooth(inImage, outImage, 200, GraphicsConsts.ImageFormat.JPG);
    inImage  = new FileInputStream("c:/temp/2.jpg");
    outImage = new FileOutputStream("c:/temp/2a.jpg");
    ImageResizer.resizeImageSmooth(inImage, outImage, 200, GraphicsConsts.ImageFormat.JPG);
   }

 }