package webserver.httpRequest;

import model.HttpMethod;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.httpRequest.HttpRequest.RequestLine;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

import static java.util.Map.entry;

class HttpRequestTest {

    @Test
    @DisplayName("HTTP 요청 파싱 테스트")
    void testParseHttpRequest() throws Exception {
        // Given
        String rawRequest = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Content-Length: 25\n" +
                "\n" +
                "userId=Juju&password=123";
        BufferedReader br = new BufferedReader(new StringReader(rawRequest));

        // When
        HttpRequest httpRequest = HttpRequestProcessor.parse(br);
        RequestLine requestLine = httpRequest.getRequestLine();
        Map<String, String> headers = httpRequest.getHeader();
        Map<String, String> body = httpRequest.getBody();

        // Then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        softly.assertThat(requestLine.getPath()).isEqualTo("/index.html");
        softly.assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1");

        softly.assertThat(headers).containsOnly(
                entry("Host", "localhost:8080"),
                entry("Content-Length", "25")
        );

        softly.assertThat(body).contains(
                entry("userId", "Juju"),
                entry("password", "123")
        );
    }
}
