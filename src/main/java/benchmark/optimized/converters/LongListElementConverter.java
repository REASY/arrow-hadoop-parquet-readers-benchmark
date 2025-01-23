package benchmark.optimized.converters;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.PrimitiveConverter;

class LongListElementConverter extends GroupConverter {
    private final PrimitiveConverter primitiveConverter;

    LongListElementConverter(LongArrayList currentList) {
        this.primitiveConverter = new PrimitiveConverter() {
            @Override
            public void addLong(long value) {
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