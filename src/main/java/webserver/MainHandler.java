package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpMessage.HttpRequest;
import webserver.httpMessage.HttpResponse;


public class MainHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MainHandler.class);
    private final Socket connection;
    private static String url;

    public MainHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequest httpRequest = new HttpRequest(readRequestLog(br));
            httpRequest.registrationParamValue();
            HttpResponse httpResponse = new HttpResponse();

            // 요청된 파일을 읽어들이고 적절한 Content-Type 설정 후 응답
            File file = new File("src/main/resources/static" + url);

            if (file.exists() && !file.isDirectory()) {
                byte[] body = httpResponse.readFileContents(file);
                ContentType contentType = new ContentType();
                String type = contentType.getContentType(url);
                httpResponse.sendResponse(out, body, type);
            } else {
                // 파일이 존재하지 않는 경우 404 응답 반환
                log.error("File not found: " + url);
                httpResponse.send404NotFound(out);
            }
        } catch (IOException e) {
            log.error("Error handling request: {}", e.getMessage());
        }
    }

    private String readRequestLog(BufferedReader br) throws IOException {
        String line = br.readLine();
        String[] tokens = line.split(" ");

        while (!"".equals(line)) {
            log.debug(line);
            line = br.readLine();
        }

        log.info(Arrays.toString(tokens));
        url = tokens[1];

        return url;
    }
}
