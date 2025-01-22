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
                // Only handle non-null values
                if (n == 1) {
                    int index = 0;
                    if (field.isPrimitive()) {
                        var primitive = field.asPrimitiveType();
                        var primitiveTypeName = primitive.getPrimitiveTypeName();
                        if (primitiveTypeName == PrimitiveType.PrimitiveTypeName.BOOLEAN) {
                            sum += record.getBoolean(field.getName(), index) ? 1 : 0;
                        } else if (primitiveTypeName == PrimitiveType.PrimitiveTypeName.INT32) {
                            sum += record.getInteger(field.getName(), index);
                        } else if (primitiveTypeName == PrimitiveType.PrimitiveTypeName.INT64) {
                            sum += record.getLong(field.getName(), index);
                        } else if (primitiveTypeName == PrimitiveType.PrimitiveTypeName.FLOAT) {
                            sum += (long) record.getFloat(field.getName(), index);
                        } else if (primitiveTypeName == PrimitiveType.PrimitiveTypeName.DOUBLE) {
                            sum += (long) record.getDouble(field.getName(), index);
                        } else if (primitiveTypeName == PrimitiveType.PrimitiveTypeName.BINARY && primitive.getLogicalTypeAnnotation() instanceof LogicalTypeAnnotation.StringLogicalTypeAnnotation) {
                            sum += record.getString(field.getName(), index).hashCode();
                        } else {
                            throw new IllegalStateException(primitiveTypeName.toString());
                        }
                    } else {
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
