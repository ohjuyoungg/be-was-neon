// MainHandler.java
package webserver.handler;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpMessage.HttpRequest;
import webserver.httpMessage.HttpResponse;
import webserver.httpRequest.HttpRequestProcessor;

public class MainHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MainHandler.class);
    private final Socket connection;
    private final MappingHandler mappingHandler;

    public MainHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.mappingHandler = new MappingHandler();
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
            HttpRequest httpRequest = HttpRequestProcessor.parse(br);
            HttpResponse httpResponse = new HttpResponse();

            mappingHandler.handleRequest(httpRequest, httpResponse, out);
        } catch (IOException e) {
            log.error("Error handling request: {}", e.getMessage());
        }
    }
}
