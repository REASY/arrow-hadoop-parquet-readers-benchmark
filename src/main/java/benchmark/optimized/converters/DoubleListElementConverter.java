package benchmark.optimized.converters;


import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.PrimitiveConverter;

class DoubleListElementConverter extends GroupConverter {
    private final PrimitiveConverter primitiveConverter;

    DoubleListElementConverter(DoubleArrayList currentList) {
        this.primitiveConverter = new PrimitiveConverter() {
            @Override
            public void addDouble(double value) {
                currentList.add(value);
            }
        };
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return primitiveConverter;
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }
}