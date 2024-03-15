package webserver;

public class ContentType {

    public String getContentType(String url) {
        String[] parts = url.split("\\.");
        String extension = parts[parts.length - 1];
        switch (extension) {
            case "html":
                return "text/html;charset=utf-8";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml"; // SVG 파일에 대한 Content-Type 추가
            default:
                return "application/octet-stream";
        }
    }
}
