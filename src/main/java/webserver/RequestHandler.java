package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HTTPRequestUtils;

// Thread 를 생성 시 두 가지 방법이 있다.
// 1. extends Thread
// 2. Runnable Thread -> 선택 이유 : 1번으로 할 시 자바는 다중 상속이 되지 않아 보통 Runnable 로 한다.
public class RequestHandler implements Runnable {
    // 로깅을 위한 Logger 객체를 생성한다. RequestHandler 클래스를 위한 Logger 를 생성하도록 LoggerFactory 에 요청한다.
    // Logger란? 시스템 운영에 대한 기록을 용이하게 남기게 해줄 수 있는 클래스
    // 로그 레벨은 TRACE > DEBUG > INFO > WARN > ERROR 로 구성되어있음
    // 일반적으로 개발 서버는 debug, 운영 서버는 info 로 설정
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    // 이 클래스가 처리하는 요청과 관련된 소켓 연결을 나타낸다. Socket -> Java 의 네트워크 통신을 위한 클래스로 클라이언트와 서버간의 통신에 사용된다.
    // connection 변수는 RequestHandler 가 생성될 때 생성자를 통해 초기화되는데 이는 서버 측에서 클라이언트와의 연결을 나타내는 소켓이다. 이 소켓을 통해 클라이언트와의 데이터 통신이 이루어진다.
    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() { // Runnable 인터페이스의 run() 메소드를 오버라이딩하여 구현한 것 -> 클라이언트 요청을 처리하는 로직이 포함되었다.
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort()); // 새로운 클라이언트가 서버에 연결되었음을 디버그 레벨의 로그로 출력한다. 클라이언트의 IP 주소와 포트 번호도 함께 출력된다.

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) { // 클라이언트와의 소켓 통신을 위해 입력 스트림과 출력 스트림을 가져온다. try-with-resource 구문을 사용하여 자동으로 스트림을 닫는다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in)); // 클라이언트로부터의 입력을 읽어들이기 위한 BufferedReader 객체를 생성한다.

            String line = br.readLine(); // 클라이언트로 부터 첫 번째 줄을 읽어들인다. 일반적으로 HTTP 요청 메시지의 첫 줄이며, 여기서는 요청 라인이다.
            String[] tokens = line.split(" "); // 첫 번째 줄을 공백을 기준으로 분할하여 토큰 배열에 저장한다. 이는 HTTP 요청 라인을 메서드, URL, HTTP 버전으로 분할하는 작업이다.

            // 요청 메시지 로깅
            while (!"".equals(line)) { // 나머지 요청 헤더를 읽어들여 디버그 레벨의 로그로 출력한다. 빈 줄을 만날 때 까지 요청 헤더를 모두 읽어들인다.
                log.debug(line);
                line = br.readLine();
            }

            log.info(Arrays.toString(tokens)); // 요청 메서드, URL, HTTP 버전을 정보 레벨의 로그로 출력한다.
            String url = tokens[1]; // 요청된 URL을 추출한다. 주로 두 번째 토큰이 URL을 나타낸다.

            if (url.startsWith("/registration/index.html?")) {
                Map<String, String> params = parseParamsFromURL(url);
                User user = createUserFromParams(params);
                Database.addUser(user);
            }

            // 요청된 파일을 읽어들이고 적절한 Content-Type 설정 후 응답
            File file = new File("src/main/resources/static" + url); // 요청된 URL을 기반으로 요청된 파일을 나타내는 File 객체를 생성한다.

            if (file.exists() && !file.isDirectory()) { // 요청된 파일이 존재하고 디렉터리가 아닌 경우, 해당 파일의 내용을 읽어들여 클라이언트에게 응답을 보낸다. 그렇지 않으면 404 응답을 보낸다.
                byte[] body = readFileContents(file); // 요청된 파일을 읽어들이는 readFileContents 메서드를 호출하여 파일의 내용을 바이트 배열로 읽어온다.
                String contentType = getContentType(url);
                sendResponse(out, body, contentType); // 클라이언트에게 응답을 보내는 sendResponse 메서드를 호출한다. 이 메서드는 응답 헤더와 본문을 함께 전송하여 클라이언트에게 요청에 대한 응답을 보낸다. 인자로는 출력 스트림('out'), 응답 본문(body), 그리고 Content-Type이 포함된다.
            } else {
                // 파일이 존재하지 않는 경우 404 응답 반환
                log.error("File not found: " + url);
                send404NotFound(out);
            }
        } catch (IOException e) {
            log.error("Error handling request: {}", e.getMessage());
        }
    }

    private Map<String, String> parseParamsFromURL(String url) {
        String[] parts = url.split("\\?");
        String queryString = parts[1];
        return HTTPRequestUtils.parseQueryString(queryString);
    }

    private User createUserFromParams(Map<String, String> params) {
        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");
        String email = params.get("email");
        return new User(userId, password, name, email);
    }

    // 파일을 읽어들이는 메서드
    private byte[] readFileContents(File file) throws IOException { // 매개변수로는 읽어들일 파일을 가리키는 File 객체를 받고, IOException을 발생할 수 있으므로 이를 throws 한다.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); // 파일의 내용을 읽어들이기 위한 바이트 배열 출력 스트림 'ByteArrayOutputStream' 객체를 생성
        try (InputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()]; // 파일의 내용을 저장할 바이트 배열을 생성, 이 배열의 크기는 파일의 크기와 동일하게 설정
            int bytesRead; // 파일에서 실제로 읽은 바이트 수를 저장할 변수 'bytesRead'를 선언
            while ((bytesRead = in.read(bytes, 0, bytes.length)) != -1) { // 파일에서 바이트를 읽어들이는 반복문 'read' 메소드를 사용하여 파일에서 바이트를 읽어들이고, 읽은 바이트 수를 bytesRead 변수에 저장 반환 값이 -1이 아닌 동안에는 파일의 끝까지 계속 읽는다.
                buffer.write(bytes, 0, bytesRead); // 읽어들인 바이트를 임시로 저장한 'ByteArrayOutputStream'에 쓰기 작업을 수행 이 작업은 파일의 내용을 임시로 저장하는데 사용
            }
        }
        return buffer.toByteArray(); // 모든 파일 내용을 읽어들인 후, 'ByteArrayOutputStream'에 저장된 내용을 바이트 배열로 변환하여 반환 메서드를 호출한 곳으로 전달되어 클라이언트에게 응답 본문으로 전송
    }

    // 파일 확장자에 따라 Content-Type을 결정하는 메서드
    private String getContentType(String url) {
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

    // HTTP 응답을 클라이언트에게 보내는 메서드
    private void sendResponse(OutputStream out, byte[] body, String contentType) throws IOException {
        DataOutputStream dos = new DataOutputStream(out); // 출력 스트림(OutputStream)을 DataOutputStream 으로 변환한다. 이렇게 하면 데이터를 바이트로 직접 쓰기 위한 메서드를 사용할 수 있다.
        dos.writeBytes("HTTP/1.1 200 OK \r\n"); // HTTP 응답의 첫 번째 줄을 작성 여기서는 HTTP 상태 코드 200(OK)을 사용 요청이 성공 했음을 나타낸다.
        dos.writeBytes("Content-Type: " + contentType + "\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n"); // 응답 헤더에 Content-Length를 작성 이는 응답 본문의 길이를 나타낸다. 클라이언트는 이 값을 사용해서 응답의 크기를 결정하고, 응답을 완전히 수신하는 데 필요한 바이트 수를 파악
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length); // 응답 본문을 작성 요청된 리소스의 내용을 클라이언트에게 보냄
        dos.flush(); // 모든 데이터를 출력 스트림으로 플러시하여 클라이언트에게 전송될 수 있도록 함.
    }

    // 404 Not Found 응답을 전송하는 메서드
    private void send404NotFound(OutputStream out) throws IOException {
        String errorMessage = "404 Not Found";
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + errorMessage.length() + "\r\n" +
                "\r\n" +
                errorMessage);
        dos.flush();
    }
}
