package ru.dmitriigusev.kata.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.dmitriigusev.kata.arrays.ArraysSort;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/***/

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
//@Warmup(iterations = 3)
//@Measurement(iterations = 8)
public class BenchmarkArraysSort {

    @Param({"10000000"}) // array under test size
    private int N;

    private int[] DATA_FOR_TESTING;

    /***/
    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(BenchmarkArraysSort.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        DATA_FOR_TESTING = createData();
    }

    @Benchmark
    public void mergeSort(Blackhole bh) {
        bh.consume(ArraysSort.mergeSort(DATA_FOR_TESTING));
    }

//    @Benchmark
//    public void loopFor(Blackhole bh) {
//        for (int i = 0; i < DATA_FOR_TESTING.size(); i++) {
//            String s = DATA_FOR_TESTING.get(i); //take out n consume, fair with foreach
//            bh.consume(s);
//        }
//    }
//
//    @Benchmark
//    public void loopWhile(Blackhole bh) {
//        int i = 0;
//        while (i < DATA_FOR_TESTING.size()) {
//            String s = DATA_FOR_TESTING.get(i);
//            bh.consume(s);
//            i++;
//        }
//    }
//
//    @Benchmark
//    public void loopForEach(Blackhole bh) {
//        for (String s : DATA_FOR_TESTING) {
//            bh.consume(s);
//        }
//    }
//
//    @Benchmark
//    public void loopIterator(Blackhole bh) {
//        Iterator<String> iterator = DATA_FOR_TESTING.iterator();
//        while (iterator.hasNext()) {
//            String s = iterator.next();
//            bh.consume(s);
//        }
//    }

    /** Generate data for testing. */
    private int[] createData() {
        //int[] data = new int[N];
        //for (int i = 0; i < N; i++) {
        //    data[i] =
        //}
        //return data;

        return new Random().ints(N).toArray();
    }

}