Arrow and Hadoop Parquet readers benchmark
======

JMH benchmarks to measure the performance of parquet readers

- Apache Parquet Hadoop (parquet-hadoop v1.15.0)
- Apache Arrow Parquet  (arrow-dataset v18.1.0)
- Apache Parquet Avro   (parquet-avro v1.15.0)

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

1. Make sure you have installed maven and Java 21
2. `mvn package` in the root folder of the project to create JAR
3. `java --add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED -jar target/benchmarks.jar -prof gc` to
   run benchmarks

## Results

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

