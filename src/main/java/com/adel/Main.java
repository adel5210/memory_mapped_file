package com.adel;

public class Main {
    public static void main(String[] args) {
        final String testData = "I can write this message.";
        final String testData2 = "You can read this message.";

        final long time = System.currentTimeMillis();

        try (final MemoryMappedSrv ins = new MemoryMappedSrv()) {

            ins.writeAsync(testData, 0, testData.length());
            ins.writeAsync(testData2, (testData.length()+1), testData2.length());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("process time: " + (System.currentTimeMillis() - time));
    }
}