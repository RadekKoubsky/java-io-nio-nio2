package org.rkoubsky.nio2.events;

import com.google.common.io.MoreFiles;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class WatchServiceDemo {
    public static final Path WRITE_DIR = Paths.get("watch_service_test");

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            Files.createDirectories(WRITE_DIR);

            Thread watchServiceThread = new Thread(() -> {
                try {
                    runWatchService();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            watchServiceThread.start();

            MoreFiles.touch(WRITE_DIR.resolve("file.txt"));
            MoreFiles.touch(WRITE_DIR.resolve("img.jpg"));
            MoreFiles.touch(WRITE_DIR.resolve("file.txt"));
            Path filesDir = WRITE_DIR.resolve("tmp_dir");
            Files.createDirectory(filesDir);
            // to watch events on this subdirectory, we have to register it with the watch service
            MoreFiles.touch(filesDir.resolve("not_watched_as_in_subdirectory.txt"));


            watchServiceThread.join(1_000L);
        } finally {
            MoreFiles.deleteRecursively(WRITE_DIR);
        }
    }

    private static void runWatchService() throws IOException, InterruptedException {
        FileSystem fileSystem = WRITE_DIR.getFileSystem();

        WatchService watchService = fileSystem.newWatchService();
        WatchKey writeDirKey = WRITE_DIR.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                                                  StandardWatchEventKinds.ENTRY_MODIFY,
                                                  StandardWatchEventKinds.ENTRY_DELETE);
        log.info("Watching directory: {}", WRITE_DIR);

        while (writeDirKey.isValid()){
            WatchKey writeDirKeyTaken = watchService.take();
            List<WatchEvent<?>> events = writeDirKeyTaken.pollEvents();

            for (WatchEvent<?> event : events) {
                if(event.kind() == StandardWatchEventKinds.OVERFLOW){

                } else if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
                    Path path = (Path) event.context();
                    log.info("Observed file creation: {} - {}", path, Files.probeContentType(path));
                } else if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
                    Path path = (Path) event.context();
                    log.info("Observed file modification: {} - {}", path, Files.probeContentType(path));
                } else if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
                    Path path = (Path) event.context();
                    log.info("Observed file deletion: {} - {}", path, Files.probeContentType(path));
                }
            }
            writeDirKeyTaken.reset();
        }

        log.info("Key is invalid.");
    }
}
