package org.featurehouse.ioutils.filesplit.info;

import org.featurehouse.ioutils.filesplit.internal.ByteHelper;

import java.io.IOException;
import java.io.InputStream;

public class V1InfoFactory implements FileSplitInfoFactory {
    @Override
    public boolean matches(int version) {
        return version == 1;
    }

    @Override
    public FileSplitInfo fromInputStream(InputStream inputStream)
            throws IOException, InvalidFileSplitInfoException {
        byte[] cache = new byte[4];
        if (inputStream.read(cache, 0, 4) < 4)
            throw InvalidFileSplitInfoException.maxFileCount(cache);
        int maxFileCount = ByteHelper.toInt(cache);
        String filename = ByteHelper.readString(inputStream);
        return FileSplitInfo.of(maxFileCount, filename, FileSplitInfo.DefaultArchiveTypes.NOP);
    }
}
