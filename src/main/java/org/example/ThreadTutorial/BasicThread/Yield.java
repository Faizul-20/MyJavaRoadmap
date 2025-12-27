package org.example.ThreadTutorial.BasicThread;

public class Yield {
    static class Mythread extends Thread{
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                Thread.yield();
                System.out.println(getName() + " Thread Running...");
            }
        }
    }

    public static void main(String[] args) {
        Mythread mythread = new Mythread();
        mythread.start();
        for (int i = 0; i <= 100; i++) {
            System.out.println("Main " + i);
        }
    }
}
