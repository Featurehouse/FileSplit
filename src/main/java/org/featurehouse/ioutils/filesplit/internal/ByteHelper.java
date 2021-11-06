package org.featurehouse.ioutils.filesplit.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.IntPredicate;

@Internal
public final class ByteHelper {
    public static byte[] fromInt(int i) {
        return new byte[] {
                (byte) (i >> 24),
                (byte) (i >> 16),
                (byte) (i >> 8),
                (byte)  i
        };
    }

    public static int toInt(byte[] bs) {
        if (bs.length < 4) return -1;
        int[] intArray = new int[4];
        for (int i = 0; i < 4; ++i) {
            if (bs[i] < 0) intArray[i] = 256 + bs[i];
            else intArray[i] = bs[i];
        }
        return  (intArray[0] << 24) +
                (intArray[1] << 16) +
                (intArray[2] << 8)  +
                 intArray[3];
    }

    public static void writeString(OutputStream instance, String s) throws IOException {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        instance.write(fromInt(b.length));
        instance.write(b);
    }

    public static String readString(InputStream instance)
            throws IOException, IllegalArgumentException {
        byte[] b = new byte[4];
        art(instance.read(b, 0, 4), i -> i != 4);
        int l = toInt(b);   // len
        b = new byte[l];
        art(instance.read(b, 0, l), i -> i != l);
        return new String(b, StandardCharsets.UTF_8);
    }

    private ByteHelper() {}

    /**
     * @throws IllegalArgumentException if matches {@code con}.
     * @return o if it does not match {@code con}
     * @param con if matches, throw {@link IllegalArgumentException}
     */
    public static int art(int o, IntPredicate con) throws IllegalArgumentException {
        if (con.test(o)) throw new IllegalArgumentException("Not a valid INFO file");
        return o;
    }
}
