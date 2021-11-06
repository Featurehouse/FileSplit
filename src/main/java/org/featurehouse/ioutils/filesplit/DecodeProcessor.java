package org.featurehouse.ioutils.filesplit;

import java.nio.file.Path;

public class DecodeProcessor implements Runnable {
    private final Path source;
    private final Path outputRoot;

    public DecodeProcessor(Path source, Path outputRoot) {
        this.source = source;
        this.outputRoot = outputRoot;
    }

    @Override
    public void run() {

    }

    public Path source() {
        return source;
    }

    public Path outputRoot() {
        return outputRoot;
    }
}
