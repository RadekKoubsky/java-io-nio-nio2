package org.rkoubsky.nio2.filetree.visit;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class VisitingDirectoriesDemo {
    public static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));

    public static void main(String[] args) throws IOException {
        CustomFileVisitor customFileVisitor = new CustomFileVisitor();

        Files.walkFileTree(CURRENT_DIR, customFileVisitor);

        log.info("Number of empty dirs: {}", customFileVisitor.getEmptyDirs());
        log.info("File types count:");
        customFileVisitor.getFileTypes().forEach((k, v) -> log.info("{} -> {}", k, v));
    }
}
