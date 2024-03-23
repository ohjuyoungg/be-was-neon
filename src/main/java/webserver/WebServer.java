package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handler.MainHandler;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    /**
     * 서버에서 요청을 처리하는 시간(책임, 부하, ...)의 대부분이
     * IO Bound : IO인 경우
     *  - ex. 검색
     *  - 유저 -> 서버 -> DB -> (검색중...) -> 서버 -> 유저
     *  - 검색할 때 대부분의 시간을 사용함. 서버는 별로 일 안함.
     * CPU Bound : CPU 작업인 경우
     *  - ex. 네비게이션에서 목적지를 입력하면 최단경로가 도출된다.
     *  - 현재 위치에서 목적지로 가는데 최단 경로를 찾아야 한다.
     *     경우의 수가 100만가지가 있는데, 그 중 최단경로 1개를 찾는다.
     *     복잡한 로직이 수행된다. 로직은 CPU가 처리한다.
     */
    private static final int CORE_POOL_SIZE = 8;

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            Socket connection;
            ExecutorService executorService = Executors.newFixedThreadPool(CORE_POOL_SIZE);

            while ((connection = listenSocket.accept()) != null) {
                executorService.execute(new MainHandler(connection));
            }
        }
    }
}