package org.rkoubsky.nio.binary;

import com.google.common.io.MoreFiles;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class ChannelReadWriteBinaryDemo {
    public static final Path WRITE_DIR = Paths.get("channel_test");
    public static final Path WRITE_FILE = WRITE_DIR.resolve("write_file.bin");
    public static final int CAPACITY = 1024 * 1024;

    public static void main(String[] args) throws IOException {
        try {
            Files.createDirectories(WRITE_DIR);

            ByteBuffer buffer = createBuffer();

            putIntsToBuffer(buffer);
            writeBufferToFile(buffer);

            readFileIntoBuffer(buffer);
            readAndPrintBuffer(buffer);

        } finally {
            MoreFiles.deleteRecursively(WRITE_DIR);
        }
    }

    private static ByteBuffer createBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
        log.info("Created new byte buffer with capacity: {}", CAPACITY);
        printPositionWithLimit(buffer);
        return buffer;
    }

    private static void readAndPrintBuffer(ByteBuffer buffer) {
        log.info("Calling flip() on buffer");
        buffer.flip();
        printPositionWithLimit(buffer);

        log.info("Reading buffer content using IntBuffer");
        IntBuffer intBuffer = buffer.asIntBuffer();

        List<Integer> integersRead = new LinkedList<>();
        while (intBuffer.hasRemaining()){
            integersRead.add(intBuffer.get());

        }
        log.info("List of integers read from file through ByteBuffer -> IntBuffer, integers: {}", integersRead);

        /**
         * Position, limit, and mark values of ByteBuffer and IntBuffer are independent.
         */
        log.info("ByteBuffer position and limit:");
        printPositionWithLimit(buffer);
        /**
         * IntBuffer has its own position which is based on the integers stored in the buffer, not bytes as in ByteBuffer.
         * Thus the current position when we have read all 3 integers is 3 and limit is also 3.
         */
        log.info("IntBuffer view of the ByteBuffer position and limit:");
        printPositionWithLimit(intBuffer);
    }

    private static void readFileIntoBuffer(ByteBuffer buffer) throws IOException {
        try(FileChannel channel = FileChannel.open(WRITE_FILE,
                                                   StandardOpenOption.READ)){
            log.info("Clearing buffer");
            buffer.clear();
            printPositionWithLimit(buffer);
            log.info("Reading file and writing the file content to buffer using FileChannel");
            channel.read(buffer);
            printPositionWithLimit(buffer);
        }
    }

    private static void writeBufferToFile(ByteBuffer buffer) throws IOException {
        try(FileChannel channel = FileChannel.open(WRITE_FILE,
                                                   StandardOpenOption.CREATE,
                                                   StandardOpenOption.WRITE)){
            log.info("Calling flip() on buffer");
            buffer.flip();
            printPositionWithLimit(buffer);
            log.info("Reading buffer and writing the buffer content to file with FileChannel");
            channel.write(buffer);
            printPositionWithLimit(buffer);
        }
        log.info("File size: {}", Files.size(WRITE_FILE));
    }

    private static void putIntsToBuffer(ByteBuffer buffer) {
        List<Integer> integersWritten = IntStream.rangeClosed(1, 3).boxed().collect(Collectors.toList());
        log.info("Writing list of integers to byte buffer, list: {}", integersWritten);
        integersWritten.forEach(i -> buffer.putInt(i));
        printPositionWithLimit(buffer);
    }

    private static void printPositionWithLimit(Buffer buffer) {
        log.info("Position = {}", buffer.position());
        log.info("Limit    = {}", buffer.limit());
    }
}
