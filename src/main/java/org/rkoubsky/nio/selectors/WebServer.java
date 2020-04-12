package org.rkoubsky.nio.selectors;

import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class WebServer {
    private final InetSocketAddress address;

    public WebServer(InetSocketAddress address) {
        this.address = address;
    }

    public void startServer() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.configureBlocking(false);

            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(this.address);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                log.info("Listening to events...");
                int receivedEvents = selector.select();
                log.info("Received {} events", receivedEvents);

                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        log.info("Accepting connection");
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        keys.remove(key);
                    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        try (SocketChannel socketChannel = (SocketChannel) key.channel()) {
                            log.info("Reading content from socket");
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            socketChannel.read(buffer);
                            buffer.flip();
                            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                            log.info("Read content: {}", charBuffer.toString());
                            buffer.clear();
                            keys.remove(key);
                            key.cancel();
                        }
                    }
                }
            }
        } catch (Exception e){
            log.error("Error while openning server socket", e);
        }
    }
}
