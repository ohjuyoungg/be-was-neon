package utils;

import java.io.*;

public class FileUtil {

    public static byte[] toByteArray(File file) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            int bytesRead;
            byte[] bytes = new byte[1024];
            while ((bytesRead = in.read(bytes)) != -1) {
                buffer.write(bytes, 0, bytesRead);
            }
        }
        return buffer.toByteArray();
    }
}
