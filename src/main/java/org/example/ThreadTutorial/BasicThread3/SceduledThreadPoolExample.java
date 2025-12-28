package org.example.ThreadTutorial.BasicThread3;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SceduledThreadPoolExample {

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        executor.scheduleAtFixedRate(() -> {
            System.out.println("Task Di jalankan : " + System.currentTimeMillis());

        },2,3, TimeUnit.SECONDS);
    }
}
