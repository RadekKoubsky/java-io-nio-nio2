package org.rkoubsky.nio2.filesystem;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.MoreFiles;
import lombok.extern.log4j.Log4j2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Radek Koubsky
 */
@Log4j2
public class JarFileSystemDemo {
    public static final Path WRITE_DIR = Paths.get("jar_file_system_test");
    public static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));

    public static void main(String[] args) throws IOException {
        try {
            Files.createDirectories(WRITE_DIR);

            URI zip = URI.create(String.format("jar:file:///%s", CURRENT_DIR.resolve(WRITE_DIR).resolve("archive.zip")));

            try(FileSystem zipFS = FileSystems.newFileSystem(zip, ImmutableMap.of("create", "true"))){

                // copy file from file system to zip file system
                String dirInZip = "books";
                zipFS.provider().createDirectory(zipFS.getPath(dirInZip));

                Path source = Paths.get("files/nio2/into_the_wild.txt");
                Path target = zipFS.getPath(String.format("%s/into_the_wild_compressed.txt", dirInZip));
                Files.copy(source, target);

                // create file and write data directly in zip file system
                Path binDir = zipFS.getPath("bin");
                Path binFile = zipFS.getPath("bin/ints.bin");
                zipFS.provider().createDirectory(binDir);

                try(DataOutputStream dos = new DataOutputStream(zipFS.provider()
                                                                     .newOutputStream(binFile,
                                                                                      StandardOpenOption.CREATE_NEW,
                                                                                      StandardOpenOption.WRITE))) {
                    dos.writeInt(1);
                    dos.writeInt(2);
                    dos.writeInt(3);
                }
            }

        } finally {
            MoreFiles.deleteRecursively(WRITE_DIR);
        }
    }
}
