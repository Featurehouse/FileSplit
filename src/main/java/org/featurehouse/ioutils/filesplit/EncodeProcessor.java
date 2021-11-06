package org.featurehouse.ioutils.filesplit;

import java.nio.file.Path;

public class EncodeProcessor implements Runnable {
    private final Path source, dest;
    private final long subfileSize;
    private final ArchiveType archiveType;

    public EncodeProcessor(Path source, Path dest, long subfileSize, ArchiveType archiveType) {
        this.source = source;
        this.dest = dest;
        this.subfileSize = subfileSize;
        this.archiveType = archiveType;
    }

    @Override
    public void run() {

    }

    public Path getSource() {
        return source;
    }

    public Path getDest() {
        return dest;
    }

    public long getSubfileSize() {
        return subfileSize;
    }

    public ArchiveType getArchiveType() {
        return archiveType;
    }
}
