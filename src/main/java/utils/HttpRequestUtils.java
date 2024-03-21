package utils;

import model.HttpMethod;
import webserver.httpMessage.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HttpRequestUtils {

    public static HttpRequest parse(BufferedReader br) throws IOException {
        StringBuilder rawRequestBuilder = new StringBuilder();
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            rawRequestBuilder.append(currentLine).append("\n");
            if (currentLine.isEmpty()) {
                break;
            }
        }

        String rawRequest = rawRequestBuilder.toString();

        String[] lines = rawRequest.split("\n");

        // Start line
        String startLine = lines[0];

        // Headers
        Map<String, String> headers = new HashMap<>();
        int bodyStartIndex = -1;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                bodyStartIndex = i + 1;
                break;
            }
            String[] headerParts = lines[i].split(": ", 2);
            headers.put(headerParts[0], headerParts[1]);
        }

        if (HttpMethod.POST == getMethod(startLine)) {
            String bodyLine = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
        }

        // Body
        Map<String, String> body = new HashMap<>();
        if (bodyStartIndex != -1 && bodyStartIndex < lines.length) {
            String bodyData = lines[bodyStartIndex];
            String[] bodyParts = bodyData.split("&");
            for (String part : bodyParts) {
                String[] keyValue = part.split("=");
                body.put(keyValue[0], keyValue[1]);
            }
        }

        return new HttpRequest(startLine, headers, body);
    }

    public static HttpMethod getMethod(String startLine) {
        String[] tokens = startLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("HTTP Request start line은 반드시 method + path + protocol로 구성되어야 합니다.");
        }
        return HttpMethod.of(tokens[0]);
    }


    public static Map<String, String> parseQueryString(String queryString) {
        return parseKeyValues(queryString, "&");
    }

    private static Map<String, String> parseKeyValues(String values, String separator) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        String[] tokens = values.split(separator);
        return Arrays.stream(tokens)
                .map(token -> token.split("="))
                .filter(pair -> pair.length == 2)
                .collect(Collectors.toMap(pair -> decode(pair[0]), pair -> decode(pair[1])));
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
