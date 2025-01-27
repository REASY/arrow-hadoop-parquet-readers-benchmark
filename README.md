Arrow and Hadoop Parquet readers benchmark
======

JMH benchmarks to measure the performance of parquet readers

- Apache Parquet Hadoop (parquet-hadoop v1.15.0)
- Apache Arrow Parquet  (arrow-dataset v18.1.0)
- Apache Parquet Avro   (parquet-avro v1.15.0)

# How to run

1. Make sure you have installed maven and Java 21
2. `mvn package` in the root folder of the project to create JAR
   a. Copy files from [src/main/resources](src/main/resources)
   to [tmpfs](https://docs.kernel.org/filesystems/tmpfs.html) to reduce IO influence, tmpfs is RAM disk
3. `java --add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED -jar target/benchmarks.jar -prof gc -p inputPath=#PATH_TO_PARQUET#` to run benchmarks

## Dataset: Yellow Taxi Trip Records      

Test data is taken from Taxi & Limousine Commission, TLC Trip Record Data, January 2022, Yellow Taxi Trip
Records (https://nyc-tlc.s3.amazonaws.com/trip+data/yellow_tripdata_2022-01.parquet). This file is copied
as [a resource to the project](src/main/resources/yellow_tripdata_2022-01.parquet). Test data contains 2463931 rows and
has 19 columns, sample:

```
+----------+-----------------------+-----------------------+-----------------+---------------+------------+--------------------+--------------+--------------+--------------+-------------+-------+---------+------------+--------------+-----------------------+--------------+----------------------+-------------+
| VendorID | tpep_pickup_datetime  | tpep_dropoff_datetime | passenger_count | trip_distance | RatecodeID | store_and_fwd_flag | PULocationID | DOLocationID | payment_type | fare_amount | extra | mta_tax | tip_amount | tolls_amount | improvement_surcharge | total_amount | congestion_surcharge | airport_fee |
+----------+-----------------------+-----------------------+-----------------+---------------+------------+--------------------+--------------+--------------+--------------+-------------+-------+---------+------------+--------------+-----------------------+--------------+----------------------+-------------+
| 1        | 2022-01-01 00:35:40.0 | 2022-01-01 00:53:29.0 | 2.0             | 3.8           | 1.0        | N                  | 142          | 236          | 1            | 14.5        | 3.0   | 0.5     | 3.65       | 0.0          | 0.3                   | 21.95        | 2.5                  | 0.0         |
| 1        | 2022-01-01 00:33:43.0 | 2022-01-01 00:42:07.0 | 1.0             | 2.1           | 1.0        | N                  | 236          | 42           | 1            | 8.0         | 0.5   | 0.5     | 4.0        | 0.0          | 0.3                   | 13.3         | 0.0                  | 0.0         |
| 2        | 2022-01-01 00:53:21.0 | 2022-01-01 01:02:19.0 | 1.0             | 0.97          | 1.0        | N                  | 166          | 166          | 1            | 7.5         | 0.5   | 0.5     | 1.76       | 0.0          | 0.3                   | 10.56        | 0.0                  | 0.0         |
| 2        | 2022-01-01 00:25:21.0 | 2022-01-01 00:35:23.0 | 1.0             | 1.09          | 1.0        | N                  | 114          | 68           | 2            | 8.0         | 0.5   | 0.5     | 0.0        | 0.0          | 0.3                   | 11.8         | 2.5                  | 0.0         |
| 2        | 2022-01-01 00:36:48.0 | 2022-01-01 01:14:20.0 | 1.0             | 4.3           | 1.0        | N                  | 68           | 163          | 1            | 23.5        | 0.5   | 0.5     | 3.0        | 0.0          | 0.3                   | 30.3         | 2.5                  | 0.0         |
| 1        | 2022-01-01 00:40:15.0 | 2022-01-01 01:09:48.0 | 1.0             | 10.3          | 1.0        | N                  | 138          | 161          | 1            | 33.0        | 3.0   | 0.5     | 13.0       | 6.55         | 0.3                   | 56.35        | 2.5                  | 0.0         |
| 2        | 2022-01-01 00:20:50.0 | 2022-01-01 00:34:58.0 | 1.0             | 5.07          | 1.0        | N                  | 233          | 87           | 1            | 17.0        | 0.5   | 0.5     | 5.2        | 0.0          | 0.3                   | 26.0         | 2.5                  | 0.0         |
| 2        | 2022-01-01 00:13:04.0 | 2022-01-01 00:22:45.0 | 1.0             | 2.02          | 1.0        | N                  | 238          | 152          | 2            | 9.0         | 0.5   | 0.5     | 0.0        | 0.0          | 0.3                   | 12.8         | 2.5                  | 0.0         |
| 2        | 2022-01-01 00:30:02.0 | 2022-01-01 00:44:49.0 | 1.0             | 2.71          | 1.0        | N                  | 166          | 236          | 1            | 12.0        | 0.5   | 0.5     | 2.25       | 0.0          | 0.3                   | 18.05        | 2.5                  | 0.0         |
| 2        | 2022-01-01 00:48:52.0 | 2022-01-01 00:53:28.0 | 1.0             | 0.78          | 1.0        | N                  | 236          | 141          | 2            | 5.0         | 0.5   | 0.5     | 0.0        | 0.0          | 0.3                   | 8.8          | 2.5                  | 0.0         |
+----------+-----------------------+-----------------------+-----------------+---------------+------------+--------------------+--------------+--------------+--------------+-------------+-------+---------+------------+--------------+-----------------------+--------------+----------------------+-------------+
```

### Results

| Reader             | Average, ms | Faster than AvroParquetReader, times | Average gc.alloc.rate, MB/sec | gc.time , ms |
|--------------------|-------------|--------------------------------------|-------------------------------|--------------|
| ArrowParquetReader | 837.026     | 8.457                                | 1436.499                      | 77           |
| HadoopGroupReader  | 3424.390    | 2.067                                | 4524.978                      | 644          |
| AvroParquetReader  | 7078.314    | 1                                    | 2395.299                      | 652          |

Raw results:

```
Benchmark                                                      Mode  Cnt            Score        Error   Units
ArrowParquetReaderBenchmark.readAllColumns                     avgt    5          837.026 ±      8.953   ms/op
ArrowParquetReaderBenchmark.readAllColumns:gc.alloc.rate       avgt    5         1436.499 ±     15.323  MB/sec
ArrowParquetReaderBenchmark.readAllColumns:gc.alloc.rate.norm  avgt    5   1260805553.333 ± 120820.850    B/op
ArrowParquetReaderBenchmark.readAllColumns:gc.count            avgt    5          123.000               counts
ArrowParquetReaderBenchmark.readAllColumns:gc.time             avgt    5           77.000                   ms
AvroParquetReaderBenchmark.readAllColumns                      avgt    5         7078.314 ±    202.397   ms/op
AvroParquetReaderBenchmark.readAllColumns:gc.alloc.rate        avgt    5         2395.299 ±     67.893  MB/sec
AvroParquetReaderBenchmark.readAllColumns:gc.alloc.rate.norm   avgt    5  17777679391.200 ± 562015.622    B/op
AvroParquetReaderBenchmark.readAllColumns:gc.count             avgt    5          246.000               counts
AvroParquetReaderBenchmark.readAllColumns:gc.time              avgt    5          652.000                   ms
HadoopGroupReaderBenchmark.readAllColumns                      avgt    5         3424.390 ±     92.500   ms/op
HadoopGroupReaderBenchmark.readAllColumns:gc.alloc.rate        avgt    5         4524.978 ±    121.529  MB/sec
HadoopGroupReaderBenchmark.readAllColumns:gc.alloc.rate.norm   avgt    5  16247558191.467 ± 525162.429    B/op
HadoopGroupReaderBenchmark.readAllColumns:gc.count             avgt    5          198.000               counts
HadoopGroupReaderBenchmark.readAllColumns:gc.time              avgt    5          644.000                   ms
```

## Dataset: Synthetic Cumulative Histogram data

Test data contains 1000 rows and has 45 columns (41 columns for tags and 4 list columns) , sample:

```
┌─────────────┬───────────────────┬──────────────┬──────────────────┬───┬────────────┬──────────────────────┬──────────────────────┬──────────────────────┬──────────────────────┐
│ application │ availability_zone │ build_number │ cloud_account_id │ … │ tenant_id  │          ts          │     sums_double      │      sums_long       │        count         │
│   varchar   │      varchar      │   varchar    │     varchar      │   │  varchar   │       int64[]        │       double[]       │       int64[]        │       int64[]        │
├─────────────┼───────────────────┼──────────────┼──────────────────┼───┼────────────┼──────────────────────┼──────────────────────┼──────────────────────┼──────────────────────┤
│ accounts    │ zone-b            │ 2169         │ 726820024292     │ … │ tenant-267 │ [1737967143010, 17…  │ []                   │ [26, 36, 45, 93, 1…  │ [8, 16, 21, 25, 34…  │
│ accounts    │ zone-b            │ 2604         │ 299380463457     │ … │ tenant-488 │ [1737967143010, 17…  │ []                   │ [5, 10, 36, 73, 11…  │ [3, 13, 17, 19, 23…  │
│ inventory   │ zone-a            │ 9821         │ 101872852789     │ … │ tenant-953 │ [1737967143010, 17…  │ []                   │ [38, 80, 110, 139,…  │ [7, 17, 23, 31, 35…  │
│ accounts    │ zone-c            │ 2876         │ 748866464070     │ … │ tenant-344 │ [1737967143010, 17…  │ []                   │ [35, 78, 83, 125, …  │ [6, 13, 19, 28, 34…  │
```

### Results

| Reader             | Average, ms | Faster than AvroParquetReader, times | Average gc.alloc.rate, MB/sec | gc.time , ms |
|--------------------|-------------|--------------------------------------|-------------------------------|--------------|
| ArrowParquetReader | 4.047       | 9.650                                | 2111.226                      | 65           |
| HadoopGroupReader  | 23.331      | 1.674                                | 4238.102                      | 436          |
| OptimizedReader    | 16.819      | 2.322                                | 2542.464                      | 356          |
| AvroParquetReader  | 39.054      | 1                                    | 1697.357                      | 229          |

Raw results:

```
Benchmark                                                                       (inputPath)  Mode  Cnt          Score       Error   Units
ArrowParquetReaderBenchmark.readAllColumns                            /tmp/syntetic.parquet  avgt    5          4.047 ±     0.619   ms/op
ArrowParquetReaderBenchmark.readAllColumns:gc.alloc.rate              /tmp/syntetic.parquet  avgt    5       2111.226 ±   319.393  MB/sec
ArrowParquetReaderBenchmark.readAllColumns:gc.alloc.rate.norm         /tmp/syntetic.parquet  avgt    5    8947556.587 ±  3819.001    B/op
ArrowParquetReaderBenchmark.readAllColumns:gc.count                   /tmp/syntetic.parquet  avgt    5        190.000              counts
ArrowParquetReaderBenchmark.readAllColumns:gc.time                    /tmp/syntetic.parquet  avgt    5         65.000                  ms
AvroParquetReaderBenchmark.readAllColumns                             /tmp/syntetic.parquet  avgt    5         39.054 ±     2.273   ms/op
AvroParquetReaderBenchmark.readAllColumns:gc.alloc.rate               /tmp/syntetic.parquet  avgt    5       1697.357 ±    97.093  MB/sec
AvroParquetReaderBenchmark.readAllColumns:gc.alloc.rate.norm          /tmp/syntetic.parquet  avgt    5   69497445.903 ± 40605.182    B/op
AvroParquetReaderBenchmark.readAllColumns:gc.count                    /tmp/syntetic.parquet  avgt    5        281.000              counts
AvroParquetReaderBenchmark.readAllColumns:gc.time                     /tmp/syntetic.parquet  avgt    5        229.000                  ms
HadoopGroupReaderBenchmark.readAllColumns                             /tmp/syntetic.parquet  avgt    5         23.331 ±     2.642   ms/op
HadoopGroupReaderBenchmark.readAllColumns:gc.alloc.rate               /tmp/syntetic.parquet  avgt    5       4238.102 ±   461.358  MB/sec
HadoopGroupReaderBenchmark.readAllColumns:gc.alloc.rate.norm          /tmp/syntetic.parquet  avgt    5  103616114.393 ± 36254.937    B/op
HadoopGroupReaderBenchmark.readAllColumns:gc.count                    /tmp/syntetic.parquet  avgt    5        472.000              counts
HadoopGroupReaderBenchmark.readAllColumns:gc.time                     /tmp/syntetic.parquet  avgt    5        436.000                  ms
optimized.OptimizedReaderBenchmark.readAllColumns                     /tmp/syntetic.parquet  avgt    5         16.819 ±     1.211   ms/op
optimized.OptimizedReaderBenchmark.readAllColumns:gc.alloc.rate       /tmp/syntetic.parquet  avgt    5       2542.464 ±   179.210  MB/sec
optimized.OptimizedReaderBenchmark.readAllColumns:gc.alloc.rate.norm  /tmp/syntetic.parquet  avgt    5   44827908.387 ± 24944.764    B/op
optimized.OptimizedReaderBenchmark.readAllColumns:gc.count            /tmp/syntetic.parquet  avgt    5        413.000              counts
optimized.OptimizedReaderBenchmark.readAllColumns:gc.time             /tmp/syntetic.parquet  avgt    5        356.000                  ms
```