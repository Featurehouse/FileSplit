package org.featurehouse.ioutils.filesplit.info;

import org.featurehouse.ioutils.filesplit.ArchiveType;
import org.featurehouse.ioutils.filesplit.internal.Archivers;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class FileSplitInfo {
    private final int maxFileCount;
    private final String filename;
    private final ArchiveType archiveType;

    public FileSplitInfo(int maxFileCount,
                         String filename,
                         ArchiveType archiveType) {
        this.maxFileCount = maxFileCount;
        this.filename = filename;
        this.archiveType = archiveType;
    }

    public enum DefaultArchiveTypes implements ArchiveType {
        NOP(0, "none", Archivers::copy, Archivers::copy, Archivers::copy, Archivers::copy),
        ZIP(1, "zip", Archivers::compressZip, Archivers::compressZip,
                Archivers::decompressZip, Archivers::decompressZip),
        SEVEN_Z(2, "7z", Archivers::compress7Z, Archivers::compress7Z,
                Archivers::decompress7Z, Archivers::decompress7Z),
        TAR(3, "tar", Archivers::compressTar, Archivers::compressTar,
                Archivers::decompressTar, Archivers::decompressTar),
        TGZ(4, "tar-gz", Archivers::compressTgz, Archivers::compressTgz,
                Archivers::decompressTgz, Archivers::decompressTgz),
        GZIP(5, "gz", Archivers::compressGzip, Archivers::compressGzip,
                Archivers::decompressGzip, Archivers::decompressGzip);

        private final FileProcessor fileCompressor;
        private final PathProcessor pathCompressor;
        private final FileProcessor fileDecompressor;
        private final PathProcessor pathDecompressor;

        private final byte id;
        private final String name;

        DefaultArchiveTypes(int id,
                            String name,
                            FileProcessor fileCompressor,
                            PathProcessor pathCompressor,
                            FileProcessor fileDecompressor,
                            PathProcessor pathDecompressor) {
            this.id = (byte) id;
            this.name = name;
            this.fileCompressor = fileCompressor;
            this.pathCompressor = pathCompressor;
            this.fileDecompressor = fileDecompressor;
            this.pathDecompressor = pathDecompressor;
        }

        private static final Map<Byte, DefaultArchiveTypes> ID2MAP =
                Arrays.stream(DefaultArchiveTypes.values())
                        .collect(Collectors.toUnmodifiableMap(
                                DefaultArchiveTypes::getId, UnaryOperator.identity()
                        ));
        private static final Map<String, DefaultArchiveTypes> NAME2MAP =
                Arrays.stream(DefaultArchiveTypes.values())
                        .collect(Collectors.toUnmodifiableMap(
                                DefaultArchiveTypes::getName, UnaryOperator.identity()
                        ));
        public static DefaultArchiveTypes fromId(byte id) {
            return ID2MAP.get(id);
        }

        public static DefaultArchiveTypes fromId(int id) {
            return fromId((byte) id);
        }

        public static Optional<DefaultArchiveTypes> fromName(String name) {
            return Optional.ofNullable(NAME2MAP.get(name));
        }

        @Override
        public FileProcessor fileCompressor() {
            return fileCompressor;
        }

        @Override
        public PathProcessor pathCompressor() {
            return pathCompressor;
        }

        @Override
        public FileProcessor fileDecompressor() {
            return fileDecompressor;
        }

        @Override
        public PathProcessor pathDecompressor() {
            return pathDecompressor;
        }

        @Override
        public boolean shouldCopy() {
            return this != NOP;
        }

        @Override
        public boolean acceptDirectory() {
            return this != NOP;
        }

        public byte getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public int maxFileCount() {
        return maxFileCount;
    }

    public String filename() {
        return filename;
    }

    public ArchiveType archiveType() {
        return archiveType;
    }

    public static FileSplitInfo of(int maxFileCount,
                                   String filename,
                                   ArchiveType archiveType) {
        return new FileSplitInfo(maxFileCount, filename, archiveType);
    }
}
