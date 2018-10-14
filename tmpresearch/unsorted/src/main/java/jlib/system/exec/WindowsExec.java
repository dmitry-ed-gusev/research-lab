package jlib.system.exec;

import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Данный класс реализует запуск внешних приложений из программы JAVA. Класс выполнен для ОС WIndows XP. Перед
 * выполненнием внешней команды, проверяется версия ОС - если это не Windows XP, то выполнение не производится.
 * Данный класс выполняет действия, аналогичные действиям пакета EXEC (Jakarta commons).
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 25.02.2009)
*/

public class WindowsExec
 {
  /** Логгер данного класса. */
  private Logger logger = Logger.getLogger(WindowsExec.class.getName());

  /**
   * Внутренний класс для вычитки потока вывода - призапуске внешнего процесса необходимо вычитывать
   * два потока - поток вывода процесса (основной) и поток ошибок процесса. Вычитка потоков запускается в
   * основном классе в разных нитях.
  */
  private class StreamGobbler extends Thread
   {
    private InputStream   is;
    private StringBuilder output = null;

    StreamGobbler(InputStream is) {this.is = is;}

    @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
    public void run()
     {
      logger.debug("Gobbler runned. Reading stream.");
      BufferedReader br = null;
      try
       {
        br  = new BufferedReader(new InputStreamReader(is));
        // Вычитываем переданный данному классу поток и записываем в StringBuilder
        String line = br.readLine();
        if (line != null)
         {
          if (output == null) output = new StringBuilder();
          do {output.append(line).append("\n");} while ( (line = br.readLine()) != null);
         }
       }
      // Перехватываем ИС
      catch (IOException ioe) {logger.error(ioe.getMessage());}
      // Пытаемся в любом случае закрыть поток чтения
      finally
       {
        try {if (br != null) {br.close();}}
        catch (IOException e) {logger.error("Can't close BufferedReader stream! Reason: " + e.getMessage());}
       }
     }

    // Возвращает вывод, вычитанный из потока
    public String getOutput()
     {
      String result;
      if (this.output == null) {result = null;} else {result = output.toString();}
      return result;
     }
   }

  /**
   * Проверка корректности версии ОС для запуска внешних команд. В данной версии модуля поддерживается
   * только ОС Windows XP.
   * @return boolean подходит или нет данная версия ОС для запуска внешних программ.
  */
  public boolean isOSVersionCorrect()
   {
    boolean result = false;
    String osName = System.getProperty("os.name" );
    if ((osName != null) && (osName.equals(WindowsExecConsts.OS_TYPE_XP))) result = true;
    return result;
   }

  public WindowsExecResult execute(String command)
   {
    // todo: протестить на Висте!

    logger.debug("WORKING execute(). Executing [" + command + "].");
    WindowsExecResult result = null;
    // Пустую команду или null-команду не запускаем!
    if ((command != null) && (!command.trim().equals("")))
     {
      result = new WindowsExecResult();
      // Проверка версии ОС - данная версия модуля должна работать только в WinXP
      if (this.isOSVersionCorrect())
       try
        {
         String[] cmd = new String[3];
         cmd[0] = WindowsExecConsts.OS_XP_COMMAND_SHELL[0];
         cmd[1] = WindowsExecConsts.OS_XP_COMMAND_SHELL[1];
         cmd[2] = command;
         // Непосредственно запуск указанной команды
         Process proc = new ProcessBuilder(cmd).start();

         // Вычитка потока вывода ошибок нового процесса
         StreamGobbler errorGobbler  = new StreamGobbler(proc.getErrorStream());
         // Вычитка потока вывода нового процесса (сообщения, выданные новым процессом)
         StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream());
         // Выполняем вычитку потоков в новых нитях
         errorGobbler.start();
         outputGobbler.start();
         // Код возврата - получаем ПОСЛЕ завершения работы внешнего процесса
         result.setExitCode(proc.waitFor());
         // Вывод потока ошибок
         result.setError(errorGobbler.getOutput());
         // Вывод потока
         result.setOutput(outputGobbler.getOutput());
        }
       catch (IOException e)          {logger.error(e.getMessage());}
       catch (InterruptedException e) {logger.error(e.getMessage());}
      // Если версия ОС нам не подошла - ошибка в лог
      else logger.warn("Wrong OS version! Can't run external command.");
     }
    else logger.error("Empty command! Nothing to run!");
    return result;
   }

  /**
   * Метод для тестирования данного класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String args[])
   {
    InitLogger.initLogger(WindowsExec.class.getName());
    Logger logger = Logger.getLogger(WindowsExec.class.getName());
    
    WindowsExec exec = new WindowsExec();
    WindowsExecResult result = exec.execute("net stop mysql");
    if (result != null)
     {
      logger.info("ERROR: \n" + result.getError());
      logger.info("OUTPUT: \n" + result.getOutput());
      logger.info("EXIT CODE: " + result.getExitCode());
     }
    else logger.warn("NULL execute result!");
   }

 }