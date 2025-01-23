package benchmark.optimized;

import benchmark.optimized.converters.RecordConverter;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.api.InitContext;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.api.*;
import org.apache.parquet.schema.MessageType;

import java.util.Map;

public class OptimizedReadSupport extends ReadSupport<RecordConverter.Record> {

    @Override
    public ReadContext init(InitContext context) {
        MessageType fileSchema = context.getFileSchema();
        return new ReadContext(fileSchema);
    }

    @Override
    public RecordMaterializer<RecordConverter.Record> prepareForRead(
            Configuration configuration,
            Map<String, String> keyValueMetaData,
            MessageType fileSchema,
            ReadContext readContext) {
        return new SimpleRecordMaterializer(fileSchema);
    }
}