package apache.storm.storm;

import apache.storm.data.Cdr;
import apache.storm.data.Clients;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class ClientIdBolt extends BaseBasicBolt {
    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        Cdr cdr = (Cdr) tuple.getValue(0);
        cdr.setClientId(Clients.getClientId(cdr.getCallSource()));
        basicOutputCollector.emit(new Values(cdr));
        System.out.println("CID>> [" + Thread.currentThread().getId() + "]" + cdr);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("cdr"));
    }
}
