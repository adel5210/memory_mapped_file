package com.adel;

public class Main {
    public static void main(String[] args) {
        final String testData = "I can write this message";

        try (final MemoryMappedSrv ins = new MemoryMappedSrv()) {

            int pos = 0;
            for (final char c : testData.toCharArray()) {
                ins.write(c, pos++);
            }

            for (int i = 0; i < pos; i++) {
                System.out.print(ins.read(i));
            }
            System.out.println();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}