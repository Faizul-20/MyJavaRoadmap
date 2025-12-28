package org.example.ThreadTutorial.BasicThread2;

import java.util.concurrent.*;

public class BasicTest2 {
    static class MyThread implements Callable<Integer>{
        int amount;
        int start;

        public MyThread(int amount,int start){
            this.amount = amount;
            this.start = start;
        }
        @Override
        public Integer call() throws InterruptedException {
            for (int i = 0; i <= 10; i++) {
                System.out.println("Thread " + Thread.currentThread().getName() + " Menambahkan " + i +" Jumlah Amount " + start);
                start+=i;
                Thread.sleep(1000);
            }

            return start;
        }

    }

    public static void main(String[] args) {
        Callable<Integer> callSatu = new MyThread(1,1);
        Callable<Integer> callDua = new MyThread(1,10);
        Callable<Integer> callTiga = new MyThread(1,20);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        FutureTask<Integer> task1 = new FutureTask<>(callSatu);
        FutureTask<Integer> task2 = new FutureTask<>(callDua);
        FutureTask<Integer> task3 = new FutureTask<>(callTiga);

        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        try {
            System.out.println("Nilai Hasil task1 : " + task1.get());
            System.out.println("Nilai Hasil task2 : " + task2.get());
            System.out.println("Nilai Hasil task3 : " + task3.get());
            executor.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }


    }
}
