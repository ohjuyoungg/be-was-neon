package utils;

import java.io.*;

public class FileUtil {

    public static byte[] toByteArray(File file) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            int bytesRead;
            while ((bytesRead = in.read(bytes, 0, bytes.length)) != -1) {
                buffer.write(bytes, 0, bytesRead);
            }
        }
        return buffer.toByteArray();
    }
}
