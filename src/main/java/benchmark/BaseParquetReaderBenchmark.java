package benchmark;


import java.io.File;

public abstract class BaseParquetReaderBenchmark {
    public String filePath = new File("src/main/resources/yellow_tripdata_2022-01.parquet").toURI().toString();
    public Integer batchSize = 200_000;
}
