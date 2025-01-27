package benchmark;

import org.apache.avro.generic.GenericRecord;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class AvroParquetReaderBenchmark {

    static class CountingProcessor implements Consumer<GenericRecord> {
        private final Blackhole blackhole;

        public Long totalRows = 0L;
        public Long hashCodeSum = 0L;

        public CountingProcessor(Blackhole blackhole) {
            this.blackhole = blackhole;
        }
        @Override
        public void accept(GenericRecord record) {
            totalRows += 1;

            long sum = 0;
            for (var field: record.getSchema().getFields()) {
                if (field != null) {
                    Object obj = record.get(field.pos());
                    if (obj != null) {
                        sum += obj.hashCode();
                    }
                }
            }
            hashCodeSum += sum;
            if (blackhole != null) {
                blackhole.consume(record);
            }
        }
    }

    @Benchmark
    public void readAllColumns(Blackhole blackhole, BenchState state) throws Exception {
        var counter = new CountingProcessor(blackhole);
        var rdr = new AvroParquetReader();
        rdr.read(state.getInputPath(), counter);
        blackhole.consume(counter.totalRows);
        blackhole.consume(counter.hashCodeSum);
    }
}