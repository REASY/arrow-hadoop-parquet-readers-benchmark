Arrow and Hadoop Parquet readers benchmark
======

JMH benchmarks to measure the performance of parquet readers
- Apache Hadoop parquet (parquet-avro v1.12.3)
- Apache Arrow parquet (arrow-dataset v8.0.0)

Test data is taken from Taxi & Limousine Commission, TLC Trip Record Data, January 2022, Yellow Taxi Trip Records (https://nyc-tlc.s3.amazonaws.com/trip+data/yellow_tripdata_2022-01.parquet). This file is copied as [a resource to the project](src/main/resources/yellow_tripdata_2022-01.parquet). Test data contains 2463931 rows and has 19 columns, sample:
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
Arrow parquet reader reads all the rows in chunks in **2177.510 ms**, Hadoop parquet readers reads all the rows in **19588.983 ms**, **8.9 times slower**!

Raw results:
```
ArrowParquetReaderBenchmark.readAllColumns                                    avgt    5         2177.510 ±        63.118   ms/op
ArrowParquetReaderBenchmark.readAllColumns:·gc.alloc.rate                     avgt    5          981.258 ±        26.948  MB/sec
ArrowParquetReaderBenchmark.readAllColumns:·gc.alloc.rate.norm                avgt    5   2343524207.040 ±     94336.692    B/op
ArrowParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Eden_Space            avgt    5          983.570 ±        85.443  MB/sec
ArrowParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Eden_Space.norm       avgt    5   2349145784.320 ± 213869388.371    B/op
ArrowParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Old_Gen               avgt    5            0.006 ±         0.040  MB/sec
ArrowParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Old_Gen.norm          avgt    5        13848.640 ±     95908.293    B/op
ArrowParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Survivor_Space        avgt    5            0.070 ±         0.606  MB/sec
ArrowParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Survivor_Space.norm   avgt    5       167772.160 ±   1444568.895    B/op
ArrowParquetReaderBenchmark.readAllColumns:·gc.count                          avgt    5           94.000                  counts
ArrowParquetReaderBenchmark.readAllColumns:·gc.time                           avgt    5           49.000                      ms
HadoopParquetReaderBenchmark.readAllColumns                                   avgt    5        19588.983 ±       727.421   ms/op
HadoopParquetReaderBenchmark.readAllColumns:·gc.alloc.rate                    avgt    5          799.610 ±        29.085  MB/sec
HadoopParquetReaderBenchmark.readAllColumns:·gc.alloc.rate.norm               avgt    5  16843029508.800 ±     44442.571    B/op
HadoopParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Eden_Space           avgt    5          799.078 ±        28.164  MB/sec
HadoopParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Eden_Space.norm      avgt    5  16833419673.600 ± 980659082.616    B/op
HadoopParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Old_Gen              avgt    5            0.768 ±         1.709  MB/sec
HadoopParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Old_Gen.norm         avgt    5     16189332.800 ±  36132567.084    B/op
HadoopParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Survivor_Space       avgt    5            1.594 ±         0.795  MB/sec
HadoopParquetReaderBenchmark.readAllColumns:·gc.churn.G1_Survivor_Space.norm  avgt    5     33554432.000 ±  16150771.232    B/op
HadoopParquetReaderBenchmark.readAllColumns:·gc.count                         avgt    5          153.000                  counts
HadoopParquetReaderBenchmark.readAllColumns:·gc.time                          avgt    5          300.000                      ms
```

