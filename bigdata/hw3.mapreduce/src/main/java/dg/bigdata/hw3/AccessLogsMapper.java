package dg.bigdata.hw3;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Mapper class for HW#3 (processing access logs).
 * Created by gusevdm on 6/19/2017.
 */
public class AccessLogsMapper extends Mapper<LongWritable, Text, LongWritable, Text>{

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        if (!StringUtils.isEmpty(value.toString())) {
            System.out.println(key + " -> " + value.toString());
            //context.write(new IntWritable(pair.getLeft()),  array);
        }

    }
}
