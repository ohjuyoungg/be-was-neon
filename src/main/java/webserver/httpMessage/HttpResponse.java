package webserver.httpMessage;


import java.io.*;

public class HttpResponse {

    public byte[] readFileContents(File file) throws IOException {
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


    public void sendResponse(OutputStream out, byte[] body, String contentType) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: " + contentType + "\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    // 404 Not Found 응답을 전송하는 메서드
    public void send404NotFound(OutputStream out) throws IOException {
        String errorMessage = "404 Not Found";
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + errorMessage.length() + "\r\n" +
                "\r\n" +
                errorMessage);
        dos.flush();
    }
}
