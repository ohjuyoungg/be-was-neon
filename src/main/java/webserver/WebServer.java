package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    // 로깅을 위한 Logger 객체를 생성 서버의 동작 상태를 기록 할 수 있다.
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080; // 기본 포트 번호를 정의 만약 사용자가 포트 번호를 지정하지 않을 경우에는 이 포트 번호가 사용됨
    private static final int CORE_POOL_SIZE = (8 * 10);

    public static void main(String args[]) throws Exception { // 프로그램의 진입점인 main 메서드를 정의 이 메서드는 예외를 던질 수 있다.
        int port = 0; // 포트 번호를 저장할 변수를 선언하고 초기화
        if (args == null || args.length == 0) { // 프로그램 실행 시 사용자가 인자를 전달하지 않았거나 인자의 개수가 0인 경우를 검사
            port = DEFAULT_PORT; // 만약 인자가 없다면 기본 포트 번호를 사용
        } else {
            port = Integer.parseInt(args[0]); // 첫 번째 전달된 인자를 정수형으로 변환하여 포트 번호를 사용
        }

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) { // 지정된 포트 번호로 ServerSocket 을 생성, 이는 클라이언트의 연결을 수신하기 위한 소켓이다
            logger.info("Web Application Server started {} port.", port); // 서버가 시작되었음을 로그에 기록 포트 번호를 함께 기록하여 어느 포트에서 서버가 동작중인지 알 수 있다.

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection; // 클라이언트와의 연결을 담당할 Socket 객체를 선언
            while ((connection = listenSocket.accept()) != null) { // 클라이언트로부터 연결 요청이 올 때까지 무한히 대기 클라이언트의 연결 요청이 있으면 해당 요청을 수락하고, 새로운 Socket 객체를 생성해서 연결을 처리
                // 요청 수가 예측 가능하고, 요청을 처리하는 데 필요한 시간이 일정하다면 'newFixedThreadPool()을 사용, 이를 통해 고정된 수의 스레드만 생성하고 작업을 처리할 수 있으며, 스레드의 생성 및 소멸에 따른 오버헤드를 줄일 수 있다.
                ExecutorService executorService = Executors.newFixedThreadPool(CORE_POOL_SIZE); // 클라이언트 요청을 처리하기 위한 ExecutorService 를 생성
                executorService.execute(new RequestHandler(connection)); // ExecutorService 를 사용하여 클라이언트 요청을 처리하는 RequestHandler 스레드를 실행 새로운 클라이언트 요청이 들어올 때마다 새로운 RequestHandler 스레드가 생성되어 클라이언트와의 통신을 담당
            }
        }
    }
}
