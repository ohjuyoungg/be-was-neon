package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    // 클라이언트 요청을 처리하는 메서드
    public void run() {
        // 클라이언트 연결 정보 출력
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line = br.readLine();
            String[] tokens = line.split(" ");

            // 요청 메시지 출력
            while (!"".equals(line)) {
                System.out.println(line);
                line = br.readLine();
            }

            log.info(Arrays.toString(tokens));
            String url = tokens[1];

            // 요청된 파일을 읽어들이고 적절한 Content-Type 설정 후 응답
            File file = new File("src/main/resources/static" + url);

            if (file.exists() && !file.isDirectory()) {
                byte[] body = Files.readAllBytes(file.toPath());
                String contentType = getContentType(url);
                sendResponse(out, body, contentType);
            } else {
                // 파일이 존재하지 않는 경우 404 응답 반환
                log.error("File not found: " + url);
                send404NotFound(out);
            }

        } catch (IOException e) {
            log.error("Error handling request: " + e.getMessage());
        }
    }

    // 파일 확장자에 따라 Content-Type을 결정하는 메서드
    private String getContentType(String url) {
        String[] parts = url.split("\\.");
        String extension = parts[parts.length - 1];
        switch (extension) {
            case "html":
                return "text/html;charset=utf-8";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml"; // SVG 파일에 대한 Content-Type 추가
            default:
                return "application/octet-stream";
        }
    }

    // HTTP 응답을 전송하는 메서드
    private void sendResponse(OutputStream out, byte[] body, String contentType) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: " + contentType + "\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    // 404 Not Found 응답을 전송하는 메서드
    private void send404NotFound(OutputStream out) throws IOException {
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
