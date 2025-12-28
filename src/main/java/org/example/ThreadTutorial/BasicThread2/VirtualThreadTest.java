package org.example.ThreadTutorial.BasicThread2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualThreadTest {
    public static void main(String[] args) throws InterruptedException {
        // Membuat executor dengan Virtual Threads
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            int totalTasks = 100_000; // jumlah task virtual
            for (int i = 0; i < totalTasks; i++) {
                int taskNum = i;
                executor.submit(() -> {
                    // Simulasi task ringan
                    System.out.println("Running task #" + taskNum + " in " +
                            Thread.currentThread());
                });
            }

            // Shutdown executor dan tunggu semua task selesai
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }

        System.out.println("All tasks finished!");
    }
}
