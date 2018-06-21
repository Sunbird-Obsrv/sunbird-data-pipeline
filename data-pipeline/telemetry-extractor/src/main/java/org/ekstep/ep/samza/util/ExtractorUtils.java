package org.ekstep.ep.samza.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.*;

public class ExtractorUtils {

    public static byte[] decompress(byte[] compressedData) throws Exception {
        try (ByteArrayInputStream bin = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipper = new GZIPInputStream(bin)) {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int len;
            while ((len = gzipper.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzipper.close();
            out.close();
            return out.toByteArray();

        }

    }
    public static byte[] compress(byte[] data) throws Exception {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             GZIPOutputStream gzipper = new GZIPOutputStream(bout)) {
            gzipper.write(data, 0, data.length);
            gzipper.close();

            return bout.toByteArray();
        }
    }
}
