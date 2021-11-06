package org.featurehouse.ioutils.filesplit;

import org.featurehouse.ioutils.filesplit.info.FileSplitInfo;

public interface Constants {
    String VERSION = "1.0.0-beta.1";
    String HELP_MESSAGES =
            "\nUsages: java -jar FileSplit-" + VERSION + ".jar\n" +
            "\tencode <path/to/source-file-or-directory> [--size=<size>]" +
            " [--output=path/to/output-directory] [--archive-type=<archive-type>]\n" +
            "\t\tSize: x (512) / xB (512B) / xK (51.2K) / xM (99.4M, default) /" +
            " xG (1.05G)\n\t\tPossible archive types:\n\t\t\t" +

            "- none (source file cannot be directory)\n\t\t\t" +
            "- zip\n\t\t\t- 7z\n\t\t\t- tar\n\t\t\t- tar-gz\n\t\t\t- gz" +

            "\n\tdecode <path/to/source-file-or-directory>" +
            " [--output=path/to/parent-directory-of-output-file" +
            "-or-directory]";
    int COMMON_HEADER = 0x49a73b19;
    int INFO_HEADER = 0x49a73b1a;
    byte INFO_VERSION = 3;
    // Default: 99.4M
    long DEFAULT_SUBFILE_SIZE = 98566144L;

}
