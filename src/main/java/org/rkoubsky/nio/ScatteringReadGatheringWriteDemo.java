package org.rkoubsky.nio;

import com.google.common.io.MoreFiles;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Radek Koubsky
 *
 * The Gather/Scatter pattern is useful when handling mesages with fixed-length parts
 */
public class ScatteringReadGatheringWriteDemo {
    public static final Path WRITE_DIR = Paths.get("scatter_gather_test");
    public static final Path WRITE_FILE = WRITE_DIR.resolve("write_file.bin");

    public static void main(String[] args) throws IOException {
        try {
            Files.createDirectories(WRITE_DIR);

            ByteBuffer header = ByteBuffer.allocate(1024);
            ByteBuffer body = ByteBuffer.allocate(4096);
            ByteBuffer footer = ByteBuffer.allocate(128);

            // Remember properly rewind the buffers
            ByteBuffer[] message = {header, body, footer};

            try (GatheringByteChannel gatheringByteChannel = FileChannel.open(WRITE_FILE, StandardOpenOption.CREATE,
                                                                              StandardOpenOption.WRITE)) {
                gatheringByteChannel.write(message);
            }

            try (ScatteringByteChannel scatteringByteChannel = FileChannel.open(WRITE_FILE, StandardOpenOption.READ)) {
                scatteringByteChannel.read(message);
            }

        } finally {
            MoreFiles.deleteRecursively(WRITE_DIR);
        }
    }
}
