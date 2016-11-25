package gusev.dmitry.research.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 04.01.14)
 */
public class QuartzSchedulerExample {

    public static void main(String[] args) {
        Log log = LogFactory.getLog(QuartzSchedulerExample.class);
        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            // turn off scheduler
            scheduler.shutdown();

        } catch (SchedulerException se) {
            log.error(se);
        }
    }

}