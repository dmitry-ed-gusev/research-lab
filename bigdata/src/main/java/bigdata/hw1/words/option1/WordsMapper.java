package bigdata.hw1.words.option1;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for words lengths counting task.
 * Created by gusevdm on 11/11/2016.
 */

public class WordsMapper extends Mapper<LongWritable, Text, IntWritable, MapWritable> {

    private static final int MAPPER_KEY = 1;
    private static final String PUNCTUATION_REGEX = "[.,:;!?%$#@*+\\-<>'\"]";

    /***/
    protected static MapWritable parseRow(String row) {

        // processing input row
        int maxLength = Integer.MIN_VALUE;
        List<Text> words = new ArrayList<>();

        if (!StringUtils.isEmpty(row)) { // process if not empty
            String tmpWord;
            for (String word : StringUtils.split(row)) {
                tmpWord = word.replaceAll(PUNCTUATION_REGEX, ""); // remove all punctuation

                if (tmpWord.length() > maxLength) { // <- new max length value
                    maxLength = tmpWord.length(); // get new length value
                    words = new ArrayList<>();    // reset words with max length list
                    words.add(new Text(tmpWord)); // add new longest word to list
                } else if (tmpWord.length() == maxLength) { // <- word with same length
                    words.add(new Text(tmpWord));
                }
            } // end of FOR
        }

        TextArrayWritable array = new TextArrayWritable();
        array.set(words.toArray(new Writable[words.size()]));

        MapWritable map   = new MapWritable();
        map.put(new IntWritable(maxLength), array);

        return map;
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        // parse received input as array
        if (!StringUtils.isEmpty(value.toString())) {
            // put all results with one key
            context.write(new IntWritable(MAPPER_KEY), WordsMapper.parseRow(value.toString()));
        }

    }

}
