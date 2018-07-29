package jlib.system.exec;

/**
 * Результат запуска внешней команды из JAVA.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 25.02.2009)
*/

public class WindowsExecResult
 {
  private String output;
  private String error;
  private int    exitCode;

  public WindowsExecResult()
   {
    this.output   = null;
    this.error    = null;
    this.exitCode = 0;
   }

  public String getOutput() {
   return output;
  }

  public void setOutput(String output) {
   this.output = output;
  }

  public String getError() {
   return error;
  }

  public void setError(String error) {
   this.error = error;
  }

  public int getExitCode() {
   return exitCode;
  }

  public void setExitCode(int exitCode) {
   this.exitCode = exitCode;
  }
  
 }