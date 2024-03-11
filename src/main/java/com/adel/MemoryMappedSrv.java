package com.adel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MemoryMappedSrv implements AutoCloseable {

    private final RandomAccessFile file;
    private final FileChannel fileChannel;
    private final MappedByteBuffer buffer;
    private final ExecutorService executors;
    private final List<CompletableFuture<Void>> completableFutures;

    //Create memory-mapped file
    public MemoryMappedSrv() {

        executors = Executors.newFixedThreadPool(2);
        completableFutures = new ArrayList<>();

        try {
            file = new RandomAccessFile("mmf.dat", "rw");
            fileChannel = file.getChannel();

            buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 1024);

            buffer.clear();
            fileChannel.close();
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(final String data, final int offset, final int length) {
        int pos = offset;
        int len = length;
        for (final char c : data.toCharArray()) {
            if (len == 0) break;

            this.writeC(c, pos);
            ++pos;
            --len;
        }
    }

    public void writeAsync(final String data, final int offset, final int length) {
        final CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> write(data, offset, length), executors);
        completableFutures.add(runAsync);
    }

    public void quickWrite(final String data) {
        final int leftIndex = 0;
        final int rightIndex = data.length() - 1;
        final int midIndex = leftIndex + (rightIndex - leftIndex) / 2;
        final String strSplit0 = data.substring(leftIndex, midIndex);
        final String strSplit1 = data.substring(midIndex, rightIndex);
        writeAsync(strSplit0, leftIndex, midIndex-leftIndex);
        writeAsync(strSplit1, midIndex, rightIndex-midIndex);
    }

    public String readAll() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            sb.append(readC(i));
        }
        return sb.toString();
    }

    public void waitAllAsync() {
        completableFutures.forEach(cf -> {
            try {
                cf.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        completableFutures.clear();
    }

    public void clear() {
        buffer.clear();
    }

    private void writeC(final char character, final int position) {
        buffer.put(position, (byte) character);
    }

    private char readC(final int position) {
        return (char) buffer.get(position);
    }

    @Override
    public void close() throws Exception {
        buffer.force();
        fileChannel.close();
        file.close();
    }
}
