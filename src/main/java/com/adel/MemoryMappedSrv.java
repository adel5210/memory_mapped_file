package com.adel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedSrv implements AutoCloseable {

    private final RandomAccessFile file;
    private final FileChannel fileChannel;
    private final MappedByteBuffer buffer;

    //Create memory-mapped file
    public MemoryMappedSrv() {
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

    public void write(final char character, final int position) {
        buffer.put(position, (byte) character);
    }

    public char read(final int position) {
        return (char) buffer.get(position);
    }

    @Override
    public void close() throws Exception {
        buffer.force();
        fileChannel.close();
        file.close();
    }
}
