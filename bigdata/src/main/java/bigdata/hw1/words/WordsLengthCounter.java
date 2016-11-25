package bigdata.hw1.words;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * MapReduce application to find the longest word.
 * Created by gusevdm on 11/11/2016.
 */

public class WordsLengthCounter {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Job job = Job.getInstance();
        job.setJarByClass(WordsLengthCounter.class);
        job.setJobName("The Longest word");

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(WordsMapper.class);
        job.setReducerClass(WordsReducer.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(MapWritable.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

}
