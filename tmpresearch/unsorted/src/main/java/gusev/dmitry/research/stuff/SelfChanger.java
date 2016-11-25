package gusev.dmitry.research.stuff;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 10.12.2008)
*/

public class SelfChanger
 {
  public static void main(String[] args)
   {
    System.out.println("Starting...");
    try
     {
      FileUtils.copyFile(new File("MAIN.class"), new File("c:/1/MAIN.class"));
      FileUtils.copyFile(new File("commons-io-1.3.2.jar"), new File("c:/1/commons-io-1.3.2.jar"));
      
      System.out.println("Copy OK!");

      if ((args != null) && (args.length > 0) && (args[0].equals("-restart")))
       {
        System.out.println("we are restarted...");
        new File("D:\\my_docs\\ПРОГРАММИРОВАНИЕ\\Java\\standalone\\selfChanger\\selfChanger_JAVA\\classes\\MAIN.class").delete();
        System.out.println("deleted");
       }
      else
       {
        System.out.println("no args...");
        System.out.println("Restarting....");
        Runtime.getRuntime().exec("start java -classpath c:/1/commons-io-1.3.2.jar;c:/1/ MAIN -restart");
        System.exit(0);
       }
      
     }
    catch (IOException e) {System.out.println("IOEx: " + e.getMessage());}
   }
 }