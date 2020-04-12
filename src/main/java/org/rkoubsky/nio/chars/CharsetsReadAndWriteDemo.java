package org.rkoubsky.nio.chars;

import com.google.common.io.MoreFiles;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class CharsetsReadAndWriteDemo {
    public static final Path WRITE_DIR = Paths.get("charsets_test");
    public static final Path LATIN1_FILE = WRITE_DIR.resolve("hello-latin1.txt");
    public static final Path UTF8_FILE = WRITE_DIR.resolve("hello-utf8.txt");
    public static final int CAPACITY = 1024 * 1024;
    public static final String TEXT_TO_WRITE = "Hello world from Jan Novák";

    public static void main(String[] args) throws IOException {
        try {
            Files.createDirectories(WRITE_DIR);

            log.info("Text length = {}", TEXT_TO_WRITE.length());

            ByteBuffer readByteBuffer = ByteBuffer.allocate(CAPACITY);
            CharBuffer writeCharBuffer = CharBuffer.allocate(CAPACITY);
            writeCharBuffer.put(TEXT_TO_WRITE);

            writeCharsToFile(StandardCharsets.ISO_8859_1, writeCharBuffer, LATIN1_FILE);
            readCharsFromFile(StandardCharsets.ISO_8859_1, readByteBuffer, LATIN1_FILE);

            writeCharsToFile(StandardCharsets.UTF_8, writeCharBuffer, UTF8_FILE);
            readCharsFromFile(StandardCharsets.UTF_8, readByteBuffer, UTF8_FILE);

        } finally {
            MoreFiles.deleteRecursively(WRITE_DIR);
        }
    }

    private static void writeCharsToFile(Charset charset, CharBuffer charBuffer, Path file) throws IOException {
        log.info("Writing text: '{}' to file {} using charset: {}", TEXT_TO_WRITE, file, charset);
        try(FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.CREATE,
                                                       StandardOpenOption.WRITE)){
            charBuffer.flip();
            fileChannel.write(charset.encode(charBuffer));
        }

        log.info("File size: {}", Files.size(file));
    }

    private static void readCharsFromFile(Charset charset, ByteBuffer byteBuffer, Path file) throws IOException {
        log.info("Reading text from file {} using charset: {}", file, charset);
        try(FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ)){
            fileChannel.read(byteBuffer);
        }
        byteBuffer.flip();
        CharBuffer charBuffer = charset.decode(byteBuffer);
        log.info("Text read from the file: {}", charBuffer.toString());
        byteBuffer.clear();
    }
}
