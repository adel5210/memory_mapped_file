package com.adel;

public class Main {
    public static void main(String[] args) {
        final String testData = "I can write this message.You can read this message.";

        final long time = System.currentTimeMillis();

        try (final MemoryMappedSrv ins = new MemoryMappedSrv()) {

            ins.quickWrite(testData);

            ins.waitAllAsync();
            System.out.println(ins.readAll());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("process time: " + (System.currentTimeMillis() - time));
        System.exit(0);
    }
}