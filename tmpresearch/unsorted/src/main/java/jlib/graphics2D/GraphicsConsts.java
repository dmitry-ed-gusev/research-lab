package jlib.graphics2D;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 09.11.2009)
*/

public interface GraphicsConsts
 {
  public static enum ImageFormat
   {
    BMP("BMP"),
    JPEG("JPEG"),
    JPG("JPG"),
    PNG("PNG"),
    GIF("GIF");

    private final String sValue;
    ImageFormat(String sValue) {this.sValue = sValue;}
    public String strValue() {return this.sValue;}
   }

 }