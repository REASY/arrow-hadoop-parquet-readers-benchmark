package benchmark;

import org.apache.arrow.util.AutoCloseables;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;

import java.util.function.Consumer;

public class HadoopGroupReader {
    void read(String path, Consumer<Group> consumer) throws Exception {
        ParquetReader<Group> rdr = ParquetReader.builder(new GroupReadSupport(), new Path(path)).build();

        try {
            Group read = rdr.read();
            while(read != null) {
                consumer.accept(read);
                read = rdr.read();
            }
        } finally {
            AutoCloseables.close(rdr);
        }
    }
}
