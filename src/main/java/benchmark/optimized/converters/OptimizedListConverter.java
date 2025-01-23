package benchmark.optimized.converters;


import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;

class OptimizedListConverter extends GroupConverter {
    private final RecordConverter root;
    private final int fieldIndex;
    private final String name;
    private final PrimitiveType.PrimitiveTypeName elementTypeName;
    private final Converter elementConverter;
    private final LongArrayList longs = new LongArrayList();
    private final DoubleArrayList doubles = new DoubleArrayList();

    public OptimizedListConverter(RecordConverter root, int fieldIndex, GroupType listType) {
        this.root = root;
        this.fieldIndex = fieldIndex;
        this.name = listType.getName();

        Type repeatedType = listType.getType(0);
        Type elementType = (repeatedType.isPrimitive()) ? repeatedType : repeatedType.asGroupType().getType(0);
        this.elementTypeName = elementType.asPrimitiveType().getPrimitiveTypeName();
        if (this.elementTypeName == PrimitiveType.PrimitiveTypeName.DOUBLE) {
            this.elementConverter = new DoubleListElementConverter(doubles);
        } else if (this.elementTypeName == PrimitiveType.PrimitiveTypeName.INT64) {
            this.elementConverter = new LongListElementConverter(longs);

        } else throw new IllegalArgumentException("Unsupported primitive type: " + elementType);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return elementConverter;
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
        final RecordConverter.Record rec = root.getCurrentRecord();
        if (this.elementTypeName == PrimitiveType.PrimitiveTypeName.DOUBLE) {
            rec.add(this.fieldIndex, name, doubles.toDoubleArray(), true);
            doubles.clear();
        } else if (this.elementTypeName == PrimitiveType.PrimitiveTypeName.INT64) {
            rec.add(this.fieldIndex, name, longs.toLongArray(), true);
            longs.clear();
        } else
            throw new IllegalArgumentException("Unsupported primitive type: " + this.elementTypeName);
    }
}