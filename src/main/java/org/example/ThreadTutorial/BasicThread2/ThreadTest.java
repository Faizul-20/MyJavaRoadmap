package org.example.ThreadTutorial.BasicThread2;

public class ThreadTest {
    public static void main(String[] args) {
        int count = 0;

        try {
            while (true) {
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(Long.MAX_VALUE); // Thread "hidup" terus
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                t.start();
                count++;
                if (count % 100 == 0) {
                    System.out.println("Threads created: " + count);
                }
            }
        } catch (OutOfMemoryError | java.lang.InternalError e) {
            System.out.println("Failed to create thread at count: " + count);
            e.printStackTrace();
        }
    }
}
