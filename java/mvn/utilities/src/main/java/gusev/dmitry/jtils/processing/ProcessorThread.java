package gusev.dmitry.jtils.processing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class implementation of Runnable interface for start data processor in separate thread (concurrent
 * tasks execution).
 * @author Gusev Dmitry (gusevd)
 * @version 2.0 (DATE: 05.10.13)
 */

//@Repository
public class ProcessorThread implements Runnable {

    // Minimum repeat time for thread (in seconds). If repeat time set to value below this,
    // thread will not proceed with repeat cycle, it just finishes with error message.
    private static final int MIN_REPEAT_TIME = 30;

    //@Autowired (required = false)                  // injecting this value is optional
    private ProcessorInterface processorInterface; // link to ProcessorInterface implementation
    //@Value("${repeat.time.seconds:0}")             // this value should exist in context or default value (=0) will be used
    private int                repeatTime;         // miner repeat time (in seconds)

    private volatile boolean   isShutdown = false; // control flag for shutdown thread
    private volatile boolean   isWorking  = false; // info flag for check - does miner work or not

    /** Default constructor. It needs for Spring context creation. */
    public ProcessorThread() {
    }

    /***/
    public ProcessorThread(ProcessorInterface processorInterface, int repeatTime) {
        this.processorInterface = processorInterface;
        this.repeatTime         = repeatTime;
    }

    /** Private method for internal use - its implementation detail. */
    private synchronized boolean isShutdown() throws InterruptedException {
        if (!isShutdown) {
            wait(this.repeatTime*1000);
        }
        return isShutdown;
    }

    /**
     * Method for shutdown data miner thread.
     * @param shutdown boolean shutdown flag: if its true, thread notification occurs and thread stops.
    */
    public synchronized void setShutdown(boolean shutdown) {
        isShutdown = shutdown;
        if (isShutdown) { // notify only if its a real shutdown
            notifyAll();
        }
    }

    /**
     * Method returns info: does data miner thread working or not.
     * @return boolean does data miner thread working or not.
    */
    public synchronized boolean isWorking() {
        return isWorking;
    }

    /** Private method for set info flag - implementation detail. */
    private synchronized void setWorking(boolean working) {
        isWorking = working;
    }

    /***/
    //public String getProcessorName() {
    //    return this.processorInterface.getProcessorName();
    //}

    @Override
    public void run() {
        Log log = LogFactory.getLog(ProcessorThread.class);

        // check minimum repeat time
        if (this.repeatTime >= MIN_REPEAT_TIME) { // repeat time is ok - starting miner thread
            //log.debug("MINER THREAD -> Miner [" + this.processorInterface.getProcessorName() + "]. Repeat time: " + this.repeatTime + ". Starting.");

            // starting thread main cycle
            try {
                do {
                    log.debug("MINER THREAD -> inside while cycle.");
                    this.setWorking(true);
                    // miner calling
                    processorInterface.process();
                    this.setWorking(false);
                } while (!this.isShutdown());

            } catch (InterruptedException e) {
                log.error("Data miner thread error!", e);
            }

            //log.info("Thread for miner [" + this.processorInterface.getProcessorName() + "] shutdown.");
        } else { // repeat time is less than minimum - don't start miner thread
            //log.warn("Miner [" + this.processorInterface.getProcessorName() + "]. Repeat time: " + this.repeatTime + " second(s). " +
            //        "MIN repeat time: " + MIN_REPEAT_TIME + " second(s). Skip.");
        }
    }

}