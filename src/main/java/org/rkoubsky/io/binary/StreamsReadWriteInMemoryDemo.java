package org.rkoubsky.io.binary;

import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class StreamsReadWriteInMemoryDemo {

    public static void main(String[] args) throws IOException {
        List<Integer> integersWritten = IntStream.range(0, 5).boxed().collect(Collectors.toList());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(bos)) {
            log.info("Writing list of integers as bytes to byte array using DataOutputStream(ByteArrayOutputStream), list: {}", integersWritten);
            integersWritten.forEach(i -> {
                try {
                    dos.writeInt(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
            List<Integer> integersRead = new LinkedList<>();
            log.info("Reading bytes as integers from byte array using DataInputStream(ByteArrayInputStream)");
            try {
                while (true) {
                    integersRead.add(dis.readInt());
                }
            } catch (EOFException e) {
                e.printStackTrace();
            }
            log.info("List of integers read from byte array: {}", integersRead);
        }
    }
}
