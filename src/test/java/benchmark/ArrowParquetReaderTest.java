package benchmark;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ArrowParquetReaderTest {

    @Test
    void can_read() throws Exception {
        String filePath = new File("src/main/resources/yellow_tripdata_2022-01.parquet").toURI().toString();
        var counter = new BaseParquetReaderBenchmark.CountingBatchProcessor(null);
        var rdr = new ArrowParquetReader();
        rdr.read(filePath, 10000,  counter);
        System.out.format("Batches: %d, total rows: %d\n", counter.batches, counter.totalRows);

        assertEquals(2463931, counter.totalRows);
    }
}