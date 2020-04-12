package org.rkoubsky.io.chars.reader;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class InMemoryStringReaderDemo {

    public static void main(String[] args) throws IOException {
        final String stringToBeRead = "In memory text that will be read.\nThis is second line.";

        log.info("StringReader: reading text in memory by BufferedReader decorator");
        try(BufferedReader bufferedReader = new BufferedReader(new StringReader(stringToBeRead))){
            bufferedReader.lines().forEach(line -> log.info("Line: {}", line));
        }
    }
}
