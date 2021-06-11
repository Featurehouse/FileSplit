package org.featurehouse.ioutils.filesplit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Main {
    /**
     * [0]: enum {encode, decode} <br>
     * [1]: filename <br>
     * encode: --size max-one-file-size (example: 512 2G 8.6M)
     *         --output path/to/output/directory
     * decode: --github|--gitee repo:branch(default=master):path/to/directory
     */
    public static void main(String[] args) {
        if (args.length < 2) help();
        CodecStatus codecStatus = readCodec(args[0]);
        if (codecStatus == null) help();
        @SuppressWarnings("all")
        @Nullable File file = file(args[1], codecStatus);
        if (file == null) {
            System.out.println("ERROR: " + args[1] + "is not a correct " + codecStatus.getPreFileType() + '!');
            System.exit(1);
        }

    }

    private static void help() {
        System.out.println("Usage: java -jar FileSpliterator.jar <encode|decode> <filename> [args]\n" +
                "encode: arg: filename\n" +
                "decode: arg: directory name\n" +
                "more args:\n [max_one_file_size] default 99.4MB\n" +
                "[directory name] default filesplit-<origin_filename>");
        System.exit(0);
    }

    private static CodecStatus readCodec(String string) {
        if ("encode".equalsIgnoreCase(string)) return CodecStatus.ENCODE;
        if ("decode".equalsIgnoreCase(string)) return CodecStatus.DECODE;
        return null;
    }

    private static @Nullable File file(String string, @NotNull CodecStatus codecStatus) {
        File file = new File(string);
        if (codecStatus == CodecStatus.ENCODE)
            return file.isFile() ? file : null;
        else
            return file.isDirectory() ? file : null;
    }
}
