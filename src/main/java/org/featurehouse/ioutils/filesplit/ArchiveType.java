package org.featurehouse.ioutils.filesplit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface ArchiveType {
    FileProcessor fileCompressor();

    PathProcessor pathCompressor();

    FileProcessor fileDecompressor();

    PathProcessor pathDecompressor();

    default boolean shouldCopy() {
        return true;
    }

    default boolean acceptDirectory() {
        return true;
    }

    @FunctionalInterface
    interface FileProcessor {
        void process(File source, File target) throws IOException;
    }

    @FunctionalInterface
    interface PathProcessor {
        void process(Path source, Path target) throws IOException;
    }
}
