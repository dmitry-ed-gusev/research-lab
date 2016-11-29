package bigdata.hw1.words.option2;

import bigdata.hw1.words.TextArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Entry reducer class: join text arrays with words of same length, if possible -
 * ignore (not write to output) arrays with length, shorter than already processed.
 * Possible reduce count of data a little-bit.
 * Created by gusevdm on 11/28/2016.
 */

public class EntryReducer extends Reducer<IntWritable, TextArrayWritable, IntWritable, Text> {

    private int               maxValue = Integer.MIN_VALUE;       // max value
    private TextArrayWritable words    = new TextArrayWritable(); // empty words array

    @Override
    protected void reduce(IntWritable key, Iterable<TextArrayWritable> values, Context context) throws IOException, InterruptedException {

        if (key.get() > maxValue) { // new maximum found
            maxValue = key.get();
            words = new TextArrayWritable();         // reset words array
            for (TextArrayWritable array : values) { // join with received by current key
                words = words.join(array);
            }
        } else if (key.get() == maxValue) { // found same value, as maximum
            for (TextArrayWritable array : values) { // just join received arrays
                words = words.join(array);
            }
        }

        // write output
        if (maxValue > Integer.MIN_VALUE && words.get() != null && words.get().length > 0) {
            context.write(new IntWritable(maxValue), new Text(Arrays.toString(words.toStrings())));
        }

    }

}
