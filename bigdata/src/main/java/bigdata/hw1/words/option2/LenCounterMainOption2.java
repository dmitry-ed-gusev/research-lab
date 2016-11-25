package bigdata.hw1.words.option2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * MapReduce application to find the longest word.
 * Option2: scalable solution with two MapReduce jobs (chained).
 * Created by gusevdm on 11/25/2016.
 */

// http://unmeshasreeveni.blogspot.com/2014/04/chaining-jobs-in-hadoop-mapreduce.html

public class LenCounterMainOption2 extends Configured implements Tool {

    private static final String OUTPUT_PATH = "intermediate_output";

    @Override
    public int run(String[] args) throws Exception {

        // common configuration
        Configuration conf = getConf();
        FileSystem fs = FileSystem.get(conf);

        // #1st MapReduce job (instance/mapper/reducer/key/value/etc.)
        Job job = Job.getInstance(conf, "Job #1");
        job.setJarByClass(LenCounterMainOption2.class);
        // mapper+reducer
        //job.setMapperClass(MyMapper1.class);
        //job.setReducerClass(MyReducer1.class);
        // output key-value pair
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // input/output format
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        TextInputFormat.addInputPath(job, new Path(args[0]));
        TextOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));

        job.waitForCompletion(true);

        // #2nd MapReduce job (instance/mapper/reducer/key/value/etc.)
        Job job2 = Job.getInstance(conf, "Job 2");
        job2.setJarByClass(LenCounterMainOption2.class);
        // mapper+reducer
        //job2.setMapperClass(MyMapper2.class);
        //job2.setReducerClass(MyReducer2.class);
        // output key-value part
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        // input/output format
        job2.setInputFormatClass(TextInputFormat.class);
        job2.setOutputFormatClass(TextOutputFormat.class);

        TextInputFormat.addInputPath(job2, new Path(OUTPUT_PATH));
        TextOutputFormat.setOutputPath(job2, new Path(args[1]));

        return job2.waitForCompletion(true) ? 0 : 1;
    }

    /**
     * Method Name: main Return type: none Purpose:Read the arguments from
     * command line and run the Job till completion
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        if (args.length != 2) {
            System.err.println("Enter valid number of arguments <Inputdirectory>  <Outputlocation>");
            System.exit(0);
        }
        ToolRunner.run(new Configuration(), new LenCounterMainOption2(), args);
    }

}
