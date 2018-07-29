package jlib.system.exec;

import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ������ ����� ��������� ������ ������� ���������� �� ��������� JAVA. ����� �������� ��� �� WIndows XP. �����
 * ������������ ������� �������, ����������� ������ �� - ���� ��� �� Windows XP, �� ���������� �� ������������.
 * ������ ����� ��������� ��������, ����������� ��������� ������ EXEC (Jakarta commons).
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 25.02.2009)
*/

public class WindowsExec
 {
  /** ������ ������� ������. */
  private Logger logger = Logger.getLogger(WindowsExec.class.getName());

  /**
   * ���������� ����� ��� ������� ������ ������ - ���������� �������� �������� ���������� ����������
   * ��� ������ - ����� ������ �������� (��������) � ����� ������ ��������. ������� ������� ����������� �
   * �������� ������ � ������ �����.
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
        // ���������� ���������� ������� ������ ����� � ���������� � StringBuilder
        String line = br.readLine();
        if (line != null)
         {
          if (output == null) output = new StringBuilder();
          do {output.append(line).append("\n");} while ( (line = br.readLine()) != null);
         }
       }
      // ������������� ��
      catch (IOException ioe) {logger.error(ioe.getMessage());}
      // �������� � ����� ������ ������� ����� ������
      finally
       {
        try {if (br != null) {br.close();}}
        catch (IOException e) {logger.error("Can't close BufferedReader stream! Reason: " + e.getMessage());}
       }
     }

    // ���������� �����, ���������� �� ������
    public String getOutput()
     {
      String result;
      if (this.output == null) {result = null;} else {result = output.toString();}
      return result;
     }
   }

  /**
   * �������� ������������ ������ �� ��� ������� ������� ������. � ������ ������ ������ ��������������
   * ������ �� Windows XP.
   * @return boolean �������� ��� ��� ������ ������ �� ��� ������� ������� ��������.
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
    // todo: ���������� �� �����!

    logger.debug("WORKING execute(). Executing [" + command + "].");
    WindowsExecResult result = null;
    // ������ ������� ��� null-������� �� ���������!
    if ((command != null) && (!command.trim().equals("")))
     {
      result = new WindowsExecResult();
      // �������� ������ �� - ������ ������ ������ ������ �������� ������ � WinXP
      if (this.isOSVersionCorrect())
       try
        {
         String[] cmd = new String[3];
         cmd[0] = WindowsExecConsts.OS_XP_COMMAND_SHELL[0];
         cmd[1] = WindowsExecConsts.OS_XP_COMMAND_SHELL[1];
         cmd[2] = command;
         // ��������������� ������ ��������� �������
         Process proc = new ProcessBuilder(cmd).start();

         // ������� ������ ������ ������ ������ ��������
         StreamGobbler errorGobbler  = new StreamGobbler(proc.getErrorStream());
         // ������� ������ ������ ������ �������� (���������, �������� ����� ���������)
         StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream());
         // ��������� ������� ������� � ����� �����
         errorGobbler.start();
         outputGobbler.start();
         // ��� �������� - �������� ����� ���������� ������ �������� ��������
         result.setExitCode(proc.waitFor());
         // ����� ������ ������
         result.setError(errorGobbler.getOutput());
         // ����� ������
         result.setOutput(outputGobbler.getOutput());
        }
       catch (IOException e)          {logger.error(e.getMessage());}
       catch (InterruptedException e) {logger.error(e.getMessage());}
      // ���� ������ �� ��� �� ������� - ������ � ���
      else logger.warn("Wrong OS version! Can't run external command.");
     }
    else logger.error("Empty command! Nothing to run!");
    return result;
   }

  /**
   * ����� ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
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