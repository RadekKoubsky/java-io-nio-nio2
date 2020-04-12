package org.rkoubsky.nio.selectors;

import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class Client {

    private final InetSocketAddress address;

    public Client(InetSocketAddress address) {
        this.address = address;
    }

    public void sendRequest(String requestMessage){
        try(SocketChannel socketChannel = SocketChannel.open(this.address)){
            CharBuffer charBuffer = CharBuffer.allocate(1024);
            charBuffer.put(requestMessage);
            charBuffer.flip();
            socketChannel.write(StandardCharsets.UTF_8.encode(charBuffer));
        } catch (Exception e){
            log.error("Erro while sending request", e);
        }
    }
}
