package dg.bigdata.hw3;

import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * MapReduce application for processing web access logs.
 * Created by gusevdm on 6/19/2017.
 */

public class ProcessAccessLogsMain extends Configured implements Tool {

    private static final Log LOG = LogFactory.getLog(ProcessAccessLogsMain.class);

    private static final String STR = "ip1 - - [24/Apr/2011:04:06:01 -0400] \"GET /~strabal/grease/photo9/927-3.jpg HTTP/1.1\" 200 40028 \"-\" \"Mozilla/5.0 (compatible; YandexImages/3.0; +http://yandex.com/bots)\"";

    /***/
    private static void readLogs() {
        //UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        UserAgent userAgent = UserAgent.parseUserAgentString(STR);
        System.out.println(userAgent);
    }

    @Override
    public int run(String[] args) throws Exception {

        // common configuration
        Configuration conf = this.getConf();
        //FileSystem fs = FileSystem.get(conf);

        // get instance of #1st MapReduce job
        Job job1 = Job.getInstance(conf, "Job #1");
        // set the Jar by finding where a current class came from
        job1.setJarByClass(ProcessAccessLogsMain.class);
        // set the job name
        //job1.setJobName("The longest word: option2.JOB#1");

        // input text file for job1- first cmd line parameter
        TextInputFormat.addInputPath(job1, new Path(args[0]));

        // path (directory) for output (plain text)
        TextOutputFormat.setOutputPath(job1, new Path(args[1]));

        job1.setMapperClass(AccessLogsMapper.class); // job mapper class
        //job1.setReducerClass(EntryReducer.class); // reducer class

        job1.setOutputKeyClass(LongWritable.class); // mapper output key class
        job1.setOutputValueClass(Text.class); // mapper class output value class

        // input/output format
        //job1.setInputFormatClass(TextInputFormat.class);
        //job1.setOutputFormatClass(TextOutputFormat.class);
        return job1.waitForCompletion(true) ? 0 : 1;

        /*
        // get instance of #2nd MapReduce job
        Job job2 = Job.getInstance(conf, "Job #2");
        job2.setJarByClass(LenCounterMainOption2.class);
        //job2.setJobName("The longest word: option2.JOB#2");
        TextInputFormat.addInputPath(job2, new Path(INTERMEDIATE_OUTPUT_PATH));
        TextOutputFormat.setOutputPath(job2, new Path(TEMP_OUTPUT_PATH));
        // mapper and reducer classes
        job2.setMapperClass(CounterMapper.class);
        job2.setReducerClass(CounterReducer.class);
        // output key-value part
        job2.setOutputKeyClass(IntWritable.class);
        job2.setOutputValueClass(IntWritable.class);
        // input/output format
        //job2.setInputFormatClass(TextInputFormat.class);
        //job2.setOutputFormatClass(TextOutputFormat.class);
        job2.waitForCompletion(true);

        // get counter from job #2 and put it into configuration
        conf.set(COUNTER_NAME, String.valueOf(job2.getCounters().findCounter(COUNTER_GROUP, COUNTER_NAME).getValue()));

        // get instance of #3rd MapReduce job
        Job job3 = Job.getInstance(conf, "Job #3");
        job3.setJarByClass(LenCounterMainOption2.class);
        TextInputFormat.addInputPath(job3, new Path(INTERMEDIATE_OUTPUT_PATH));
        TextOutputFormat.setOutputPath(job3, new Path(args[1]));
        job3.setMapperClass(FinalMapper.class);
        job3.setReducerClass(FinalReducer.class);
        job3.setOutputKeyClass(IntWritable.class);
        job3.setOutputValueClass(Text.class);

        return job3.waitForCompletion(true) ? 0 : 1;
        */
    }

    /** Read the arguments from command line and run the Job till completion. */
    public static void main(String[] args) throws Exception {
        LOG.info("Starting HW3: access logs processing.");

        //CommonUtils.unZipIt("access_logs.zip");
        //ProcessAccessLogsMain.readLogs();

        //if (args.length < 2) { // fail-fast
        //    throw new IllegalStateException("Expected at least 2 cmd line parameters!");
        //}

        ToolRunner.run(new Configuration(), new ProcessAccessLogsMain(), args);

    }

}
