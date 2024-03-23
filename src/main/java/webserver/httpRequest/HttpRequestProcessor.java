package webserver.httpRequest;

import model.HttpMethod;
import utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static webserver.httpRequest.HttpRequest.getMethod;


public class HttpRequestProcessor {

    public static HttpRequest parse(BufferedReader br) throws IOException {
        StringBuilder rawRequestBuilder = readRawRequest(br);
        String rawRequest = rawRequestBuilder.toString();
        String[] lines = rawRequest.split("\n");
        // Start line
        String startLine = getStartLine(lines);
        // Headers
        Map<String, String> headers = getHeaders(lines);
        // Body
        Map<String, String> body = getBody(br, headers, startLine);
        return new HttpRequest(startLine, headers, body);
    }

    private static StringBuilder readRawRequest(BufferedReader br) throws IOException {
        StringBuilder rawRequestBuilder = new StringBuilder();
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            rawRequestBuilder.append(currentLine).append("\n");
            if (currentLine.isEmpty()) {
                break;
            }
        }
        return rawRequestBuilder;
    }

    private static String getStartLine(String[] lines) {
        return lines[0];
    }

    private static Map<String, String> getHeaders(String[] lines) {
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                break;
            }
            String[] headerParts = lines[i].split(": ", 2);
            headers.put(headerParts[0], headerParts[1]);
        }
        return headers;
    }

    private static Map<String, String> getBody(BufferedReader br, Map<String, String> headers, String startLine) throws IOException {
        Map<String, String> body = new HashMap<>();
        if (HttpMethod.POST == getMethod(startLine)) {
            String bodyStr = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
            String[] bodyParts = bodyStr.split("&");
            for (String part : bodyParts) {
                String[] keyValue = part.split("=");
                body.put(keyValue[0], keyValue[1]);
            }
        }
        return body;
    }
}


