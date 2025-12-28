package org.example.ThreadTutorial.BasicThread2;

public class ManualMain {
    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = new MyRunnable();
        Runnable runnable1 = new MyRunnable();
        Runnable runnable2 = new MyRunnable();
        Thread thread = new Thread(runnable);
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        thread2.start();
        thread.start();
        thread1.start();
        int i=0;
        while(true) {
            if (i > 10) break;
            Thread.sleep(1000);
            System.out.println("Main Thread : " + Thread.currentThread().getName() +" ke-" + i);
            i++;
        }
    }
}
