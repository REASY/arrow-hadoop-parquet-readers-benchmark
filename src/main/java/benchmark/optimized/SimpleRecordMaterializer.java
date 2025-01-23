package benchmark.optimized;

import benchmark.optimized.converters.RecordConverter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.schema.MessageType;

class SimpleRecordMaterializer extends RecordMaterializer<RecordConverter.Record> {
    public final RecordConverter root;

    public SimpleRecordMaterializer(MessageType schema) {
        this.root = new RecordConverter(schema);
    }

    @Override
    public RecordConverter.Record getCurrentRecord() {
        return root.getCurrentRecord();
    }

    @Override
    public GroupConverter getRootConverter() {
        return root;
    }
}
