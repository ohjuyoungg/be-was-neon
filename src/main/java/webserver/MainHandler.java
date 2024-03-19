package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;
import utils.IOUtils;
import webserver.httpMessage.HttpRequest;
import webserver.httpMessage.HttpResponse;


public class MainHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MainHandler.class);
    private final Socket connection;
    private static String url;
    private Map<String, String> parameters;


    public MainHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequest httpRequest = new HttpRequest(readRequestLog(br, out));
            HttpResponse httpResponse = new HttpResponse();

            httpRequest.registrationParamValue(out, parameters); // parameters 전달


            // 정적 파일 요청을 처리합니다.
            File file = new File("src/main/resources/static" + url);
            if (file.exists() && !file.isDirectory()) {
                byte[] body = httpResponse.readFileContents(file);
                ContentType contentType = new ContentType();
                String type = contentType.getContentType(url);
                httpResponse.response200Header(out, body, type);
            } else {
                // 파일이 존재하지 않는 경우 404 응답을 보냅니다.
                log.error("File not found: " + url);
                httpResponse.send404NotFound(out);
            }
        } catch (IOException e) {
            log.error("Error handling request: {}", e.getMessage());
        }
    }

    private String readRequestLog(BufferedReader br, OutputStream out) throws IOException {
        String line = br.readLine();
        String[] tokens = line.split(" ");
        Map<String, String> headers = new HashMap<>();

        String requestBody = "";
        String queryString = "";

        while (!"".equals(line)) {
            log.debug(line);
            line = br.readLine();
            String[] headerTokens = line.split(": ", 2);

            if (headerTokens.length == 2) {
                headers.put(headerTokens[0], headerTokens[1]);
            }
        }

        if (tokens[1].contains("?")) {
            queryString = tokens[1].substring(tokens[1].indexOf("?") + 1);
            log.debug("query string:{}",queryString);
        }

        if (headers.get("Content-Length") != null) {
            requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
        }
        queryString = queryString + "&" + requestBody;
        parameters = HttpRequestUtils.parseQueryString(queryString);

        log.info(Arrays.toString(tokens));
        url = tokens[1];

        return url;
    }
}
