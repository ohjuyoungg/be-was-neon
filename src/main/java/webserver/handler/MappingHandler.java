package webserver.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import model.HttpMethod;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import db.Database;
import utils.FileUtil;
import webserver.ContentType;
import webserver.httpMessage.HttpRequest;
import webserver.httpMessage.HttpResponse;

public class MappingHandler {

    private static final Logger log = LoggerFactory.getLogger(MappingHandler.class);
    public static final String STATIC_PATH = "src/main/resources/static";

    public void handleRequest(HttpRequest request, HttpResponse response, OutputStream out) throws IOException {
        HttpMethod method = request.getRequestLine().getMethod();
        String path = request.getRequestLine().getPath();

        if (method == HttpMethod.GET) {
            handleGetRequest(path, response, out);
        } else if (method == HttpMethod.POST) {
            handlePostRequest(path, request, response, out);
        } else {
            handleUnsupportedRequest(method, path, response, out);
        }
    }

    private void handleGetRequest(String path, HttpResponse response, OutputStream out) throws IOException {
        File file = new File(STATIC_PATH + path);
        if (file.exists() && !file.isDirectory()) {
            serveStaticFile(file, response, out);
        } else {
            log.error("File not found: " + path);
            response.send404NotFound(out);
        }
    }

    private void handlePostRequest(String path, HttpRequest request, HttpResponse response, OutputStream out) throws IOException {
        if (path.equals("/create")) {
            handleUserCreate(request, response, out);
        } else if (path.equals("/login")) {
            handleUserLogin(request, response, out);
        } else {
            log.warn(String.format("Unsupported POST request: %s", path));
            response.send404NotFound(out);
        }
    }

    private void handleUserCreate(HttpRequest request, HttpResponse response, OutputStream out) throws IOException {
        User user = HttpRequest.buildUser(request.getBody());
        Database.addUser(user);
        log.debug("user : {}", user);
        response.response302Header(out);
    }

    private void handleUserLogin(HttpRequest request, HttpResponse response, OutputStream out) throws IOException {
        User user = HttpRequest.buildUser(request.getBody());
        Database.addUser(user);
        log.debug("user : {}", user);
        if (user == null) {
            log.debug("User Not Found");
        } else if (user.getPassword().equals("password")) {
            response.response302HeaderWithCookie(out, "login=true");
        } else {
            log.debug("Password Mismatch");
            response.response302Header(out);
        }
    }

    private void handleUnsupportedRequest(HttpMethod method, String path, HttpResponse response, OutputStream out) throws IOException {
        log.warn(String.format("Unsupported request: method=%s, path=%s", method, path));
        response.send404NotFound(out);
    }

    private void serveStaticFile(File file, HttpResponse response, OutputStream out) throws IOException {
        byte[] body = FileUtil.toByteArray(file);
        String type = ContentType.getContentType(file.getName());
        response.response200Header(out, body, type);
    }
}
