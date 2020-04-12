package org.rkoubsky.io.binary;

import com.google.common.io.MoreFiles;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class StreamsReadWriteFileDemo {
    public static final Path WRITE_DIR = Paths.get("stream_test");
    public static final Path WRITE_FILE = WRITE_DIR.resolve("write_file.bin");

    public static void main(String[] args) throws IOException {
        try {
            Files.createDirectories(WRITE_DIR);
            List<Integer> integersWritten = IntStream.range(0, 5)
                                                     .boxed()
                                                     .collect(Collectors.toList());

            try (DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(WRITE_FILE)))) {
                log.info("Writing list of integers as bytes to file using DataOutputStream, list: {}", integersWritten);
                integersWritten.forEach(i -> {
                    try {
                        dos.writeInt(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            try (DataInputStream dis = new DataInputStream(Files.newInputStream(WRITE_FILE))) {
                List<Integer> integersRead = new LinkedList<>();
                log.info("Reading bytes as integers from the file using DataInputStream");
                try {
                    while (true) {
                        integersRead.add(dis.readInt());
                    }
                } catch (EOFException e) {
                    e.printStackTrace();
                }
                log.info("List of integers read from file: {}", integersRead);
            }

        } finally {
            MoreFiles.deleteRecursively(WRITE_DIR);
        }
    }
}
