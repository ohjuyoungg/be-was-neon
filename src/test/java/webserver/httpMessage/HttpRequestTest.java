package webserver.httpMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체가 올바르게 생성되는지 확인")
    void testHttpRequestCreation() {
        // Given
        String startLine = "GET /test?param1=value1&param2=value2 HTTP/1.1";
        Map<String, String> header = new HashMap<>();
        header.put("Host", "localhost");
        header.put("User-Agent", "Mozilla/5.0");
        Map<String, String> body = new HashMap<>();
        body.put("field1", "value1");
        body.put("field2", "value2");

        // When
        HttpRequest httpRequest = new HttpRequest(startLine, header, body);

        // Then
        assertThat(httpRequest).isNotNull();
        assertThat(httpRequest.getRequestLine().getMethod().toString()).isEqualTo("GET");
        assertThat(httpRequest.getRequestLine().getPath()).isEqualTo("/test");
        assertThat(httpRequest.getRequestLine().getProtocol()).isEqualTo("HTTP/1.1");

        assertThat(httpRequest.getRequestLine().getParams()).containsEntry("param1", "value1")
                .containsEntry("param2", "value2");

        assertThat(httpRequest.getHeader()).containsEntry("Host", "localhost")
                .containsEntry("User-Agent", "Mozilla/5.0");

        assertThat(httpRequest.getBody()).containsEntry("field1", "value1")
                .containsEntry("field2", "value2");
    }
}
