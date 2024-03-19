package webserver.httpMessage;

import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.HashMap;


import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    @DisplayName("URL이 '/create'로 시작하는지 확인한다.")
    void testIsCreateRequest() {
        HttpRequest httpRequest = new HttpRequest("/create");
        assertThat(httpRequest.isCreateRequest()).isTrue();
    }

    @Test
    @DisplayName("주어진 맵으로부터 유저 객체를 생성한다.")
    void testCreateUserFromParams() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", "testId");
        params.put("password", "testPassword");
        params.put("name", "Test User");
        params.put("email", "test@example.com");

        User user = HttpRequest.createUserFromParams(params);

        assertThat(user.getUserId()).isEqualTo("testId");
        assertThat(user.getPassword()).isEqualTo("testPassword");
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }
}
