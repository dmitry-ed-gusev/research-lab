package bigdata.hw1.words.option2.job3;

import bigdata.hw1.words.option2.job2.CounterMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static bigdata.hw1.words.option2.LenCounterDefaults.COUNTER_NAME;

/**
 * Final Mapper: read all output from first mapper (EntryMapper) and send to single reducer
 * only strings with maximum length.
 * Created by gusevdm on 12/2/2016.
 */

public class FinalMapper extends Mapper<LongWritable, Text, IntWritable, Text> {

    int maxValue;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

        try { // get counter value from previous job (from configuration)
            maxValue = Integer.parseInt(context.getConfiguration().get(COUNTER_NAME));
        } catch (NumberFormatException e) {
            LoggerFactory.getLogger(FinalMapper.class).error("Cant parse number!", e);
        }

    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        if (!StringUtils.isEmpty(value.toString())) {

            String[] splitted = StringUtils.split(value.toString(), '[');
            if (splitted.length > 1) { // splitted array has at least two elements
                try {
                    int length = Integer.parseInt(StringUtils.trimToEmpty(splitted[0])); // get length of words in current line

                    if (length == maxValue) { // if this length equals to maximum - go ahead
                        String strArray = splitted[1].replaceAll("]", ""); // remove right brace
                        context.write(new IntWritable(maxValue), new Text(strArray));
                    }

                    //context.write(new IntWritable(length), new Text(splitted[1].replaceAll("]", "")));

                } catch (NumberFormatException e) {
                    LoggerFactory.getLogger(CounterMapper.class).error("Cant parse number!", e);
                }
            }
        } // end main if clause

    }

}
