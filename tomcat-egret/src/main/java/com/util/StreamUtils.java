package com.util;

import java.io.*;

/**
 * @author：rkc
 * @date：Created in 2021/4/26 18:30
 * @description：
 */
public class StreamUtils {

    public static byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        buffer = baos.toByteArray();
        baos.close();
        bis.close();
        return buffer;
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String str = reader.readLine();
        while (str != null && !str.isEmpty()) {
            sb.append(str).append("\r\n");
            str = reader.readLine();
        }
        reader.close();
        inputStream.close();
        return sb.toString();
    }
}
