package org.featurehouse.ioutils.filesplit;

@Deprecated
public enum CodecStatus {
    ENCODE("file"),
    DECODE("directory");

    private final String preFileType;

    CodecStatus(String preFileType) {
        this.preFileType = preFileType;
    }

    public String getPreFileType() {
        return preFileType;
    }
}
