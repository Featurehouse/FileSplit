package org.featurehouse.ioutils.filesplit.info;

import org.featurehouse.ioutils.filesplit.internal.ByteHelper;
import org.featurehouse.ioutils.filesplit.internal.Internal;

public class InvalidFileSplitInfoException extends Exception {
    public InvalidFileSplitInfoException() { super(); }
    public InvalidFileSplitInfoException(String message) { super(message); }
    public InvalidFileSplitInfoException(Throwable cause) { super(cause); }
    public InvalidFileSplitInfoException(String message, Throwable cause) { super(message, cause); }

    @Internal
    static InvalidFileSplitInfoException version(byte[] b) {
        return new InvalidFileSplitInfoException("Invalid file header: "
                + Integer.toHexString(ByteHelper.toInt(b)));
    }

    static InvalidFileSplitInfoException maxFileCount(byte[] b) {
        return new InvalidFileSplitInfoException("Invalid max file count: "
                + ByteHelper.toInt(b));
    }

    static InvalidFileSplitInfoException missingInfoFactory(int version) {
        return new InvalidFileSplitInfoException("Missing file split info factory" +
                " for version " + version + '.');
    }
}
