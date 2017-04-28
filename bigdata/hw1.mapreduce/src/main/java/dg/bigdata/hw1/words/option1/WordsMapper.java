package dg.bigdata.hw1.words.option1;

import dg.bigdata.hw1.words.ParseHelper;
import dg.bigdata.hw1.words.TextArrayWritable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.List;

/**
 * Mapper for words lengths counting task.
 * Created by gusevdm on 11/11/2016.
 */

public class WordsMapper extends Mapper<LongWritable, Text, IntWritable, MapWritable> {

    private static final int MAPPER_KEY = 1;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        if (!StringUtils.isEmpty(value.toString())) {
            // parse data
            Pair<Integer, List<Text>> pair = ParseHelper.parseDataRow(value.toString());

            // prepare mapper output
            TextArrayWritable array = new TextArrayWritable();
            array.set(pair.getRight().toArray(new Writable[0]));

            MapWritable map = new MapWritable();
            map.put(new IntWritable(pair.getLeft()), array);

            // put all results with one key
            context.write(new IntWritable(MAPPER_KEY), map);
        }

    }

}
