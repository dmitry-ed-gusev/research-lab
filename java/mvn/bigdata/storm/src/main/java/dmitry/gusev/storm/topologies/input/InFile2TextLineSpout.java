package dmitry.gusev.storm.topologies.input;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import dmitry.gusev.storm.Helper;
import dmitry.gusev.storm.StormAppDefaults;
import dmitry.gusev.storm.config.StormAppProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

/**
 * Spout for input (producer) topology. Reads text file line by line and emits tuples with read lines.
 * This spout should always be single-instanced (one task, one executor) because it reads text file
 * sequentally - line by line.
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 07.04.2016)
 */
public class InFile2TextLineSpout extends BaseRichSpout {

    private static final Log log = LogFactory.getLog(InFile2TextLineSpout.class);

    private final StormAppProperties properties;
    private SpoutOutputCollector outputCollector;

    private AtomicBoolean isProcessingDone = new AtomicBoolean(false); // flag - whole file has been processed
    private final long    totalLines;                                  // total lines in input file
    private AtomicLong    linesCounter     = new AtomicLong(0L);       // processed lines (whole)
    private AtomicLong    ackedLines       = new AtomicLong(0L);       // count of acked lines
    private AtomicLong    failedLines      = new AtomicLong(0L);       // count of failed lines
    private LongAdder     totaller         = new LongAdder();          // aggregator of acked+failed lines

    private Scanner scanner;

    /***/
    public InFile2TextLineSpout(StormAppProperties properties) {
        this.properties = properties;
        this.totalLines = Helper.getLinesInFileCount(this.properties.getSourceFile());
        log.info(String.format("Input file [%s] contains [%s] line(s).", this.properties.getSourceFile(), this.totalLines));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        log.debug("InFile2TextLineSpout.declareOutputFields() working.");
        declarer.declare(new Fields(StormAppDefaults.STORM_TEXT_LINE_FIELD));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        log.debug("InFile2TextLineSpout.open() working.");
        this.outputCollector = collector;

        try {
            this.scanner = new Scanner(new File(this.properties.getSourceFile()));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e); // rethrow as runtime exception
        }
    }

    /** Implementation details - internal method for producing next text line from an input file. */
    private void produceNextLine() {
        if (this.isProcessingDone.get()) { // we already processed a whole file
            return;
        }

        String line = this.scanner.nextLine();
        // Emits tuple with ID to topology. As a tuple ID we use line number.
        this.outputCollector.emit(new Values(line), this.linesCounter.incrementAndGet());

        if (log.isTraceEnabled()) { // avoid performance/memory issues (and too much output)
            log.trace(String.format("line #%s -> [%s]", this.linesCounter.get(), line));
        }
        if (log.isDebugEnabled() && this.properties.getProcessedLinesCount() > 0 &&
                this.linesCounter.get() % this.properties.getProcessedLinesCount() == 0) { // only for debug - processing progress
            log.debug(String.format("Processed [%s] line(s).", this.linesCounter.get()));
        }

        // check - is processing done or not (end of file or debug boundary reached)
        if (this.linesCounter.get() >= this.totalLines ||
                (this.properties.getLinesCount() > 0 && this.linesCounter.get() >= this.properties.getLinesCount())) {
            this.isProcessingDone.getAndSet(true);
            this.scanner.close();
            log.info(String.format("Input file [%s] processing done. Processed [%s] lines.", this.properties.getSourceFile(), this.linesCounter.get()));
        }

    }

    @Override
    public void nextTuple() {
        //log.trace("InFile2TextLineSpout.nextTuple() working."); // <- too much output
        this.produceNextLine();
    }

    @Override
    public void ack(Object msgId) {
        log.trace("InFile2TextLineSpout.ack() working."); // <- too much output (in most cases)
        this.ackedLines.incrementAndGet(); // increment acked lines counter
        this.totaller.increment();

        if (log.isDebugEnabled() && this.properties.getProcessedLinesCount() > 0 &&
                this.ackedLines.get() % this.properties.getProcessedLinesCount() == 0) {
            log.debug(String.format("InFile2TextLineSpout: acks [%s]", this.ackedLines));
        }

        if (this.totaller.sum() == this.totalLines) {
            log.info(String.format("InFile2TextLineSpout.ack(): total acks [%s], total fails [%s], total errors [%s].",
                    this.ackedLines.get(), this.failedLines.get(), this.totaller.sum() - this.ackedLines.get() - this.failedLines.get()));
        }
    }

    @Override
    public void fail(Object msgId) {
        log.trace("InFile2TextLineSpout.fail() working."); // <- too much output (if there are many fails)
        this.failedLines.incrementAndGet(); // increment failed lines counter
        this.totaller.increment();

        try (Stream<String> fileLines = Files.lines(Paths.get(this.properties.getSourceFile()))) { // get value of failed line
            String errorLine = fileLines.skip(((long) msgId) - 1).findFirst().get();
            log.error(String.format("InFile2TextLineSpout: fails [%s]. Failed to process line #%s:%n [%s]", this.failedLines, msgId, errorLine));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        if (this.totaller.sum() == this.totalLines) {
            log.info(String.format("InFile2TextLineSpout.fail(): total acks [%s], total fails [%s], total errors [%s].",
                    this.ackedLines.get(), this.failedLines.get(), this.totaller.sum() - this.ackedLines.get() - this.failedLines.get()));
        }
    }

    @Override
    public void close() {
        log.debug("InFile2TextLineSpout.close() working.");
        this.scanner.close();
    }

}
