package org.rkoubsky.io.chars.reader;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class BufferedReaderDemo {
    public static final Path TEXT_FILE = Paths.get("files/io/read_file.txt");

    public static void main(String[] args) throws IOException {

        readByChar();

        readLines();
    }

    private static void readByChar() throws IOException {
        try(BufferedReader bufferedReader = Files.newBufferedReader(TEXT_FILE, StandardCharsets.UTF_8)){
            log.info("BufferedReader#readByChar: reading characters from file {}", TEXT_FILE);
            int nextChar = bufferedReader.read();

            while (nextChar != -1){
                if((char) nextChar == '\n'){
                    log.info("Char (new line): {}", (char) nextChar);
                } else {
                    log.info("Char: {}", (char) nextChar);
                }
                nextChar = bufferedReader.read();
            }
        }
    }

    private static void readLines() throws IOException {
        try(BufferedReader bufferedReader = Files.newBufferedReader(TEXT_FILE, StandardCharsets.UTF_8)){
            log.info("");
            log.info("BufferedReader#readLines: reading lines from file {}", TEXT_FILE);

            bufferedReader.lines().forEach(line -> log.info("Line: {}", line));
        }
    }

}
