package org.example.ThreadTutorial.BasicThread2;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableAndFutureTask {

    // Dengan Implement ini Dia bisa Return sesuatu
    static class MyCallable implements Callable<String>{

        @Override
        public String call() throws Exception {
            //Simulasi Thread Berat
            Thread.sleep(1000);
            return "Task Selesai";
        }
    }

    public static void main(String[] args) {
        Callable<String> callable = new MyCallable();

        FutureTask<String> futureTask = new FutureTask<>(callable);

        Thread thread = new Thread(futureTask);

        thread.start();

        System.out.println("Main Thread Tetap Berjalan....");

        String result = null;
        try {
            result = futureTask.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Hasil Task : " + result);
    }

}
