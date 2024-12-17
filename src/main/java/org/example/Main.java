package org.example;

import java.util.Arrays;

public class Main {
    static final int SIZE = 400;
    static final int DEFAULT_THREADS_COUNT = 7;

    public static void main(String[] args) {

        float[] arr = new float[SIZE];
        Arrays.fill(arr, 1.0f);

        long startTime = System.nanoTime();
        Formula(arr);
        long endTime = System.nanoTime();
        Print(arr);
        System.out.println("\nВремя выполнения первого метода: " + (endTime - startTime) / 1_000_000 + " мс");


        arr = new float[SIZE];
        Arrays.fill(arr, 1.0f);
        startTime = System.nanoTime();
        Vich(arr);
        endTime = System.nanoTime();
        Print(arr);
        System.out.println("\nВремя выполнения второго метода: " + (endTime - startTime) / 1_000_000 + " мс");


        arr = new float[SIZE];
        Arrays.fill(arr, 1.0f);
        startTime = System.nanoTime();
        Vich2(arr, DEFAULT_THREADS_COUNT);
        endTime = System.nanoTime();
        Print(arr);
        System.out.println("\nВремя выполнения метода с " + DEFAULT_THREADS_COUNT + " потоками: " + (endTime - startTime) / 1_000_000 + " мс");
    }

    private static void Formula(float[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5.0) * Math.cos(0.2f + i / 5.0) * Math.cos(0.4f + i / 2.0));
        }
    }

    public static void Vich(float[] array) {
        Thread threadOne = new Thread(() -> {
            for (int i = 0; i < SIZE / 2; i++) {
                array[i] = (float) (array[i] * Math.sin(0.2f + i / 5.0) * Math.cos(0.2f + i / 5.0) * Math.cos(0.4f + i / 2.0));
            }
        });

        Thread threadTwo = new Thread(() -> {
            for (int i = SIZE / 2; i < array.length; i++) {
                array[i] = (float) (array[i] * Math.sin(0.2f + i / 5.0) * Math.cos(0.2f + i / 5.0) * Math.cos(0.4f + i / 2.0));
            }
        });

        threadOne.start();
        threadTwo.start();

        try {
            threadOne.join();
            threadTwo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void Vich2(float[] arr, int threadCount) {
        int chunkSize = arr.length / threadCount;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int startIndex = i * chunkSize;
            final int endIndex = (i == threadCount - 1) ? arr.length : startIndex + chunkSize;

            threads[i] = new Thread(() -> {
                Formula_V_Potoke(arr, startIndex, endIndex);


                if (Thread.currentThread().getId() == threads[threadCount - 1].getId()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Последний поток: ");
                    int limit = 4;
                    for (int j = startIndex; j < Math.min(limit, endIndex); j++) {
                        sb.append(arr[j]).append(" ");
                    }
                    sb.append("");
                    for (int j = Math.max(endIndex - limit, startIndex); j < endIndex; j++) {
                        sb.append(arr[j]).append(" ");
                    }
                    System.out.println(sb.toString().trim());
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void Formula_V_Potoke(float[] arr, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5.0) * Math.cos(0.2f + i / 5.0) * Math.cos(0.4f + i / 2.0));
        }
    }

    private static void Print(float[] arr) {
        System.out.println("Первая ячейка: " + arr[0]);
        System.out.println("Последняя ячейка: " + arr[arr.length - 1] + "\n");
    }
}
