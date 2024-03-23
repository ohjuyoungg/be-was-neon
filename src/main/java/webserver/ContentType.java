package webserver;

import java.util.Map;

public class ContentType {

    private static final Map<String, String> CONTENT_TYPE = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css",
            "js", "application/javascript",
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "gif", "image/gif",
            "svg", "image/svg+xml"
    );

    public static String getContentType(String url) {
        String[] parts = url.split("\\.");
        String extension = parts[parts.length - 1];
        return CONTENT_TYPE.getOrDefault(
                extension,
                "application/octet-stream"
        );
    }
}
