package dg.bigdata.hw1.words.option2.job2;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This mapper rerads output of previous MapReduce job (line by line) and produce
 * pairs of two numbers - key (the same for all, for one reducer) and length.
 * Reducer, that will follow this mapper will just count the maximum.
 * Created by gusevdm on 12/1/2016.
 */

public class CounterMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

    private static final int REDUCER_KEY = 1;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        if (!StringUtils.isEmpty(value.toString())) {
            String[] splitted = StringUtils.split(value.toString());
            if (splitted.length > 0) {
                try {
                    int length = Integer.parseInt(splitted[0]);
                    context.write(new IntWritable(REDUCER_KEY), new IntWritable(length));
                } catch (NumberFormatException e) {
                    LoggerFactory.getLogger(CounterMapper.class).error("Cant parse number!", e);
                }
            }
        }

    }

}
