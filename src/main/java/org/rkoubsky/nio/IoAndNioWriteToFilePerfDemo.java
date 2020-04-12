package org.rkoubsky.nio;

import com.google.common.io.MoreFiles;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.LongStream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class IoAndNioWriteToFilePerfDemo {
    public static final int CAPACITY_IN_BYTES = 10000008;
    public static final int LONGS = 12500000;
    public static final Path TEST_DIR = Paths.get("io_vs_nio_write_to_file");

    public static void main(String[] args) throws IOException {
        try {
            Files.createDirectories(TEST_DIR);
            Path ioFile = TEST_DIR.resolve("java_io_file.bin");
            Path nioFile = TEST_DIR.resolve("java_nio_file.bin");

            Instant start = Instant.now();
            writeJavaIO(ioFile);
            Instant finish = Instant.now();
            Duration ioDuration = Duration.between(start, finish);

            start = Instant.now();
            writeJavaNIO(nioFile);
            finish = Instant.now();
            Duration nioDuration = Duration.between(start, finish);

            log.info("Java IO duration : {}", ioDuration);
            log.info("Java NIO duration: {}", nioDuration);
            log.info("{} size: {}", ioFile, FileUtils.byteCountToDisplaySize(Files.size(ioFile)));
            log.info("{} size: {}", nioFile, FileUtils.byteCountToDisplaySize(Files.size(nioFile)));

        } finally {
            MoreFiles.deleteRecursively(TEST_DIR);
        }
    }

    private static void writeJavaIO(Path ioFile) throws IOException {
        try (DataOutputStream os = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(ioFile), CAPACITY_IN_BYTES))) {
            LongStream.range(0, LONGS)
                      .forEach(v -> {
                          try {
                              os.writeLong(v);
                          } catch (IOException e) {
                              e.printStackTrace();
                          }
                      });
        }
    }

    private static void writeJavaNIO(Path nioFile) throws IOException {
        try (FileChannel channel = FileChannel.open(nioFile,
                                                    StandardOpenOption.CREATE,
                                                    StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.allocate(CAPACITY_IN_BYTES);

            LongStream.rangeClosed(1, LONGS)
                      .forEach(v -> {
                          try {
                              buffer.putLong(v);
                              if (v % 1250000 == 0) {
                                  while (buffer.hasRemaining()) {
                                      /**
                                       * To correctly read from this buffer filled with ints,
                                       * we need to set the limit to the current position of the cursor
                                       * and then rewind the cursor (set the current position to zero).
                                       *
                                       * This is exactly what the flip() methods does. The flip method
                                       * resets the cursor and prevents readings past what has been
                                       * written into the buffer.
                                       *
                                       * Buffer before flip:
                                       *    1
                                       *    2
                                       *    3
                                       *    <--- cursor
                                       *    Nothing will be read from the buffer. This is not what we want.
                                       *
                                       * Buffer after flip:
                                       *    <--- cursor
                                       *    1
                                       *    2
                                       *    3
                                       *    <--- limit
                                       *    Only ints from the cursor position to the limit will be read.
                                       *    This is what we want.
                                       */
                                      buffer.flip();
                                      channel.write(buffer);
                                  }

                                  /**
                                   * Once we are done reading the ints from the buffer
                                   * (which is done by the channel.write), we need to
                                   * clear the buffer which resets the position and limit.
                                   */
                                  buffer.clear();
                              }
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                      });
        }
    }
}
