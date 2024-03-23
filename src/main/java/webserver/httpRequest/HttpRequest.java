package webserver.httpMessage;

import model.HttpMethod;
import model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final Map<String, String> body;

    public HttpRequest(String startLine, Map<String, String> header, Map<String, String> body) {
        this.requestLine = new RequestLine(startLine);
        this.header = header;
        this.body = body;
    }

    public static User buildUser(Map<String, String> body) {
        String userId = body.get("userId");
        String password = body.get("password");
        String name = body.get("name");
        String email = body.get("email");
        return new User(userId, password, name, email);
    }


    public static HttpMethod getMethod(String startLine) {
        String[] tokens = startLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("HTTP Request start line은 반드시 method + path + protocol로 구성되어야 합니다.");
        }
        return HttpMethod.of(tokens[0]);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public static class RequestLine {
        private final HttpMethod method;
        private final String path;
        private final Map<String, String> params;
        private final String protocol;

        public RequestLine(String startLine) {
            String[] tokens = startLine.split(" ");
            if (tokens.length != 3) {
                throw new IllegalArgumentException("HTTP 요청 라인이 올바르지 않습니다.");
            }
            this.method = HttpMethod.of(tokens[0]);
            this.path = getPath(tokens[1]);
            this.params = getParams(tokens[1]);
            this.protocol = tokens[2];
        }

        public HttpMethod getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public String getProtocol() {
            return protocol;
        }

        private String getPath(String requestTarget) {
            return requestTarget.split("\\?")[0];
        }

        private Map<String, String> getParams(String requestTarget) {
            String[] tokens = requestTarget.split("\\?");
            if (tokens.length <= 1 || tokens[1].isBlank()) {
                return Collections.emptyMap();
            }
            Map<String, String> params = new HashMap<>();
            String[] queryParams = tokens[1].split("&");
            for (String queryParam : queryParams) {
                String[] entry = queryParam.split("=");
                params.put(entry[0], entry[1]);
            }
            return params;
        }
    }
}
