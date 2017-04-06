package bigdata.hw1.words.option2.job3;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Final Reducer: just sum up all arrays with the longest words.
 * Single instance.
 * Created by gusevdm on 12/2/2016.
 */

public class FinalReducer extends Reducer<IntWritable, Text, IntWritable, Text> {

    @Override
    protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        StringBuilder words = new StringBuilder();

        for (Text value : values) { // iterate over values and add them to result
            words.append(value.toString()).append(" ");
            context.write(new IntWritable(key.get()), new Text(words.toString()));
        }

    }

}
