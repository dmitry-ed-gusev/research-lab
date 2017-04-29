package apache.storm;

import apache.storm.storm.CdrSpout;
import apache.storm.storm.PrintOutBolt;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BasicApp
{
    public static void main( String[] args ) throws Throwable
    {
        Logger.getRootLogger().setLevel(Level.ERROR);

        // Storm builder for topology
        TopologyBuilder builder = new TopologyBuilder();
        // Storm SPOUT (data source)
        builder.setSpout("CdrReader", new CdrSpout());
        // Storm BOLT (data processing node)
        builder.setBolt("PrintOutBolt", new PrintOutBolt(), 2).shuffleGrouping("CdrReader");

        // Storm config
        Config config = new Config();
        config.setDebug(false);
        //config.setNumWorkers(32);

        // Storm cluster with our simple topology
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("T1", config, builder.createTopology());
        Thread.sleep(1000*10);

        // shutdown Storm cluster
        cluster.shutdown();

    }

}
