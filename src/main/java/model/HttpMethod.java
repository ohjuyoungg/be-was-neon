package model;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod of(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(m -> m.name().equalsIgnoreCase(method))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메서드입니다."));
    }
}
