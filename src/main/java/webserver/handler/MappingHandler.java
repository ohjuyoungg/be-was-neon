package webserver.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

import model.HttpMethod;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import db.Database;
import utils.FileUtil;
import webserver.ContentType;
import webserver.httpRequest.HttpRequest;
import webserver.httpResponse.HttpResponse;

public class MappingHandler {

    private static final Logger log = LoggerFactory.getLogger(MappingHandler.class);
    public static final String STATIC_PATH = "src/main/resources/static";

    /**
     * 주어진 HTTP 요청에 대해 적절한 처리를 수행하도록 지정된
     * 요청 핸들러 메서드를 호출합니다.
     */
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

    /**
     * GET 요청에 대한 처리를 수행합니다.
     * 요청된 파일이 존대하면 해당 파일을 응답으로 전송하고,
     * 존재하지 않으면 404 응답을 반환합니다.
     */
    private void handleGetRequest(String path, HttpResponse response, OutputStream out) throws IOException {
        File file = new File(STATIC_PATH + path);
        if (file.exists() && !file.isDirectory()) {
            serveStaticFile(file, response, out);
        } else {
            log.error("File not found: " + path);
            response.send404NotFound(out);
        }
    }

    /**
     * POST 요청에 대한 처리를 수행합니다.
     * '/create' 경로에 대해서는 새로운 사용자를 생성하고,
     * '/login' 경로에 대해서는 사용자 로그인을 처리합니다.
     */
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

    /**
     * 사용자 생성 요청을 처리합니다.
     * 요청된 사용자 정보를 데이터베이스에 추가하고,
     * 응답으로 302 코드를 반환합니다.
     */
    private void handleUserCreate(HttpRequest request, HttpResponse response, OutputStream out) throws IOException {
        User user = HttpRequest.buildUser(request.getBody());
        Database.addUser(user);
        log.debug("user : {}", user);
        response.response302Header(out);
    }

    /**
     * 사용자 로그인 요청을 처리합니다.
     * 요청된 사용자 정보를 검증하고, 쿠키를 설정하여
     * 로그인 상태를 유지합니다.
     */
    private void handleUserLogin(HttpRequest request, HttpResponse response, OutputStream out) throws IOException {
        Map<String, String> body = request.getBody(); // Request의 body를 가져옴
        String userId = body.get("userId");
        User user = Database.findUserById(userId);
        log.debug("user : {}", user); // user 객체를 로그로 출력

        if (user == null) {
            log.debug("User Not Found");
            response.response302HeaderWithoutCookie(out);
        } else if (request.getBody().get("password").equals(user.getPassword())) { // 비밀번호가 "password"인 경우
            // format: <cookie-name>=<cookie-value>; Path=<path-value>
            String cookie = String.format("sid=%s; Path=/", UUID.randomUUID().toString());
            response.response302HeaderWithCookie(out, cookie);
        } else {
            log.debug("Password Mismatch");
            response.response302HeaderWithoutCookie(out);
        }
    }

    /**
     * 지원되지 않는 HTTP 메소드에 대한 요청을 처리합니다.
     * 해당 경로와 메소드에 대한 404 응답을 반환합니다.
     */
    private void handleUnsupportedRequest(HttpMethod method, String path, HttpResponse response, OutputStream out) throws IOException {
        log.warn(String.format("Unsupported request: method=%s, path=%s", method, path));
        response.send404NotFound(out);
    }

    /**
     * 정적 파일을 서빙하기 위한 메소드입니다.
     * 요청된 파일의 내용과 유형에 따라 적절한 응답을 생성합니다.
     */
    private void serveStaticFile(File file, HttpResponse response, OutputStream out) throws IOException {
        byte[] body = FileUtil.toByteArray(file);
        String type = ContentType.getContentType(file.getName());
        response.response200Header(out, body, type);
    }
}
