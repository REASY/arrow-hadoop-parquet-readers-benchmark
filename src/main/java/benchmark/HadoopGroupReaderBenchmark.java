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
                    if (field.isPrimitive()) {
                        var primitive = field.asPrimitiveType();
                        int fieldIndex = record.getType().getFieldIndex(field.getName());
                        sum += readField(record, primitive, fieldIndex);
                    } else {
                        var groupType = field.asGroupType();
                        if (groupType.getLogicalTypeAnnotation() instanceof LogicalTypeAnnotation.ListLogicalTypeAnnotation) {
                            var fieldGroup = record.getGroup(field.getName(), 0);
                            var count = fieldGroup.getFieldRepetitionCount(0);
                            for (int k = 0; k < count; k++) {
                                var listGroup = fieldGroup.getGroup("list", k);
                                if (listGroup.getFieldRepetitionCount(0) == 1) {
                                    // Only handle List<Optional<PrimitiveType>>
                                    var itemType = listGroup.getType().getType("item").asPrimitiveType();
                                    sum += readField(listGroup, itemType, 0);
                                }
                            }
                        } else {
                            throw new IllegalStateException(field.toString());
                        }
                    }
                }
            }
            hashCodeSum += sum;
            if (this.blackhole != null) {
                this.blackhole.consume(sum);
            }
        }
    }

    private static int readField(Group group, PrimitiveType itemType, int fieldIndex) {
        var primitiveTypeName = itemType.getPrimitiveTypeName();
        int hash;
        switch (primitiveTypeName) {
            case PrimitiveType.PrimitiveTypeName.BOOLEAN:
                hash = group.getBoolean(fieldIndex, 0) ? 1 : 0;
                break;
            case PrimitiveType.PrimitiveTypeName.INT32:
                hash = group.getInteger(fieldIndex, 0);
                break;
            case PrimitiveType.PrimitiveTypeName.INT64:
                hash = (int)group.getLong(fieldIndex, 0);
                break;
            case PrimitiveType.PrimitiveTypeName.FLOAT:
                hash = (int) group.getFloat(fieldIndex, 0);
                break;
            case PrimitiveType.PrimitiveTypeName.DOUBLE:
                hash = (int) group.getDouble(fieldIndex, 0);
                break;
            case PrimitiveType.PrimitiveTypeName.BINARY:
                if (itemType.getLogicalTypeAnnotation() instanceof LogicalTypeAnnotation.StringLogicalTypeAnnotation) {
                    hash = group.getString(fieldIndex, 0).hashCode();
                } else {
                    hash = group.getBinary(fieldIndex, 0).hashCode();
                }
                break;
            default:
                throw new IllegalStateException(primitiveTypeName.toString());
        }
        return hash;
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
