package org.peergos.cbor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Benchmark {

    public static void main(String[] args) throws IOException {
        // each file is a single object
        List<Path> paths = List.of(
                Paths.get("data", "canada.json.dagcbor"),
                Paths.get("data", "citm_catalog.json.dagcbor"),
                Paths.get("data", "twitter.json.dagcbor"));
        for (int i=0; i < 10; i++) {
            for (Path path : paths) {
                decodeBenchmark(Files.readAllBytes(path), path.getFileName().toString());
            }
            for (Path path : paths) {
                encodeBenchmark(Files.readAllBytes(path), path.getFileName().toString());
            }
        }
    }

    private static void decodeBenchmark(byte[] raw, String name) throws IOException {
        long t0 = System.currentTimeMillis();
        int count = 100;
        for (int i=0; i < count; i++) {
            CborDecoder decoder = new CborDecoder(new ByteArrayInputStream(raw));
            CborObject cbor = CborObject.deserialize(decoder, raw.length);
        }
        long duration = System.currentTimeMillis() - t0;
        System.out.println("Decode rate " + (count * raw.length)/1_000/duration + " MB/s for " + name);
    }

    private static void encodeBenchmark(byte[] raw, String name) throws IOException {
        CborDecoder decoder = new CborDecoder(new ByteArrayInputStream(raw));
        CborObject cbor = CborObject.deserialize(decoder, raw.length);
        int count  = 100;
        long t2 = System.currentTimeMillis();
        for (int i=0; i < count; i++) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(raw.length);
            CborEncoder encoder = new CborEncoder(bout);
            cbor.serialize(encoder);
        }
        long encodeDuration = System.currentTimeMillis() - t2;
        System.out.println("Encode rate " + (count * raw.length)/1_000/encodeDuration + " MB/s for " + name);
    }
}
