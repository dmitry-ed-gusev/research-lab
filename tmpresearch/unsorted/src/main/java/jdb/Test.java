package jdb;

import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ������ ����� ������������ ��� ������ � ��������� �������������.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 04.03.2009)
*/

public class Test
 {
  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[] {"jdb"});
    Logger logger = Logger.getLogger("jdb");

    Timestamp timestamp = new Timestamp(new Date().getTime());
    logger.info(timestamp);

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    logger.info(sdf.format(timestamp));


    // ������ ����� �� �������
    //Scanner scanner = new Scanner(System.in);
    //int number = scanner.nextInt();
    //logger.info("-> " + number);

   }
    
 }