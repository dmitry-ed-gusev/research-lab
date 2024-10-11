package gusev.dmitry.research.io.fileman;

import gusev.dmitry.research.io.fileman.modules.FSCopier;
import gusev.dmitry.research.io.fileman.modules.FSScanner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

/**
 * Main application for jFileman utility.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 17.01.14)
*/

public class FSManager implements Observer {

    private final Log log = LogFactory.getLog(FSManager.class);
    private static final int MAX_THREADS = 5;

    private int     threadsCount             = MAX_THREADS;
    private boolean isMultiThreadedOperation = false;

    public synchronized boolean areThereFreeThreads() throws InterruptedException {
        if (this.threadsCount <= 0) {
            wait();
        }
        return threadsCount > 0;
    }

    public synchronized void decrementThreadsCount() {
        this.threadsCount--;
    }

    public synchronized void incrementThreadsCount() {
        this.threadsCount++;
        notifyAll();
    }

    //public synchronized void resetThreadsCount() {
    //    this.threadsCount = MAX_THREADS;
    //    notifyAll();
    //}

    @Override
    public void update(Observable o, Object arg) {
        //log.debug("FSManager.update() working.");
        //log.info(String.format("Percents completed [%3s%%].", arg));
        if (o != null) {
            if (o instanceof FSCopier) { // observable is FSCopier
                System.out.print(String.format("Percents completed [%3s%%].", arg));
                System.out.print("\r");
            } else if(o instanceof FSScanner) { // observable is FSScanner
                log.debug("FSScanner");
            } else { // observable is unknown
                log.warn("Observable object is unknown!");
            }
        } else {
            log.error("Observable object is NULL!");
        }
    }

    /** Copy files.*/
    public void copy(final String source, final String destination) throws IOException, InterruptedException {
        log.debug("FSManager.copy() working.");

        // files copier object
        final FSCopier copier = new FSCopier(source, destination);
        copier.addObserver(this);

        final CountDownLatch latch = new CountDownLatch(1); // latch for waiting thread to complete
        // thread for copying files
        Thread copyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    copier.copy(true);
                } catch (IOException e) {
                    log.error(String.format("Copy [$1%s] to [$2%s] failed!", source, destination), e);
                }
                latch.countDown(); // countdown awaiting latch
            }
        });

        copyThread.start(); // start copying thread
        latch.await();      // Wait for countdown
        //copyThread.join();
        log.debug("Copy finished.");
    }

    /** Copy files in multiple threads. */
    // todo: method has some errors -> in the end of copying more than one thread copies one folder
    public void copyMultiThreads(final String source, final String destination) throws IOException, InterruptedException {
        log.debug("FSManager.copyMultiThreads() working.");

        if (new File(source).isDirectory()) { // multiple threads just if source is directory
            log.debug("Source [" + source + "] is directory - processing.");
            //int directoriesCount;
            //int threadsCount = MAX_THREADS;
            //final CountDownLatch latch = new CountDownLatch(MAX_THREADS); // latch for waiting thread to complete

            for (String path : new File(source).list()) {
                File file = new File(new File(source).getPath(), path);

                if (file.isDirectory()) { // process only directories
                    if (this.areThereFreeThreads()) { // there are free threads
                        this.decrementThreadsCount();
                        final FSCopier copier = new FSCopier(source + "/" + path, destination);
                        //copier.addObserver(this);
                        // thread for copying files
                        Thread copyThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                log.debug("Thread started.");
                                try {
                                    copier.copy(true);
                                } catch (IOException e) {
                                    log.error(String.format("Copy [$1%s] to [$2%s] failed!", source, destination), e);
                                }
                                //latch.countDown(); // countdown awaiting latch
                                FSManager.this.incrementThreadsCount();
                                log.debug("Thread finished.");
                            }
                        });
                        // start copying
                        copyThread.start();
                    }
                } // end if(is directory)

            } // end of FOR cycle

        } else { // copy in one thread
            throw new UnsupportedOperationException("UNSUPPORTED!");
        }

        // files copier object
        final FSCopier copier = new FSCopier(source, destination);
        copier.addObserver(this);

        final CountDownLatch latch = new CountDownLatch(1); // latch for waiting thread to complete
        // thread for copying files
        Thread copyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    copier.copy(true);
                } catch (IOException e) {
                    log.error(String.format("Copy [$1%s] to [$2%s] failed!", source, destination), e);
                }
                latch.countDown(); // countdown awaiting latch
            }
        });

        copyThread.start(); // start copying thread
        latch.await();      // Wait for countdown
        //copyThread.join();
        log.debug("Copy finished.");
    }

    /***/
    public void move() {
        log.debug("FSManager.move() working.");
    }

    /***/
    public void copyAndZip() {
        log.debug("FSManager.copyAndZip() working.");
    }

    /***/
    public static void main(String[] args) {
        Log log = LogFactory.getLog(FSManager.class);
        log.info("FSManager starting.");

        try {
            FSManager fmanager = new FSManager();
            //fmanager.copy("//pst-fs/asu/users/new/asu", "c:/temp/backup1");
            fmanager.copyMultiThreads("//pst-fs/asu/USERS/new/bux", "c:/temp/backup");

        } catch (InterruptedException | IOException e) {
            log.error("Copy failed!", e);
        }

    }

}