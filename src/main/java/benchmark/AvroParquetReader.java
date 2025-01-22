package benchmark;

import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;

import java.util.function.Consumer;

public class AvroParquetReader {
    void read(String path, Consumer<GenericRecord> consumer) throws Exception {
        Configuration conf = new Configuration();
        HadoopInputFile file = HadoopInputFile.fromPath(new Path(path), conf);
        try (ParquetReader<GenericRecord> rdr = org.apache.parquet.avro.AvroParquetReader.genericRecordReader(file)) {
            GenericRecord read = rdr.read();
            while (read != null) {
                consumer.accept(read);
                read = rdr.read();
            }
        }
    }
}
