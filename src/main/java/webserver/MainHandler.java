package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import db.Database;
import model.HttpMethod;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileSerializer;
import utils.HttpRequestUtils;
import webserver.httpMessage.HttpRequest;
import webserver.httpMessage.HttpResponse;


public class MainHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MainHandler.class);
    private final Socket connection;
    public static final String STATIC_PATH = "src/main/resources/static";


    public MainHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (
                InputStream in = connection.getInputStream();
                OutputStream out = connection.getOutputStream()
        ) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequest httpRequest = HttpRequestUtils.parse(br);
            HttpResponse httpResponse = new HttpResponse();

            handlerMapping(
                    httpRequest.getRequestLine(),
                    httpResponse,
                    out
            );

        } catch (IOException e) {
            log.error("Error handling request: {}", e.getMessage());
        }
    }

    private void handlerMapping(
            HttpRequest.RequestLine requestLine,
            HttpResponse response,
            OutputStream out
    ) throws IOException {
        HttpMethod method = requestLine.getMethod();
        String path = requestLine.getPath();
        Map<String, String> params = requestLine.getParams();

        if (method == HttpMethod.GET) {
            File file = new File(STATIC_PATH + path);
            if (file.exists() && !file.isDirectory()) {
                byte[] body = FileSerializer.toByteArray(file);
                String type = ContentType.getContentType(path);
                response.response200Header(out, body, type);
            } else {
                log.error("File not found: " + path);
                response.send404NotFound(out);
            }
        } else if (method == HttpMethod.POST && path.equals("/create")) {
            User user = HttpRequest.buildUser(params);
            Database.addUser(user);
            response.response302Header(out);
        } else {
            // UNREACHABLE
            log.warn(String.format("method=%s path=%s 는 처리할 수 없는 요청입니다.", method, path));
            response.send404NotFound(out);
        }
    }
}
