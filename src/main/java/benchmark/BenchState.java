package benchmark;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.File;

@State(Scope.Benchmark)
public class BenchState {
    @Param("src/main/resources/yellow_tripdata_2022-01.parquet")
    private String inputPath;

    public String getInputPath() {
        return new File(inputPath).toURI().toString();
    }
}
