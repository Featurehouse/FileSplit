package org.featurehouse.ioutils.filesplit.info;

import org.featurehouse.ioutils.filesplit.Constants;
import org.featurehouse.ioutils.filesplit.internal.ByteHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ServiceLoader;

public interface FileSplitInfoFactory {
    boolean matches(int version);

    FileSplitInfo fromInputStream(InputStream restOfInputStream)
            throws IOException, InvalidFileSplitInfoException;

    static int consumeVersion(InputStream inputStream)
        throws InvalidFileSplitInfoException, IOException {

        byte[] cache = new byte[4];
        if (inputStream.read(cache, 0, 4) != 4 || ByteHelper.toInt(cache) != Constants.INFO_HEADER)
            throw InvalidFileSplitInfoException.version(cache);
        return inputStream.read();
    }

    static FileSplitInfo readInfo(InputStream inputStream)
        throws InvalidFileSplitInfoException, IOException {

        int version = consumeVersion(inputStream);
        var factory = ServiceLoader.load(FileSplitInfoFactory.class)
                .stream()
                .filter(provider -> provider.get().matches(version))
                .findFirst()
                .orElseThrow(() -> InvalidFileSplitInfoException.missingInfoFactory(version))
                .get();
        var ret = factory.fromInputStream(inputStream);
        inputStream.close();
        return ret;
    }
}
