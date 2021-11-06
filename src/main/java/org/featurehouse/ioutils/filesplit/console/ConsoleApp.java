package org.featurehouse.ioutils.filesplit.console;

import org.featurehouse.ioutils.filesplit.*;
import org.featurehouse.ioutils.filesplit.info.FileSplitInfo;

import java.nio.file.Path;
import java.util.Locale;

public class ConsoleApp {
    public static void main(String[] args) {
        long l = System.nanoTime();
        if (args.length < 2) help();
        Runnable runnable;
        switch (args[0]) {
            case "encode":
                runnable = encoder(args);
                break;
            case "decode":
                runnable = decoder(args);
                break;
            default:
                help();
                return;
        } runnable.run();
        System.out.printf("Process finished in %.2fms.\n", (System.nanoTime() - l) / 1e6);
    }

    static Runnable encoder(String[] args) {
        Path source = Path.of(args[1]);
        Path output = Path.of(System.getProperty("user.dir"),
                "filesplit-" + source.getFileName());
        long subfileSize = Constants.DEFAULT_SUBFILE_SIZE;
        ArchiveType archiveType = FileSplitInfo.DefaultArchiveTypes.ZIP;

        final var l = args.length-1;
        for (int i = 2; i < l; i++) {
            String s = args[i];
            if (s.startsWith("--")) {
                switch (s.substring(2).toLowerCase(Locale.ROOT)) {
                    case "size":
                        subfileSize = maxOneFileSize(args[++i]);
                        break;
                    case "output":
                        output = Path.of(args[++i]);
                        break;
                    case "archive-type":
                        var s2 = args[++i];
                        archiveType = FileSplitInfo.DefaultArchiveTypes.fromName(s2)
                                .orElseGet(() -> {
                                    System.err.println("Missing archive type: " + s2 +
                                            ". Will be set to default.");
                                    return Constants2.DEFAULT_ARCHIVE_TYPE;
                                });
                        break;
                }
            }

        } return new EncodeProcessor(source, output, subfileSize, archiveType);
    }

    static Runnable decoder(String[] args) {
        Path source = Path.of(args[1]);
        Path outputRoot = Path.of(System.getProperty("user.dir"));
        var l = args.length-1;
        for (int i = 2; i < l; i++) {

            // overwrite them if add arguments
            String s = args[i];
            if (s.equalsIgnoreCase("--size")) {
                outputRoot = Path.of(args[++i]);
            }

        }
        return new DecodeProcessor(source, outputRoot);
    }

    public static long maxOneFileSize(String size) {
        return maxOneFileSize0(size).longValue();
    }

    private static Number maxOneFileSize0(String string) {
        final String copy = string;
        try {
            if (string.endsWith("B"))
                return Long.parseLong(string, 0, string.length()-1, 10);
            if (string.endsWith("K")) {
                string = string.substring(0, string.length() - 1);
                return Double.parseDouble(string) * 1024;
            } else if (string.endsWith("M")) {
                string = string.substring(0, string.length() - 1);
                return Double.parseDouble(string) * (1024 * 1024);
            } else if (string.endsWith("G")) {
                string = string.substring(0, string.length() - 1);
                return Double.parseDouble(string) * (1024 * 1024 * 1024);
            } return Long.parseLong(string);
        } catch (NumberFormatException e) {
            System.err.println("Invalid file size: " + copy
                    + ". Will be set to default.");
            return Constants.DEFAULT_SUBFILE_SIZE;
        }
    }

    private static void help() {
        System.out.println(Constants.HELP_MESSAGES);
        System.exit(0);
    }
}
