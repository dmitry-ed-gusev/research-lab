package bigdata.hw1.words;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Reducer for words lengths counting task.
 * Created by gusevdm on 11/11/2016.
 */

public class WordsReducer extends Reducer<IntWritable, MapWritable, IntWritable, Text> {

    @Override
    protected void reduce(IntWritable key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {

        int maxValue = Integer.MIN_VALUE;

        // iterate over input values and calculate
        Map.Entry<Writable, Writable> entry;
        IntWritable                   length;
        TextArrayWritable             wordsArray = new TextArrayWritable();
        for (MapWritable map : values) {
            entry = map.entrySet().iterator().next();
            length     = (IntWritable) entry.getKey();

            if (length.get() > maxValue) { // we've found new max value
                maxValue = length.get();
                wordsArray = (TextArrayWritable) entry.getValue();
            } else if (length.get() == maxValue) { // we've the same value in defferent input - merge them
                wordsArray = wordsArray.join((TextArrayWritable) entry.getValue());
            }

        }

        // write output
        context.write(new IntWritable(maxValue), new Text(Arrays.toString(wordsArray.toStrings())));
    }

}
