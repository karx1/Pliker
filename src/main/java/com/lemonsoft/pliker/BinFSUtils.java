package com.lemonsoft.pliker;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BinFSUtils {
    private static boolean byteInRange(byte value, int lowerBound, int upperBound) {
        return lowerBound <= value && value <= upperBound;
    }
    public static boolean stringIllegal(String string) {
        for (byte character : string.getBytes(StandardCharsets.US_ASCII)) {
            if (character == 95 || character == 45 || byteInRange(character, 48, 57)
                    || byteInRange(character, 65, 90)
                    || byteInRange(character, 97, 122) ) {
                continue;
            }
            return true;
        }
        return false;
    }

    private final File binfsdir;
    private Set<Bin> trackedBins;

    public BinFSUtils(File binfsdir) {
        if (!binfsdir.isDirectory()) {
            throw new IllegalArgumentException("binfsdir doesn't exist.");
        }
        this.binfsdir = binfsdir;
        this.trackedBins = new HashSet<>();
        for (var item : Objects.requireNonNull(binfsdir.listFiles())) {
            trackedBins.add(new Bin(item));
        }
    }
}
