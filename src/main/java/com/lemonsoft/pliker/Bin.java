package com.lemonsoft.pliker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Bin {
    public static class BinFile {
        private String author;
        private String name;
        // fnode <author> <name>
        private File descriptor;

        public String getName() {
            return name;
        }

        public String getAuthor() {
            return author;
        }

        public InputStream openFile() throws FileNotFoundException {
            return new FileInputStream(descriptor);
        }
    }

    private Set<BinFile> files = new HashSet<>();
    private String name;
    // fbin <author> <name>
    private File descriptor;

    // create new bin
    public Bin(File binspace, String binName) throws IOException {
        if (BinFSUtils.stringIllegal(binName)) {
            throw new IllegalArgumentException("Name %s contains illegal characters.".formatted(name));
        }
        name = "fbin %s".formatted(binName);
        descriptor = Path.of(binspace.getAbsolutePath(), binName).toFile();
        if (!descriptor.mkdir()) {
            throw new IOException("Couldn't create directory '%s'.".formatted(descriptor.getAbsolutePath()));
        }
    }

    // create new file
    public void createFile(String author, String filename, InputStream data) throws IOException {
        var binFile = new BinFile();
        if (BinFSUtils.stringIllegal(filename)) {
            throw new IllegalArgumentException("Name %s contains illegal characters.".formatted(name));
        }
        name = "fnode %s %s".formatted(author, filename);
        descriptor = Path.of(descriptor.getAbsolutePath(), filename).toFile();
        if (!descriptor.createNewFile()) {
            throw new IOException("Couldn't create file '%s'.".formatted(descriptor.getAbsolutePath()));
        }
        if (data != null) {
            try (data) {
                Files.copy(data, descriptor.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        binFile.author = author;
        files.add(binFile);
    }

    // load existing bin
    public Bin(File descriptor) {
        if (!descriptor.isDirectory()) {
            throw new IllegalArgumentException("descriptor has to represent an existing directory.");
        }
        var dirname = descriptor.getName();
        var nameParts = dirname.split(" ");
        if (nameParts.length != 2 || !nameParts[0].equals("fbin") || BinFSUtils.stringIllegal(nameParts[1])) {
            throw new IllegalArgumentException("%s is not a valid bin name.".formatted(dirname));
        }
        for (var file : Objects.requireNonNull(descriptor.listFiles())) {
            var filename = file.getName();
            var filenameParts = filename.split(" ");
            if (filenameParts.length != 3 || !filenameParts[0].equals("fnode") || BinFSUtils.stringIllegal(filenameParts[2])) {
                throw new IllegalArgumentException("%s is not a valid node name.".formatted(filename));
            }
            var binFile = new BinFile();
            binFile.descriptor = file;
            binFile.author = filenameParts[1];
            binFile.name = filenameParts[2];
            files.add(binFile);
        }
        this.descriptor = descriptor;
        this.name = nameParts[1];
    }

    public Set<BinFile> getFiles() {
        return files;
    }

    public String getName() {
        return name;
    }
}
