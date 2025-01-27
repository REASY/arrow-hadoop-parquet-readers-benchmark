package benchmark.optimized.converters;

import com.google.common.base.Strings;
import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordConverter extends GroupConverter {
    private final Converter[] converters;
    private Record record;
    private final String[] tags;


    public RecordConverter(MessageType schema) {
        int fieldCount = schema.getFieldCount();
        this.converters = new Converter[fieldCount];
        this.tags = new String[fieldCount - 4];

        for (int i = 0; i < fieldCount; i++) {
            converters[i] = createConverter(i, schema.getType(i));
            if (i < fieldCount - 4) {
                tags[i] = schema.getFieldName(i);
            }
        }
    }

    private Converter createConverter(int fieldIndex, Type field) {
        if (field.isPrimitive()) {
            return new PrimitiveConverterWithString(this, field.getName(), fieldIndex);
        } else {
            GroupType groupType = field.asGroupType();
            if (groupType.getLogicalTypeAnnotation() == LogicalTypeAnnotation.listType()) {
                return new OptimizedListConverter(this, fieldIndex, groupType);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + field);
            }
        }
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return converters[fieldIndex];
    }

    @Override
    public void start() {
        record = new Record(this.tags);
    }

    @Override
    public void end() {
    }

    public Record getCurrentRecord() {
        return record;
    }

    public static class Record {
        private final String[] tags;

        public final List<String> tagValues = new ArrayList<>();
        public long[] ts;
        public double[] sumsDouble;
        public long[] sumsLong;
        public long[] count;

        public Record(String[] tags) {
            this.tags = tags;
        }

        public void add(int fieldIndex, String name, Object value, boolean isList) {
            if (isList) {
                switch (name) {
                    case "ts":
                        ts = (long[]) value;
                        break;
                    case "sums_double":
                        sumsDouble = (double[]) value;
                        break;
                    case "sums_long":
                        sumsLong = (long[]) value;
                        break;
                    case "count":
                        count = (long[]) value;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported column: " + name);
                }
            } else {
                String tag = this.tags[fieldIndex];
                this.tagValues.add(tag);
                this.tagValues.add((String) value);
            }
        }

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            List<NameValue> values = getNameValues();
            prettyPrint(new PrintWriter(sw, true), 0, values);
            return sw.toString();
        }

        public List<NameValue> getNameValues() {
            List<NameValue> values = new ArrayList<>();
            for (int i = 0; i < this.tagValues.size(); i += 2) {
                values.add(new NameValue(this.tagValues.get(i), this.tagValues.get(i + 1)));
            }
            values.add(new NameValue("ts", this.ts));
            values.add(new NameValue("sums_double", this.sumsDouble));
            values.add(new NameValue("sums_long", this.sumsLong));
            values.add(new NameValue("count", this.count));
            return values;
        }

        public void prettyPrint(PrintWriter out, int depth, List<NameValue> values) {
            for (NameValue value : values) {
                out.print(Strings.repeat(".", depth));
                prettyPrint(out, depth, value);
                out.println();
            }
        }

        static private void prettyPrint(PrintWriter out, int depth, NameValue value) {
            out.print(value.name);
            Object val = value.value;
            if (val == null) {
                out.print(": <null>");
            } else if (val.getClass().isArray()) {
                out.print(": ");
                switch (val) {
                    case long[] arr -> out.print(Arrays.toString(arr));
                    case double[] arr -> out.print(Arrays.toString(arr));
                    default -> throw new IllegalArgumentException("Unsupported type: " + val.getClass());
                }
            } else if (Record.class.isAssignableFrom(val.getClass())) {
                out.println(": ");
                Record r = (Record) val;
                r.prettyPrint(out, depth + 1, r.getNameValues());
            } else {
                out.print(": ");
                out.print(val);
            }
        }
    }

    public record NameValue(String name, Object value) {

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            Record.prettyPrint(new PrintWriter(sw, true), 0, this);
            return sw.toString();
        }
    }


}