package webserver.httpMessage;

import db.Database;
import model.User;
import utils.HttpRequestUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpRequest {

    private String url; // 인스턴스 변수로 변경

    public HttpRequest(String url) {
        this.url = url; // 생성자에서 초기화
    }

    public boolean isCreateRequest() {
        return url != null && url.startsWith("/create"); // null 체크 추가
    }

    public void registrationParamValue(OutputStream out, Map<String, String> params) throws IOException {
        if (isCreateRequest()) { // isCreateRequest 메서드를 호출하여 null 체크
            User user = createUserFromParams(params);
            Database.addUser(user);
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.response302Header(out);
        }
    }

    public static User createUserFromParams(Map<String, String> params) {
        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");
        String email = params.get("email");
        return new User(userId, password, name, email);
    }

    private Map<String, String> parseParamsFromURL(String url) {
        String[] parts = url.split("\\?");
        String queryString = parts[1];
        return HttpRequestUtils.parseQueryString(queryString);
    }
}
