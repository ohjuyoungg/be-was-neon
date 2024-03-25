package utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.HttpRequestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class HttpRequestUtilsTest {

    @Test
    @DisplayName("주어진 쿼리 스트링을 파싱하여 맵 형태로 반환한다.")
    void testParseQueryString() {
        String queryString1 = "userId=Juju&password=password123&name=Juju&email=Juju@example.com";
        Map<String, String> params1 = HttpRequestUtils.parseQueryString(queryString1);
        assertThat(params1)
                .containsEntry("userId", "Juju")
                .containsEntry("password", "password123")
                .containsEntry("name", "Juju")
                .containsEntry("email", "Juju@example.com");
    }
}
