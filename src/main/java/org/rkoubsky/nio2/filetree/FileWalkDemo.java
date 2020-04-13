package org.rkoubsky.nio2.filetree;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class FileWalkDemo {
    public static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));

    public static void main(String[] args) throws IOException {
        log.info("Exploring tree in dir: {}", CURRENT_DIR);

        countAllDirsAndFiles();
        countDirs();
        countFiles();
        countJavaFiles();
    }

    private static void countAllDirsAndFiles() throws IOException {

        Stream<Path> result = Files.find(CURRENT_DIR, Integer.MAX_VALUE, ((path, attr) -> true));
        log.info("Total number of directories and files: {}", result.count());
    }

    private static void countDirs() throws IOException {
        Stream<Path> result = Files.find(CURRENT_DIR, Integer.MAX_VALUE, ((path, attr) -> attr.isDirectory()));
        log.info("Total number of directories: {}", result.count());
    }

    private static void countFiles() throws IOException {
        Stream<Path> result = Files.find(CURRENT_DIR, Integer.MAX_VALUE, ((path, attr) -> attr.isRegularFile()));
        log.info("Total number of files: {}", result.count());
    }


    private static void countJavaFiles() throws IOException {
        Stream<Path> result = Files.find(CURRENT_DIR, Integer.MAX_VALUE, ((path, attr) -> path.toString().endsWith(".java")));
        log.info("Total number of .java files: {}", result.count());

        Stream<Path> result2 = Files.find(CURRENT_DIR, Integer.MAX_VALUE, ((path, attr) -> path.toString().endsWith(".class")));
        log.info("Total number of .class files: {}", result2.count());
    }
}
