package org.rkoubsky.nio2.filetree.visit;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class CustomFileVisitor implements FileVisitor<Path> {
    private final AtomicLong emptyDirs = new AtomicLong(0);
    private final Map<String, Long> fileTypes = new HashMap<>();

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
        /**
         *  findFirst().isPresent() is lazy evaluated, thus not causing to go through the whole stream
         *
         *  Do not use stream.count() != 0 which will go through the whole stream that can cause
         *  performance cost for large directories
         */

        boolean dirNotEmpty = StreamSupport.stream(directoryStream.spliterator(), false)
                                           .findFirst()
                                           .isPresent();

        if (dirNotEmpty){
            return FileVisitResult.CONTINUE;
        } else {
            this.emptyDirs.incrementAndGet();
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String fileType = Files.probeContentType(file);

        this.fileTypes.merge(fileType, 1L, (oldValue, newValue) -> oldValue + newValue);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        log.error("Failed to visit file: {}", file, exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public Long getEmptyDirs() {
        return this.emptyDirs.get();
    }

    public Map<String, Long> getFileTypes() {
        return Collections.unmodifiableMap(this.fileTypes);
    }
}
