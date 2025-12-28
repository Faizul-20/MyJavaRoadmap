package org.example.ThreadTutorial.BasicThread3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPoolExample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i <= 30; i++) {
            int taskId = i;
            executor.submit(()->{
                System.out.println("Task " + taskId + " di kerjakan oleh " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){

                }
            });
        }
    }
}
