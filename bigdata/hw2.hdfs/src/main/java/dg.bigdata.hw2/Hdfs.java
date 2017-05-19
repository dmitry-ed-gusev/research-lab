package dg.bigdata.hw2;

import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * HDFS utility.
 * Created by vinnypuhh on 30.04.17.
 */
public class Hdfs {

    public static void main(String[] args) throws IOException {
        System.out.println("HDFS test is starting...");

        InputStream in = null;
        try {
            in = new URL("hdfs://host/path").openStream();
// process in
        } finally {
            IOUtils.closeStream(in);
        }
    }

}
