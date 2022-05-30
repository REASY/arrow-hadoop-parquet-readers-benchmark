package benchmark;

import org.apache.arrow.util.AutoCloseables;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;

import java.util.function.Consumer;

public class HadoopParquetReader {
    void read(String path, Consumer<GenericRecord> consumer) throws Exception {
        Configuration conf = new Configuration();
        HadoopInputFile file = HadoopInputFile.fromPath(new Path(path), conf);
        ParquetReader<GenericRecord> rdr = org.apache.parquet.avro.AvroParquetReader.genericRecordReader(file);

        try {
            GenericRecord read = rdr.read();
            while(read != null) {
                consumer.accept(read);
                read = rdr.read();
            }
        } finally {
            AutoCloseables.close(rdr);
        }
    }
}
