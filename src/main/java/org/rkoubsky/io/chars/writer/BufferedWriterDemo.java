package org.rkoubsky.io.chars.writer;

import com.google.common.io.MoreFiles;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class BufferedWriterDemo {

    public static final Path WRITE_DIR = Paths.get("writer_test");
    public static final Path WRITE_FILE = WRITE_DIR.resolve("write_file.txt");

    public static void main(String[] args) throws IOException {
        try {
            Files.createDirectories(WRITE_DIR);

            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(WRITE_FILE, StandardCharsets.UTF_8)) {

                bufferedWriter.write("This text is written to a file using BufferedWriter.\nThis is second line.");
                log.info("Text has been written to a file: {}", WRITE_FILE);
            }

            log.info("Reading the content of the file from disk, file content:");
            Files.readAllLines(WRITE_FILE)
                 .forEach(line -> log.info("Line: {}", line));

        } finally {
            MoreFiles.deleteRecursively(WRITE_DIR);
        }
    }
}
