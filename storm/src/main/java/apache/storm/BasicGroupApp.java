package apache.storm;

import apache.storm.storm.CdrGrouper;
import apache.storm.storm.CdrSpout;
import apache.storm.storm.PrintOutBolt;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BasicGroupApp
{
    public static void main( String[] args ) throws Throwable
    {
        Logger.getRootLogger().setLevel(Level.ERROR);
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("CdrReader", new CdrSpout());
        builder.setBolt("PrintOutBolt", new PrintOutBolt(), 2).
                customGrouping("CdrReader", new CdrGrouper());

        Config config = new Config();
        config.setDebug(false);
        //config.setNumWorkers(32);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("T1", config, builder.createTopology());
        Thread.sleep(1000*10);
        cluster.shutdown();

    }
}
