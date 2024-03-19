package webserver.httpMessage;

import db.Database;
import model.User;

import utils.HttpRequestUtils;

import java.util.Map;

public class HttpRequest {

    private static String url;

    public HttpRequest(String url) {
        HttpRequest.url = url;
    }

    public void registrationParamValue() {
        if (url.startsWith("/registration/index.html?")) {
            Map<String, String> params = parseParamsFromURL(url);
            User user = createUserFromParams(params);
            Database.addUser(user);
        }
    }

    private Map<String, String> parseParamsFromURL(String url) {
        String[] parts = url.split("\\?");
        String queryString = parts[1];
        return HttpRequestUtils.parseQueryString(queryString);
    }

    private User createUserFromParams(Map<String, String> params) {
        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");
        String email = params.get("email");
        return new User(userId, password, name, email);
    }
}
