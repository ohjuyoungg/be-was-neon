package webserver;

import org.junit.jupiter.api.Test;
import utils.HTTPRequestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class HTTPRequestUtilsTest {

    @Test
    void testParseQueryString() {
        String queryString1 = "userId=johndoe&password=password123&name=John+Doe&email=johndoe@example.com";
        Map<String, String> params1 = HTTPRequestUtils.parseQueryString(queryString1);
        assertThat(params1)
                .containsEntry("userId", "johndoe")
                .containsEntry("password", "password123")
                .containsEntry("name", "John Doe")
                .containsEntry("email", "johndoe@example.com");
    }
}
