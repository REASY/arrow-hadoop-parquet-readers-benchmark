package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(org.openjdk.jmh.annotations.Scope.Benchmark)
public class ArrowParquetReaderBenchmark extends BaseParquetReaderBenchmark {

    @Benchmark
    public void readAllColumns(Blackhole blackhole) throws Exception {
        var counter = new CountingBatchProcessor(blackhole);
        var rdr = new ArrowParquetReader();
        rdr.read(filePath, batchSize,  counter);
    }
}