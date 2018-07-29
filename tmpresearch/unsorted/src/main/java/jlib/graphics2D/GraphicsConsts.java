package jlib.graphics2D;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 09.11.2009)
*/

public interface GraphicsConsts
 {
  /** Класс-перечисление форматов изображений, с которыми работает модуль изменения размеров изображения. */
  public static enum ImageFormat
   {
    BMP("BMP"),
    JPEG("JPEG"),
    JPG("JPG"),
    PNG("PNG"),
    GIF("GIF");

    // Поле (хранит значение)
    private final String sValue;
    // Конструктор
    ImageFormat(String sValue) {this.sValue = sValue;}
    // Доступ к полю
    public String strValue() {return this.sValue;}
   }

 }