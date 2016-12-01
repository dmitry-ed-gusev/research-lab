package bigdata.hw1.words.option2.job2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * This reducer (singleton) receives all length and find the maximum length.
 * Created by gusevdm on 12/1/2016.
 */

public class CounterReducer extends Reducer<IntWritable, IntWritable, IntWritable, Text> {

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        int maxValue = Integer.MIN_VALUE;

        for (IntWritable value : values) {
            if (value.get() > maxValue) {
                maxValue = value.get();
            }
        } // end of FOR cycle

        // write output
        context.write(new IntWritable(maxValue), new Text());

    }

}
