package jlib.compress;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.*;

/**
 * В данном классе реализовано несколько методов для использования алгоритмов zip/gzip для
 * сжатия/распаковки текста (байтовых массивов). 
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 04.03.2009)
*/

public class Zippo
 {
  public static byte[] zip(byte[] source) throws IOException
   {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ZipOutputStream zop = new ZipOutputStream(bos);
    ZipEntry ze=new ZipEntry("name");
    zop.putNextEntry(ze);
    zop.write(source);
    zop.closeEntry();
    zop.finish();
    zop.close();
    return bos.toByteArray();
   }

  public static byte[] gzip(byte[] source) throws IOException
   {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    GZIPOutputStream zop = new GZIPOutputStream(bos);
    zop.write(source);
    zop.finish();
    zop.close();
    return bos.toByteArray();
   }

  public static byte[] unzip(byte[] source) throws IOException
   {
    ByteArrayInputStream bis = new ByteArrayInputStream(source);
    ZipInputStream zip = new ZipInputStream(bis);
    zip.getNextEntry();
    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    int read;
    byte[] buf=new byte[1024];
    while((read=zip.read(buf))!=-1){bos.write(buf,0,read);}
    zip.close();
    return bos.toByteArray();
   }

  public static byte[] ungzip(byte[] source) throws IOException
   {
    ByteArrayInputStream bis=new ByteArrayInputStream(source);
    GZIPInputStream zip=new GZIPInputStream(bis);
    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    int read;
    byte[] buf=new byte[1024];
    while((read=zip.read(buf))!=-1) {bos.write(buf,0,read);}
    zip.close();
    return bos.toByteArray();
   }

 }