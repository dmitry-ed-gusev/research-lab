package gusev.dmitry.research.books.java24h_trainer.lesson21.threads.newsReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 26.10.12)
 */

public class ReaderThread extends Thread {

    private Log log = LogFactory.getLog(ReaderThread.class);

    private String     fileName;
    private JTextArea textArea;

    public ReaderThread(String name, String fileName, JTextArea textArea) {
        super(name);
        // file name
        if (!new File(fileName).exists()) {
            throw new IllegalStateException("File [" + this.fileName + "] doesn't exist!");
        }
        this.fileName = fileName;
        // reference to text area
        if (textArea == null) {
            throw new IllegalStateException("Null reference to text area component!");
        }
        this.textArea = textArea;
        log.info("Thread [" + name + "] with file name [" + fileName + "] created OK.");
    }

    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
    @Override
    public void run() {
        // simple debug log and check - are we in event dispatch thread or not?
        log.debug("ReaderThread.run() working. Is event dispatch thread: " + SwingUtilities.isEventDispatchThread());

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
                //System.out.println(line);
                //this.textArea.append(line);
                //this.textArea.setCaretPosition(this.textArea.getText().length());
                //sleep(200);
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

            log.info("Thread [" + this.getName() + "] has just finished reading from [" + this.fileName + "] file.");
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

        // caret position at the end of text
        this.textArea.setCaretPosition(this.textArea.getText().length());
    }

}
