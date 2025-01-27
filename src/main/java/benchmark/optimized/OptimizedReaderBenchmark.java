package benchmark.optimized;

import benchmark.BaseParquetReaderBenchmark;
import benchmark.optimized.converters.RecordConverter;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class OptimizedReaderBenchmark extends BaseParquetReaderBenchmark {
    public static class CountingProcessor implements Consumer<RecordConverter.Record> {

        private final Blackhole blackhole;

        public Long totalRows = 0L;

        public Long hashCodeSum = 0L;

        public CountingProcessor(Blackhole blackhole) {
            this.blackhole = blackhole;
        }

        @Override
        public void accept(RecordConverter.Record record) {
            totalRows += 1;
            long sum = 0L;
            sum += Arrays.hashCode(record.ts);
            sum += Arrays.hashCode(record.count);
            sum += Arrays.hashCode(record.sumsDouble);
            sum += Arrays.hashCode(record.sumsLong);
            sum += record.tagValues.hashCode();
            hashCodeSum += sum;
            if (this.blackhole != null) {
                this.blackhole.consume(sum);
            }
        }
    }


    @Benchmark
    public void readAllColumns(Blackhole blackhole) throws Exception {
        var counter = new CountingProcessor(blackhole);
        var rdr = new OptimizedHadoopReader();
        rdr.read(filePath, counter);
        blackhole.consume(counter.totalRows);
        blackhole.consume(counter.hashCodeSum);
    }
}
