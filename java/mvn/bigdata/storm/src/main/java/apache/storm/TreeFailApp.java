package apache.storm;

import apache.storm.faults.FailAwareSpout;
import apache.storm.faults.FailingBolt;
import apache.storm.faults.MultiplierBolt;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TreeFailApp
{
    public static void main( String[] args ) throws Throwable
    {
        Logger.getRootLogger().setLevel(Level.ERROR);
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("FailAwareSpout", new FailAwareSpout());
        builder.setBolt("MultiplierBolt", new MultiplierBolt(), 2).shuffleGrouping("FailAwareSpout");
        builder.setBolt("FailingBolt", new FailingBolt(), 2).shuffleGrouping("MultiplierBolt");

        Config config = new Config();
        config.setDebug(false);
        //config.setNumWorkers(32);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("T1", config, builder.createTopology());
        Thread.sleep(1000*10);
        cluster.shutdown();

    }
}
