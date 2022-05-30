package benchmark;

import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(org.openjdk.jmh.annotations.Scope.Benchmark)
public class ArrowParquetReaderBenchmark extends BaseParquetReaderBenchmark {

    static class CountingBatchProcessor implements Consumer<VectorSchemaRoot> {
        private final Blackhole blackhole;

        public Long totalRows = 0L;

        public Integer batches = 0;

        public Long hashCodeSum = 0L;

        public CountingBatchProcessor(Blackhole blackhole) {
            this.blackhole = blackhole;
        }

        @Override
        public void accept(VectorSchemaRoot root) {
            totalRows += root.getRowCount();
            batches += 1;

            long sum = 0;

            for (FieldVector field : root.getFieldVectors()) {
                for (int i = 0; i < field.getValueCount(); i++) {
                    Object obj = field.getObject(i);
                    if (obj != null) {
                        sum += obj.hashCode();
                    }
                }
            }
            hashCodeSum += sum;

            // Null check so it can be reused in test
            if (blackhole != null) {
                blackhole.consume(root);
                blackhole.consume(sum);
            }
        }
    }

    @Benchmark
    public void readAllColumns(Blackhole blackhole) throws Exception {
        var counter = new CountingBatchProcessor(blackhole);
        var rdr = new ArrowParquetReader();
        rdr.read(filePath, batchSize,  counter);
        blackhole.consume(counter.batches);
        blackhole.consume(counter.totalRows);
        blackhole.consume(counter.hashCodeSum);
    }
}