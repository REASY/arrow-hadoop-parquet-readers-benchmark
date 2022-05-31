package benchmark;

import org.apache.parquet.example.data.Group;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(org.openjdk.jmh.annotations.Scope.Benchmark)
public class HadoopGroupReaderBenchmark extends BaseParquetReaderBenchmark {
    static class CountingProcessor implements Consumer<Group> {

        private final Blackhole blackhole;

        public Long totalRows = 0L;

        public Integer batches = 0;

        public Long hashCodeSum = 0L;

        public CountingProcessor(Blackhole blackhole) {
            this.blackhole = blackhole;
        }

        @Override
        public void accept(Group record) {
            totalRows += 1;

            long sum = 0L;
            for (Type field : record.getType().getFields()) {
                var n = record.getFieldRepetitionCount(field.getName());
                for (int i = 0; i < n; i++) {
                    if (field.isPrimitive()) {
                        var primitive = field.asPrimitiveType();
                        if (primitive.getPrimitiveTypeName() == PrimitiveType.PrimitiveTypeName.BOOLEAN) {
                            sum += record.getBoolean(field.getName(), i) ? 1 : 0;
                        } else if (primitive.getPrimitiveTypeName() == PrimitiveType.PrimitiveTypeName.INT32) {
                            sum += record.getInteger(field.getName(), i);
                        }
                        else if (primitive.getPrimitiveTypeName() == PrimitiveType.PrimitiveTypeName.INT64) {
                            sum += record.getLong(field.getName(), i);
                        }
                        else if (primitive.getPrimitiveTypeName() == PrimitiveType.PrimitiveTypeName.FLOAT) {
                            sum += record.getFloat(field.getName(), i);
                        }
                        else if (primitive.getPrimitiveTypeName() == PrimitiveType.PrimitiveTypeName.DOUBLE) {
                            sum += record.getDouble(field.getName(), i);
                        }
                        else if (primitive.getPrimitiveTypeName() == PrimitiveType.PrimitiveTypeName.BINARY &&
                                primitive.getLogicalTypeAnnotation() instanceof LogicalTypeAnnotation.StringLogicalTypeAnnotation) {
                            sum += record.getString(field.getName(), i).hashCode();
                        }
                        else {
                            throw new IllegalStateException(primitive.getPrimitiveTypeName().toString());
                        }
                    }
                    else {
                        throw new IllegalStateException(field.toString());
                    }
                }
            }
            hashCodeSum += sum;
            if (this.blackhole != null) {
                this.blackhole.consume(sum);
            }
        }
    }


    @Benchmark
    public void readAllColumns(Blackhole blackhole) throws Exception {
        var counter = new CountingProcessor(blackhole);
        var rdr = new HadoopGroupReader();
        rdr.read(filePath, counter);
        blackhole.consume(counter.totalRows);
        blackhole.consume(counter.hashCodeSum);
    }
}
