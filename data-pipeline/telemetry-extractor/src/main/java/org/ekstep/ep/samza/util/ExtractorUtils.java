package org.ekstep.ep.samza.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.*;

public class ExtractorUtils {

    public static byte[] decompress(byte[] compressedData) throws Exception{
        Inflater decompressor = new Inflater();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
        decompressor.setInput(compressedData);
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            int count = decompressor.inflate(buf);
            bos.write(buf, 0, count);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static byte [] compress(byte[] data) throws Exception{
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }
}
