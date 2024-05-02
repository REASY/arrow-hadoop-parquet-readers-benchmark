Arrow and Hadoop Parquet readers benchmark
======

JMH benchmarks to measure the performance of parquet readers

- Apache Hadoop parquet (parquet-avro v1.13.1)
- Apache Arrow parquet (arrow-dataset v16.0.0)

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

## How to run

1. Make sure you have installed maven and Java 11
2. `mvn package` in the root folder of the project to create JAR
3. `java -jar target/benchmarks.jar -prof gc` to run benchmarks

## Results

| Reader              | Average, ms | Faster than HadoopParquetReader, times | Average gc.alloc.rate, MB/sec | gc.time , ms |
|---------------------|-------------|----------------------------------------|-------------------------------|--------------|
| ArrowParquetReader  | 1551.534    | 10.3                                   | 1405.202                      | 87           |
| HadoopGroupReader   | 6152.581    | 2.6                                    | 2336.315                      | 341          |
| HadoopParquetReader | 15980.235   | 1                                      | 1008.220                      | 199          |

Raw results:

```
Benchmark                                                       Mode  Cnt            Score       Error   Units
ArrowParquetReaderBenchmark.readAllColumns                      avgt    5         1551.534 ±    78.654   ms/op
ArrowParquetReaderBenchmark.readAllColumns:gc.alloc.rate        avgt    5         1405.202 ±    71.406  MB/sec
ArrowParquetReaderBenchmark.readAllColumns:gc.alloc.rate.norm   avgt    5   2285875266.743 ± 39364.033    B/op
ArrowParquetReaderBenchmark.readAllColumns:gc.count             avgt    5          128.000              counts
ArrowParquetReaderBenchmark.readAllColumns:gc.time              avgt    5           87.000                  ms
HadoopGroupReaderBenchmark.readAllColumns                       avgt    5         6152.581 ±   331.406   ms/op
HadoopGroupReaderBenchmark.readAllColumns:gc.alloc.rate         avgt    5         2336.315 ±   125.578  MB/sec
HadoopGroupReaderBenchmark.readAllColumns:gc.alloc.rate.norm    avgt    5  15070635016.800 ± 32148.495    B/op
HadoopGroupReaderBenchmark.readAllColumns:gc.count              avgt    5          222.000              counts
HadoopGroupReaderBenchmark.readAllColumns:gc.time               avgt    5          341.000                  ms
HadoopParquetReaderBenchmark.readAllColumns                     avgt    5        15980.235 ±   827.535   ms/op
HadoopParquetReaderBenchmark.readAllColumns:gc.alloc.rate       avgt    5         1008.220 ±    52.112  MB/sec
HadoopParquetReaderBenchmark.readAllColumns:gc.alloc.rate.norm  avgt    5  16892091680.000 ± 44679.540    B/op
HadoopParquetReaderBenchmark.readAllColumns:gc.count            avgt    5          151.000              counts
HadoopParquetReaderBenchmark.readAllColumns:gc.time             avgt    5          199.000                  ms
```

