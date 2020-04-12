package org.rkoubsky.io.chars.reader;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class LineNumberReaderDemo {
    public static final Path TEXT_FILE = Paths.get("files/io/read_file.txt");

    public static void main(String[] args) throws IOException {

        /**
         * LineNumberReader extends BufferedReader.
         * It adds getLineNumber() method to return the number
         * of the current line.
         */
        try(LineNumberReader lineNumberReader  = new LineNumberReader(Files.newBufferedReader(TEXT_FILE,
                                                                                              StandardCharsets.UTF_8))){
            log.info("LineNumberReader: reading characters from file {}", TEXT_FILE);

            String line = lineNumberReader.readLine();

            while (line != null){
                log.info("Line number {}: {}", lineNumberReader.getLineNumber(), line);
                line = lineNumberReader.readLine();
            }
        }
    }
}
