package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.HttpRequestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class HttpRequestUtilsTest {

    @Test
    @DisplayName("주어진 쿼리 스트링을 파싱하여 맵 형태로 반환한다.")
    void testParseQueryString() {
        String queryString1 = "userId=johndoe&password=password123&name=John+Doe&email=johndoe@example.com";
        Map<String, String> params1 = HttpRequestUtils.parseQueryString(queryString1);
        assertThat(params1)
                .containsEntry("userId", "johndoe")
                .containsEntry("password", "password123")
                .containsEntry("name", "John Doe")
                .containsEntry("email", "johndoe@example.com");
    }
}
