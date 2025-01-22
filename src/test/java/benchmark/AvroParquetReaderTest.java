package benchmark;

import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvroParquetReaderTest {
    static class CountingProcessor implements Consumer<GenericRecord> {
        public Long totalRows = 0L;
        @Override
        public void accept(GenericRecord record) {
            totalRows += 1;
        }
    }

    @Test
    void can_read() throws Exception {
        String filePath = new File("src/main/resources/yellow_tripdata_2022-01.parquet").toURI().toString();
        var counter = new CountingProcessor();
        var rdr = new AvroParquetReader();
        rdr.read(filePath, counter);
        System.out.format("total rows: %d\n", counter.totalRows);

        assertEquals(2463931, counter.totalRows);
    }
}