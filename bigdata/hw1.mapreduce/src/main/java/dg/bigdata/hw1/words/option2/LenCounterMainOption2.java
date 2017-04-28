package dg.bigdata.hw1.words.option2;

import dg.bigdata.hw1.words.TextArrayWritable;
import dg.bigdata.hw1.words.option2.job1.EntryMapper;
import dg.bigdata.hw1.words.option2.job1.EntryReducer;
import dg.bigdata.hw1.words.option2.job2.CounterMapper;
import dg.bigdata.hw1.words.option2.job2.CounterReducer;
import dg.bigdata.hw1.words.option2.job3.FinalMapper;
import dg.bigdata.hw1.words.option2.job3.FinalReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static dg.bigdata.hw1.words.option2.LenCounterDefaults.COUNTER_GROUP;
import static dg.bigdata.hw1.words.option2.LenCounterDefaults.COUNTER_NAME;

/**
 * MapReduce application to find the longest word.
 * Option2: scalable solution with two MapReduce jobs (chained).
 * Created by gusevdm on 11/25/2016.
 */

// http://unmeshasreeveni.blogspot.com/2014/04/chaining-jobs-in-hadoop-mapreduce.html

public class LenCounterMainOption2 extends Configured implements Tool {

    private static final String INTERMEDIATE_OUTPUT_PATH = "intermediate_output";
    private static final String TEMP_OUTPUT_PATH         = "temp";

    @Override
    public int run(String[] args) throws Exception {

        // common configuration
        Configuration conf = getConf();
        //FileSystem fs = FileSystem.get(conf);

        // get instance of #1st MapReduce job
        Job job1 = Job.getInstance(conf, "Job #1");
        // set the Jar by finding where a current class came from
        job1.setJarByClass(LenCounterMainOption2.class);
        // set the job name
        //job1.setJobName("The longest word: option2.JOB#1");
        // input text file for job1- first cmd line parameter
        TextInputFormat.addInputPath(job1, new Path(args[0]));
        // path (directory) for output (plain text)
        TextOutputFormat.setOutputPath(job1, new Path(INTERMEDIATE_OUTPUT_PATH));
        // mapper class
        job1.setMapperClass(EntryMapper.class);
        // reducer class
        job1.setReducerClass(EntryReducer.class);
        // mapper class output key class
        job1.setOutputKeyClass(IntWritable.class);
        // mapper class output value class
        job1.setOutputValueClass(TextArrayWritable.class);
        // input/output format
        //job1.setInputFormatClass(TextInputFormat.class);
        //job1.setOutputFormatClass(TextOutputFormat.class);
        job1.waitForCompletion(true);

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
    }

    /**
     * Method Name: main Return type: none Purpose:Read the arguments from
     * command line and run the Job till completion
     */
    public static void main(String[] args) throws Exception {

        if (args.length < 2) { // fail-fast
            throw new IllegalStateException("Expected at least 2 cmd line parameters!");
        }

        ToolRunner.run(new Configuration(), new LenCounterMainOption2(), args);

    }

}
