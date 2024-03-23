package utils;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    public static String readData(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);
        return new String(body);
    }
}
