package org.featurehouse.ioutils.filesplit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Deprecated
public class Main {
    /**
     * [0]: enum {encode, decode} <br>
     * [1]: filename <br>
     * encode: --size max-one-file-size (example: 512 2G 8.6M) <br>
     *         --output path/to/output/directory <br>
     * <s>decode: --github|--gitee repo:branch(default=master):path/to/directory</s>
     */
    public static void main(String[] args) {
        long l = System.nanoTime();

        if (args.length < 2) help();
        CodecStatus codecStatus = readCodec(args[0]);
        if (codecStatus == null) help();
        @SuppressWarnings("all")
        @Nullable File file = file(args[1], codecStatus);
        if (file == null) {
            System.out.println("ERROR: " + args[1] + "is not a correct " + codecStatus.getPreFileType() + '!');
            System.exit(1);
        }
        if (codecStatus == CodecStatus.DECODE) {
            try {
                Core.decode(file);
                System.out.printf("Process finished in %.2fms.\n", (System.nanoTime() - l) / 1e6);
            } catch (Throwable t) {
                System.gc();
                System.err.println("An exception has occurred");
                t.printStackTrace();
            }
            return;
        } // else ENCODE
        int maxOneFileSize = FILE_SIZE_DEFAULT;
        Path outputDirectory = Paths.get(System.getProperty("user.home"), "filesplit-" + file.getName());

        for (int i = 2; i < args.length; ++i) {
            if (args[i].startsWith("--")) {
                if (args[i].equalsIgnoreCase("--size"))
                    maxOneFileSize = maxOneFileSize(args[++i]);
                else if (args[i].equalsIgnoreCase("--output"))
                    outputDirectory = Paths.get(args[++i]);
            }
        }

        try {
            Core.encode(file, maxOneFileSize, outputDirectory);
            System.out.printf("Process finished in %.2fms.\n", (System.nanoTime() - l) / 1e6);
        } catch (Throwable t) {
            System.gc();
            System.err.println("An exception has occurred");
            t.printStackTrace();
        }
    }

    protected static final int FILE_SIZE_DEFAULT = 98566144;

    private static void help() {
        System.out.println("Usage: java -jar FileSplit-" + Constants.VERSION + "-.jar <encode|decode> <filename> [args]\n" +
                "encode: arg: filename\n" +
                "decode: arg: directory name\n" +
                "more args:\n [max_one_file_size] default 99.4MB\n" +
                "[directory name] default filesplit-<origin_filename>");
        System.exit(0);
    }

    public static int maxOneFileSize(String size) {
        return (int) maxOneFileSize0(size);
    }

    private static float maxOneFileSize0(String string) {
        if (string.endsWith("B")) string = string.substring(0, string.length() - 1);

        final String copy = string;
        try {
            if (string.endsWith("K")) {
                string = string.substring(0, string.length() - 1);
                return Float.parseFloat(string) * 1024;
            } else if (string.endsWith("M")) {
                string = string.substring(0, string.length() - 1);
                return Float.parseFloat(string) * (1024 * 1024);
            } else if (string.endsWith("G")) {
                string = string.substring(0, string.length() - 1);
                return Float.parseFloat(string) * (1024 * 1024 * 1024);
            } return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            System.err.println("Invalid file size: " + copy
                + ". Will be set to default.");
            return FILE_SIZE_DEFAULT;
        }
    }

    private static CodecStatus readCodec(String string) {
        if ("encode".equalsIgnoreCase(string)) return CodecStatus.ENCODE;
        if ("decode".equalsIgnoreCase(string)) return CodecStatus.DECODE;
        return null;
    }

    private static @Nullable File file(String string, @Nonnull CodecStatus codecStatus) {
        File file = new File(string);
        if (codecStatus == CodecStatus.ENCODE)
            return file.exists() ? file : null;
        else
            return file.isDirectory() ? file : null;
    }
}
