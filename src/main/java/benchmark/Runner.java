package benchmark;

import benchmark.optimized.OptimizedHadoopReader;
import benchmark.optimized.OptimizedReaderBenchmark;
import org.apache.commons.cli.*;

import java.io.File;

public class Runner {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addRequiredOption("i", "input-parquet-file-path", true, "Input path to Parquet");
        options.addRequiredOption("r", "reader-type", true, "The type of Parquet reader. One of ArrowParquetReader, AvroParquetReader, HadoopGroupReader or OptimizedHadoopReader");
        options.addOption("n", "number-of-iterations", true, "Number of iterations");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = parseCmdOrExit(args, parser, options, formatter);

        String input = new File(cmd.getOptionValue("input-parquet-file-path")).toURI().toString();
        String readerType = cmd.getOptionValue("reader-type");
        int numberOfIterations = Integer.parseInt(cmd.getOptionValue("number-of-iterations", "1000"));
        System.out.println("Parquet file path: " + input);
        System.out.println("readerType: " + readerType);
        System.out.println("numberOfIterations: " + numberOfIterations);

        System.out.println("Warming up the JVM...");
        // Lets warm-up the JVM
        runBenchmark(500, readerType, input, false);

        System.out.println("Running the benchmark...");
        runBenchmark(numberOfIterations, readerType, input, true);

    }

    private static void runBenchmark(int numberOfIterations, String readerType, String input, boolean shouldReport) throws Exception {
        var s = System.currentTimeMillis();
        Long total = 0L;
        Long totalHash = 0L;

        for (var i = 0; i < numberOfIterations; i++) {
            switch (readerType) {
                case "ArrowParquetReader": {
                    var counter = new ArrowParquetReaderBenchmark.CountingBatchProcessor(null);
                    var rdr = new ArrowParquetReader();
                    rdr.read(input, 10000, counter);
                    total += counter.totalRows;
                    totalHash += counter.hashCodeSum;
                }
                break;
                case "AvroParquetReader": {
                    var counter = new AvroParquetReaderBenchmark.CountingProcessor(null);
                    var rdr = new AvroParquetReader();
                    rdr.read(input, counter);
                    total += counter.totalRows;
                    totalHash += counter.hashCodeSum;
                }
                break;
                case "HadoopGroupReader": {
                    var counter = new HadoopGroupReaderBenchmark.CountingProcessor(null);
                    var rdr = new HadoopGroupReader();
                    rdr.read(input, counter);
                    total += counter.totalRows;
                    totalHash += counter.hashCodeSum;
                }
                break;
                case "OptimizedHadoopReader": {
                    var counter = new OptimizedReaderBenchmark.CountingProcessor(null);
                    var rdr = new OptimizedHadoopReader();
                    rdr.read(input, counter);
                    total += counter.totalRows;
                    totalHash += counter.hashCodeSum;
                }
                break;
                default:
                    throw new RuntimeException("Unknown reader type: " + readerType);
            }
        }
        var dt = System.currentTimeMillis() - s;
        double avg = (double) dt / numberOfIterations;
        if (shouldReport) {
            System.out.printf("Total time is %d ms, average time %.3f ms. Dummy counters: (%d, %d)", dt, avg, total, totalHash);
        }
    }

    private static CommandLine parseCmdOrExit(String[] args, CommandLineParser parser, Options options, HelpFormatter formatter) {
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Runner", options);
            System.exit(1);
            return null;
        }
    }
}
