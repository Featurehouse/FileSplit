package org.featurehouse.ioutils.filesplit.internal;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.examples.Archiver;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.UUID;

public class Archivers {
    private static final Archiver theArchiver = new Archiver();

    public static void compressTgz(File source, File target) throws IOException {
        File tmpFile = createTempFile();
        compressTar(source, tmpFile);
        compressGzip(tmpFile, target);
    }

    public static void compressTgz(Path source, Path target) throws IOException {
        Path tmpFile = createTempFile().toPath();
        compressTar(source, tmpFile);
        compressGzip(tmpFile, target);
    }

    public static void compressTar(File source, File target) throws IOException {
        try {
            theArchiver.create(new TarArchiveOutputStream(new FileOutputStream(target)), source);
        } catch (ArchiveException e) {
            throw new InternalError(e);
        }
    }

    public static void compressTar(Path source, Path target) throws IOException {
        theArchiver.create(new TarArchiveOutputStream(Files.newOutputStream(target)), source);
    }

    public static void compressGzip(File source, File target) throws IOException {
        var gzip = new GzipCompressorOutputStream(new FileOutputStream(target));
        new BufferedInputStream(new FileInputStream(source)).transferTo(gzip);
    }

    public static void compressGzip(Path source, Path target) throws IOException {
        var gzip = new GzipCompressorOutputStream(Files.newOutputStream(target));
        Files.newInputStream(source).transferTo(gzip);
    }

    public static void compress7Z(File source, File target) throws IOException {
        theArchiver.create(new SevenZOutputFile(target), source);
    }

    public static void compress7Z(Path source, Path target) throws IOException {
        theArchiver.create(new SevenZOutputFile(FileChannel.open(target)), source);
    }

    public static void compressZip(File source, File target) throws IOException {
        try {
            theArchiver.create(new ZipArchiveOutputStream(target), source);
        } catch (ArchiveException e) {
            throw new InternalError(e);
        }
    }

    public static void compressZip(Path source, Path target) throws IOException {
        theArchiver.create(new ZipArchiveOutputStream(source), target);
    }

    public static void decompressTgz(File source, File target) throws IOException {
        File tmpFile = createTempFile();
        decompressGzip(source, tmpFile);
        decompressTar(tmpFile, target);
    }

    public static void decompressTgz(Path source, Path target) throws IOException {
        Path tmpFile = createTempFile().toPath();
        decompressGzip(source, tmpFile);
        decompressTar(tmpFile, target);
    }

    public static void decompressTar(Path source, Path target) throws IOException {
        TarFile tarFile = new TarFile(source);
        for (TarArchiveEntry entry : tarFile.getEntries()) {
            Path path = target.resolve(entry.getName());
            if (!entry.isDirectory()) {
                Files.createDirectories(path.getParent());

                InputStream inputStream = tarFile.getInputStream(entry);
                OutputStream outputStream = Files.newOutputStream(path);

                inputStream.transferTo(outputStream);
                inputStream.close();
                outputStream.close();
            } else {
                Files.createDirectories(path);
            }
        }
    }

    public static void decompressTar(File source, File target) throws IOException {
        decompressTar(source.toPath(), target.toPath());
    }

    public static void decompressGzip(File source, File target) throws IOException {
        var gzip = new GzipCompressorInputStream(new FileInputStream(source));
        gzip.transferTo(new BufferedOutputStream(new FileOutputStream(target)));
    }

    public static void decompressGzip(Path source, Path target) throws IOException {
        var gzip = new GzipCompressorInputStream(Files.newInputStream(source));
        gzip.transferTo(Files.newOutputStream(target));
    }

    public static void decompress7Z(Path source, Path target) throws IOException {
        SevenZFile sevenZFile = new SevenZFile(FileChannel.open(source));
        for (SevenZArchiveEntry entry : sevenZFile.getEntries()) {
            Path path = target.resolve(entry.getName());
            if (!entry.isDirectory()) {
                Files.createDirectories(path.getParent());

                InputStream inputStream = sevenZFile.getInputStream(entry);
                OutputStream outputStream = Files.newOutputStream(path);

                inputStream.transferTo(outputStream);
                inputStream.close();
                outputStream.close();
            } else {
                Files.createDirectories(path);
            }
        }
    }

    public static void decompress7Z(File source, File target) throws IOException {
        decompress7Z(source.toPath(), target.toPath());
    }

    public static void decompressZip(Path source, Path target) throws IOException {
        ZipFile zipFile = new ZipFile(FileChannel.open(source));
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            Path path = target.resolve(entry.getName());
            if (!entry.isDirectory()) {
                Files.createDirectories(path.getParent());

                InputStream inputStream = zipFile.getInputStream(entry);
                OutputStream outputStream = Files.newOutputStream(path);

                inputStream.transferTo(outputStream);
                inputStream.close();
                outputStream.close();
            } else {
                Files.createDirectories(path);
            }
        }
    }

    public static void decompressZip(File source, File target) throws IOException {
        decompressZip(source.toPath(), target.toPath());
    }

    public static void copy(Path source, Path target) throws IOException {
        Files.copy(source, target);
    }

    public static void copy(File source, File target) throws IOException {
        copy(source.toPath(), target.toPath());
    }

    private static volatile Object createTempDirectoryLock = new Object();
    private static synchronized void checkTmpDirectoryLock() throws IOException {
        if (createTempDirectoryLock != null) {
            Files.createTempDirectory("fsplit");
            createTempDirectoryLock = null;
        }
    }

    private static File createTempFile() throws IOException {
        checkTmpDirectoryLock();

        File f = File.createTempFile("fsplit/{" + UUID.randomUUID() + '}',
                null);
        f.deleteOnExit();
        return f;
    }

}
