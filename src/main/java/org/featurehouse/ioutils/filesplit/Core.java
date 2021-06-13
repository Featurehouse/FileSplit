package org.featurehouse.ioutils.filesplit;

import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.IntPredicate;

import static org.featurehouse.ioutils.filesplit.ByteHelper.fromInt;
import static org.featurehouse.ioutils.filesplit.ByteHelper.toInt;

public class Core {
    /**
     * @param file can be file or directory. Zip if directory.
     */
    public static void encode(File file, int maxOneFileSize, Path outputDirectory)
            throws Throwable {
        System.out.println("Try encoding...");
        File outputDirectoryFile = outputDirectory.toFile();
        if (outputDirectoryFile.isDirectory()) {
            throw new FileAlreadyExistsException("Directory " + outputDirectoryFile.getAbsolutePath() + " already exists!");
        } else if (!outputDirectoryFile.mkdirs())
            throw new IOException("Cannot create directory tree: " + outputDirectoryFile.getAbsolutePath());

        final boolean inputDir = file.isDirectory();
        final File originFileBck = file;
        if (inputDir) {
            File tmpFile = File.createTempFile("{" + UUID.randomUUID() + '}', "zip");
            tmpFile.deleteOnExit();
            ZipUtil.pack(file, tmpFile);
            file = tmpFile;
        }

        int i = 0;
        OutputStream fileOutputStream;
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        while (inputStream.available() != 0) {
            //bytes = ByteHelper.readNBytes(inputStream, maxOneFileSize - 4);
            //if (bytes.length == 0) break;

            File outputOneFile = //new File(outputDirectory + '/' + i + ".fsplit");
                    outputDirectory.resolve(i + ".fsplit").toFile();
            if (!outputOneFile.createNewFile()) {
                throw new IOException("cannot create new file: " + outputOneFile.getName());
            }

            fileOutputStream = new BufferedOutputStream(new FileOutputStream(outputOneFile));
            fileOutputStream.write(fromInt(Main.COMMON_HEADER));
            for (int t = 0; t < maxOneFileSize - 4; ++t) {
                int b = inputStream.read();
                if (b < 0) break;
                fileOutputStream.write(b);
            }
            //fileOutputStream.write(fromInt(Main.COMMON_HEADER));
            //fileOutputStream.write(bytes);
            fileOutputStream.close();
            ++i;
        }
        inputStream.close();
        fileOutputStream = new BufferedOutputStream(new FileOutputStream(outputDirectory.resolve("INFO.fsplitinfo").toFile()));
        fileOutputStream.write(fromInt(Main.INFO_HEADER));                      // Magic number 4
        fileOutputStream.write(Main.INFO_VERSION);                              // fsplitinfo version 1
        fileOutputStream.write(fromInt(i));                                     // Split file 4
        ByteHelper.writeString(fileOutputStream, originFileBck.getName());    // Filename 4+*

        fileOutputStream.write(inputDir ? 1 : 0);                               // If Use Zipped Directory 1
        // TODO: support gzipped file


        fileOutputStream.close();

        System.out.println("Process finished. Successfully created directory " + outputDirectoryFile.getAbsolutePath());
    }

    public static void decode(File file) throws Throwable {
        System.out.println("Try decoding...");
        String directoryWithSlash = file.getAbsolutePath() + '/';
        byte[] cache = new byte[4];
        int iCache;

        InputStream inputStream = new BufferedInputStream(new FileInputStream(directoryWithSlash + "INFO.fsplitinfo"));
        //Fsplitinfo fsplitinfo = ByteFileExecutorKt.infoFromStream(inputStream);
        iCache = inputStream.read(cache, 0, 4);
        if (iCache != 4 || toInt(cache) != Main.INFO_HEADER) throw new IllegalArgumentException("Not a valid INFO file");
        int fileSplitVersion = art(inputStream.read(), i -> i < 1);
        if (fileSplitVersion > Main.INFO_VERSION) throw new
                UnsupportedOperationException(String.format("INFO version too high: 0x%x. Greater than supported (0x%x).", fileSplitVersion, Main.INFO_VERSION));
        art(inputStream.read(cache, 0, 4), i -> i != 4);
        int maxFileCount = toInt(cache);
        String newFileName = ByteHelper.readString(inputStream);

        boolean unzipDirectory = false;
        if (fileSplitVersion >= 2) {
            iCache = inputStream.read();
            if (iCache == 1) unzipDirectory = true;
            else if (iCache != 0) throw new IllegalArgumentException("Not a valid INFO file");
        }


        //String newFilePath = file.getParent() + '/' + newFileName;
        File newFile;
        if (unzipDirectory) {
            newFile = File.createTempFile("{" + UUID.randomUUID() + '}', "zip");
            newFile.deleteOnExit();
        } else {
            newFile = new File(file.getParentFile(), newFileName);
            if (newFile.exists())
                throw new FileAlreadyExistsException(newFileName);
            if (!newFile.createNewFile())
                throw new IOException("Cannot create new file: " + newFileName);
        }

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFile));

        int iCache2;
        for (iCache = 0; iCache < maxFileCount; ++iCache) {
            //inputStream = new BufferedInputStream(new FileInputStream(file.getName() + '/' + iCache + ".fsplit"));
            inputStream = new BufferedInputStream(new FileInputStream(new File(file, iCache + ".fsplit")));
            /*if (iCache2 != 4 || toInt(cache) != VersionKt.fsplitHeader) {
                throw new InvalidFileException(iCache + ".fsplit", 0x00000002);
            }*/
            art(inputStream.read(cache, 0, 4), i -> i != 4 || toInt(cache) != Main.COMMON_HEADER);
            //cache = ByteHelper.readNBytes(inputStream, Integer.MAX_VALUE);  // SEE InputStream#readAllBytes()
            //outputStream.write(cache);
            while ((iCache2 = inputStream.read()) >= 0) {
                outputStream.write(iCache2);
            } inputStream.close();
        }
        outputStream.close();

        if (unzipDirectory) {
            File targetDirectory = new File(file.getParentFile(), newFileName);
            if (!targetDirectory.mkdir()) {
                throw new IOException("Cannot create file " + targetDirectory.getAbsolutePath());
            }
            ZipUtil.unpack(newFile, targetDirectory);
            newFile = targetDirectory;
        }

        System.out.println("Successfully decode file at " + newFile.getAbsolutePath());
    }

    /**
     * @throws IllegalArgumentException if matches {@code con}.
     * @return o if does not match {@code con}
     * @param con if matches, throw {@link IllegalArgumentException}
     */
    static int art(int o, IntPredicate con) throws IllegalArgumentException {
        if (con.test(o)) throw new IllegalArgumentException("Not a valid INFO file");
        return o;
    }

}
