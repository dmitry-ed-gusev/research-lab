package dg.bigdata.hw3;

import gusev.dmitry.utils.CsvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.List;

/**
 * Mapper class for HW#3 (processing access logs).
 * Created by gusevdm on 6/19/2017.
 */
public class AccessLogsMapper extends Mapper<LongWritable, Text, LongWritable, Text>{

    //private static final
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        List<String> parsedLine;
        String ip;
        String userAgentString;
        long bytes;
        if (!StringUtils.isEmpty(value.toString())) {

            // parse input line
            parsedLine = CsvUtils.parseLine(value.toString());
            ip = parsedLine.get(0); // ip -> first token
            userAgentString = parsedLine.get(parsedLine.size() - 1); // agent - the last token
            try {
                bytes = Long.parseLong(parsedLine.get(parsedLine.size() - 3)); // bytes -> 3rd token from tail (end)
            } catch (NumberFormatException e) {

            }
            //System.out.println(key + " -> " + value.toString());
            //context.write(new IntWritable(pair.getLeft()),  array);
        }

    }
}
