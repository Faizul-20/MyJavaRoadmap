package org.example.ThreadTutorial.BasicThread3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcecutorImp {
    //Contoh Implementasi Thread dimana Dia akan membuat fixed Thread
    // jadi tidak ada pembuatan Thread Baru
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 100; i++) {
            int taskId = i;
            executorService.submit(() ->{
                System.out.println("Task " + taskId + " Dijalankan " + Thread.currentThread().getName());
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){

                }
            });
        }
        executorService.shutdown();
    }
}
