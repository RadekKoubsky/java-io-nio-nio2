package org.rkoubsky.nio.selectors;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author Radek Koubsky
 */
public class WebServerDemo {
    public static final String HOSTNAME = "127.0.0.1";
    public static final int PORT = 12345;


    public static void main(String[] args) throws InterruptedException {
        WebServer webServer = new WebServer(new InetSocketAddress(PORT));
        Client client = new Client(new InetSocketAddress(HOSTNAME, PORT));

        Thread serverThread = new Thread(() -> webServer.startServer());
        serverThread.start();

        IntStream.range(0, 5).forEach(i -> {
            client.sendRequest("Request from client#" + i);
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    
        serverThread.join(1_000L);
    }
}
