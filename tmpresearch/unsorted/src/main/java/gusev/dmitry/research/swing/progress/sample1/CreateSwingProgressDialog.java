package gusev.dmitry.research.swing.progress.sample1;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 17.10.12)
 */
public class CreateSwingProgressDialog {
  /**
   * This method shows a progress dialog if the long running task takes more
   * than 500 milliseconds. It uses a Swing ProgressMonitor to achieve this.
   * Look at the documentaiton for the Progress Monitor to learn about its
   * more advanced features.
   *
   * @param parent - the parent component
   * @param worker - The task (needs to be a subclass of SwingWorker)
   * @param title  - The title for the dialog box.
   * @param startValue - the start value for the progress indicator
   * @param maxValue - the max value for the progress indicator
   */
  public void showProgressDialog(Component parent, final SwingWorker worker,
                                 String title, int startValue, int maxValue) {

    /* This is final because we are making use of it in the anonymous
       inner class  created by the next statement in this method. */
    final ProgressMonitor monitor = new ProgressMonitor(parent, title, "",
                   startValue, maxValue);

    /* We add a property change listener to our worker which will monitor
     * the progress of the worker. This also allows us to check if the user
     * has cancelled the task and close the dialog and stop the task.
     */
    worker.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {

        int progress = worker.getProgress();
        String message = String.format("Completed %d%%.\n", progress);
        monitor.setNote(message);
        monitor.setProgress(progress);

        if (monitor.isCanceled() || worker.isDone()) {
          if (monitor.isCanceled()) {
            worker.cancel(true);
          }
          monitor.close();
        }
      }
    });
  }

  /**
   * This rather contrived example demonstrates the calling code for the
   * showProgressDialog method which will allow the user to track progress
   * while blocking access to the rest of the user interface.
   */
  public void createSwingProgressDialog() {
    /* This class performs a long running task */
    class MyTask extends SwingWorker {
      @Override
      protected String doInBackground() throws Exception {
        int i = 0;
        while (i < 100 && !isCancelled() && !isDone()) {
          try {
            setProgress(i);
            Toolkit.getDefaultToolkit().beep();
            Thread.sleep(1000);
            i += 5;
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        setProgress(100);
        return "Done";
      }
    }

    /* Call the dialog code */
    MyTask task = new MyTask();
    showProgressDialog(null, task, "Executing long running task", 0, 100);
    task.execute();
  }

  public static void main(String[] args) {
    new CreateSwingProgressDialog().createSwingProgressDialog();
  }
}