package benchmark.optimized;

import benchmark.optimized.converters.RecordConverter;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetReader;

import java.util.function.Consumer;

public class OptimizedHadoopReader {
    void read(String path, Consumer<RecordConverter.Record> consumer) throws Exception {
        try (ParquetReader<RecordConverter.Record> rdr = ParquetReader.builder(new OptimizedReadSupport(), new Path(path)).build()) {
            RecordConverter.Record read = rdr.read();
            while (read != null) {
                consumer.accept(read);
                read = rdr.read();
            }
        }
    }
}
