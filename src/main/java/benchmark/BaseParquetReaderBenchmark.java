package benchmark;


import java.io.File;

public abstract class BaseParquetReaderBenchmark {
    public String filePath = new File("/home/user/github/REASY/parquet-playground-rs/syntetic.parquet").toURI().toString();
    public Integer batchSize = 200_000;
}
