package org.rkoubsky.nio2.filesystem;

import com.google.common.io.MoreFiles;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class DiskFileSystemDemo {
    public static final Path WRITE_DIR = Paths.get("filesystems_test");

    public static void main(String[] args) throws IOException {
        try {
            Files.createDirectories(WRITE_DIR);
            printProviders();

            FileSystem fileSystem = FileSystems.getDefault();
            createDir(fileSystem);
            // root dirs are all declared elements for read/write
            printRootDris(fileSystem);
            // stores are only mounted stores in this machine
            printFileStores(fileSystem);

        } finally {
            MoreFiles.deleteRecursively(WRITE_DIR);
        }
    }

    private static void printFileStores(FileSystem fileSystem) {
        log.info("File stores info:");
        fileSystem.getFileStores().forEach(store -> {
            try {
                log.info("name={}, type={}, totalSpace={}, unallocatedSpace={}, usableSpace={}", store.name(), store.type(),
                         FileUtils.byteCountToDisplaySize(store.getTotalSpace()),
                         FileUtils.byteCountToDisplaySize(store.getUnallocatedSpace()),
                         FileUtils.byteCountToDisplaySize(store.getUsableSpace()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void printRootDris(FileSystem fileSystem) {
        log.info("Root directories:");
        fileSystem.getRootDirectories().forEach(dir -> log.info("{}", dir));
    }

    private static void createDir(FileSystem fileSystem) throws IOException {
        FileSystemProvider provider = fileSystem.provider();
        /**
         * The Path is always bound to a file system
         *
         * 1) Create path by default file system
         * Paths.get("my_path") uses FileSystems.getDefault().getPath("my_path") which will use the default provider.
         *
         * 2) Create path by a filesystem provider defined by URI
         * To create Path by a different provider, use Paths.get(URI.create("file:///my_path")) which
         * will use provider based on the URI scheme (file:/// for file system provider)
         *
         * 3) Create path by a specific file system object
         * To create path for a specific file system, use fileSystem.getPath("my_path");
         */
        Path tmpDir = WRITE_DIR.resolve("created_by_provider_dir");
        provider.createDirectory(tmpDir);
    }

    private static void printProviders() {
        log.info("FileSystemProviders:");
        final List<FileSystemProvider> providers = FileSystemProvider.installedProviders();
        providers.forEach(p -> log.info("{}", p));

        FileSystemProvider linux = providers.get(0);

        FileSystem fileSystem1 = FileSystems.getDefault();
        log.info("FileSystem: {}", fileSystem1);

        FileSystem fileSystem2 = FileSystems.getFileSystem(URI.create("file:///"));
        log.info("FileSystem: {}", fileSystem2);

    }
}
