package bigdata.hw1.words.option2;

import bigdata.hw1.words.ParseHelper;
import bigdata.hw1.words.TextArrayWritable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.List;

/**
 * Entry mapper class: reads input data line by line and produce (for each line) the longest
 * words array and the length itself (pair of elements).
 * Created by gusevdm on 11/28/2016.
 */

public class EntryMapper extends Mapper<LongWritable, Text, IntWritable, TextArrayWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        if (!StringUtils.isEmpty(value.toString())) {
            // parse data
            Pair<Integer, List<Text>> pair = ParseHelper.parseDataRow(value.toString());
            // write context
            TextArrayWritable array = new TextArrayWritable();
            array.set(pair.getRight().toArray(new Text[0]));
            context.write(new IntWritable(pair.getLeft()),  array);
        }

    }

}
