package gusev.dmitry.research.books.java24h_trainer.lesson21.threads.newsReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 30.10.12)
 */
public class ReaderSwingWorker extends SwingWorker<Void, Void> {

    private Log log = LogFactory.getLog(ReaderSwingWorker.class);

    private String     fileName;
    private JTextArea  textArea;

    public ReaderSwingWorker(String fileName, JTextArea textArea) {
        this.fileName = fileName;
        this.textArea = textArea;
    }

    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
    @Override
    protected Void doInBackground() throws Exception {
        // simple debug log and check - are we in event dispatch thread or not?
        log.debug("ReaderSwingWorker.doInBackground() working. Event dispatch thread: " + SwingUtilities.isEventDispatchThread() +
                ". File [" + this.fileName + "].");

        // clear text area
        this.textArea.setText(null);

        FileReader freader = null;
        try {
            freader = new FileReader(this.fileName);
            BufferedReader reader = new BufferedReader(freader);
            String line;
            int linesCounter = 0;
            StringBuilder builder = new StringBuilder();

            // read file
            while((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
                linesCounter++;
                if (linesCounter%1000 == 0) {
                    //log.debug("Last line: " + linesCounter);
                    //log.debug("text area text size: " + this.textArea.getText().length());
                    this.textArea.append(builder.toString());
                    builder = new StringBuilder();

                    /*try {
                        yield();
                        //sleep(200);
                        //wait(200);
                    } catch (InterruptedException e) {
                        log.error(e);
                    }
                    */
                }
            }
            // tail of file
            if (linesCounter%200 != 0) {
                log.debug("Last line: " + linesCounter);
                log.debug("text area text size: " + this.textArea.getText().length());
                this.textArea.append(builder.toString());
            }

            log.info("ReaderSwingWorker has just finished reading from [" + this.fileName + "] file.");
        } catch (IOException e) {
            log.error(e);
        } /*catch (InterruptedException e) {
            log.error("Thread [" + this.fileName + "] interrupted!", e);
        }*/ finally {
            if (freader != null) {
                try {
                    freader.close();
                } catch (IOException e) {
                    log.error("Can't close file [" + this.fileName + "]! Reason: " + e.getMessage());
                }
            }
        }

        log.debug("Setting up position: " + this.textArea.getText().length());
        // caret position -> end of text
        this.textArea.setCaretPosition(this.textArea.getText().length());
        log.debug("ReaderSwingWorker.doInBackground() finished!");
        return null;
    }

    @Override
    protected void done() {
        log.debug("ReaderSwingWorker.done() working. Event dispatch thread: " + SwingUtilities.isEventDispatchThread());
        //super.done();
    }

}