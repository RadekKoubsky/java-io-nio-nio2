package org.rkoubsky.io.chars.writer;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class InMemoryStringWriterDemo {

    public static void main(String[] args) throws IOException {

        try(StringWriter stringWriter = new StringWriter()){

            stringWriter.write("This text is written to a string buffer using StringWriter.");

            log.info("Text has been written to a string buffer.");
            log.info("Content of the string buffer: {}", stringWriter.toString());
        }
    }
}
