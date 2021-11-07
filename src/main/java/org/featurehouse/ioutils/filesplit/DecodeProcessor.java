package org.featurehouse.ioutils.filesplit;

import org.featurehouse.ioutils.filesplit.info.FileSplitInfo;
import org.featurehouse.ioutils.filesplit.info.FileSplitInfoFactory;
import org.featurehouse.ioutils.filesplit.info.InvalidFileSplitInfoException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class DecodeProcessor implements Runnable {
    private final Path source;
    private final Path outputRoot;
    private final PrintStream sysOut, sysErr;

    protected DecodeProcessor(Path source, Path outputRoot, boolean enableLogging) {
        this.source = source;
        this.outputRoot = outputRoot;
        if (!enableLogging) {
            sysErr = sysOut = new PrintStream(OutputStream.nullOutputStream());
        } else {
            sysErr = System.err;
            sysOut = System.out;
        }
    }

    @Override
    public void run() throws RuntimeException {
        sysOut.println("Try decoding...");
        Path infoFile = source.resolve("INFO.fsplitinfo");
        InputStream inputStream;
        FileSplitInfo info;
        try {
            inputStream = Files.newInputStream(infoFile);
            info = FileSplitInfoFactory.readInfo(inputStream);

            ArchiveType archiveType = info.archiveType();
            if (!archiveType.shouldCopy()) {
                directlyDecode(info);
            } else {

            }
        } catch (InvalidFileSplitInfoException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void directlyDecode(FileSplitInfo info) {
        
    }

    public Path source() {
        return source;
    }

    public Path outputRoot() {
        return outputRoot;
    }

    public static DecodeProcessor logEnabled(Path source, Path outputRoot) {
        return new DecodeProcessor(source, outputRoot, true);
    }

    public static DecodeProcessor logDisabled(Path source, Path outputRoot) {
        return new DecodeProcessor(source, outputRoot, false);
    }
}
