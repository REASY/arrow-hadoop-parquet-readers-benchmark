package benchmark;

import org.apache.arrow.dataset.file.FileFormat;
import org.apache.arrow.dataset.file.FileSystemDatasetFactory;
import org.apache.arrow.dataset.jni.NativeDataset;
import org.apache.arrow.dataset.jni.NativeMemoryPool;
import org.apache.arrow.dataset.jni.NativeScanTask;
import org.apache.arrow.dataset.jni.NativeScanner;
import org.apache.arrow.dataset.scanner.ScanOptions;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.util.AutoCloseables;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowReader;

import java.util.*;
import java.util.function.Consumer;

public class ArrowParquetReader {
    void read(String path, Integer batchSize, Consumer<VectorSchemaRoot> processBatch) throws Exception {
        List<AutoCloseable> toFree = new ArrayList<>();

        try {
            RootAllocator allocator = new RootAllocator(Long.MAX_VALUE);
            toFree.add(allocator);

            FileSystemDatasetFactory factory = new FileSystemDatasetFactory(allocator, NativeMemoryPool.getDefault(), FileFormat.PARQUET, path);
            toFree.add(factory);

            NativeDataset ds = factory.finish();
            toFree.add(ds);

            NativeScanner scanner = ds.newScan(new ScanOptions(batchSize));
            toFree.add(scanner);

            for (NativeScanTask next : scanner.scan()) {
                try(next) {
                    try (ArrowReader reader = next.execute()) {
                        while(reader.loadNextBatch()) {
                            try(VectorSchemaRoot root = reader.getVectorSchemaRoot()) {
                                processBatch.accept(root);
                            }
                        }
                    }
                }
            }
        } finally {
            AutoCloseables.close(toFree);
        }
    }
}
