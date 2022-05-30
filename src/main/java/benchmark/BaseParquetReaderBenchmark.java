package benchmark;

import org.apache.arrow.vector.VectorSchemaRoot;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.util.function.Consumer;

public abstract class BaseParquetReaderBenchmark {
    public String filePath = new File("src/main/resources/yellow_tripdata_2022-01.parquet").toURI().toString();
    public Integer batchSize = 200_000;

    static class CountingBatchProcessor implements Consumer<VectorSchemaRoot> {
        private final Blackhole blackhole;

        public Long totalRows = 0L;

        public Integer batches = 0;

        public CountingBatchProcessor(Blackhole blackhole) {
            this.blackhole = blackhole;
        }

        @Override
        public void accept(VectorSchemaRoot root) {
            totalRows += root.getRowCount();
            batches += 1;

            // Null check so it can be reused in test
            if (blackhole != null) {
                blackhole.consume(root);
            }
        }
    }
}
