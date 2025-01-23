package benchmark.optimized.converters;


import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.PrimitiveConverter;

class PrimitiveConverterWithString extends PrimitiveConverter {
    private final RecordConverter root;
    private final String name;
    private final int fieldIndex;

    PrimitiveConverterWithString(RecordConverter root, String name, int fieldIndex) {
        this.root = root;
        this.name = name;
        this.fieldIndex = fieldIndex;
    }

    @Override
    public void addBinary(Binary value) {
        root.getCurrentRecord().add(this.fieldIndex, this.name, value.toStringUsingUTF8(), false);
    }
}