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
