package webserver.httpMessage;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {

    public void sendResponse(OutputStream out, byte[] body, String contentType) throws IOException {
        DataOutputStream dos = new DataOutputStream(out); // 출력 스트림(OutputStream)을 DataOutputStream 으로 변환한다. 이렇게 하면 데이터를 바이트로 직접 쓰기 위한 메서드를 사용할 수 있다.
        dos.writeBytes("HTTP/1.1 200 OK \r\n"); // HTTP 응답의 첫 번째 줄을 작성 여기서는 HTTP 상태 코드 200(OK)을 사용 요청이 성공 했음을 나타낸다.
        dos.writeBytes("Content-Type: " + contentType + "\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n"); // 응답 헤더에 Content-Length를 작성 이는 응답 본문의 길이를 나타낸다. 클라이언트는 이 값을 사용해서 응답의 크기를 결정하고, 응답을 완전히 수신하는 데 필요한 바이트 수를 파악
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length); // 응답 본문을 작성 요청된 리소스의 내용을 클라이언트에게 보냄
        dos.flush(); // 모든 데이터를 출력 스트림으로 플러시하여 클라이언트에게 전송될 수 있도록 함.
    }

    // 404 Not Found 응답을 전송하는 메서드
    public void send404NotFound(OutputStream out) throws IOException {
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
