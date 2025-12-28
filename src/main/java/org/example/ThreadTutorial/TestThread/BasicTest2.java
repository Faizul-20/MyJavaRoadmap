package org.example.ThreadTutorial.TestThread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BasicTest2 {
    static class Producer extends Thread {
        private final BlockingQueue<Integer> queue;

        public Producer(BlockingQueue<Integer> queue,String name){
            this.queue = queue;
            Thread.currentThread().setName(name);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + " : Menambah Dish....");
                    queue.put(1);  // blocking jika queue penuh
                    System.out.println("Dish Yang ada : " + queue.size());
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Consumer extends Thread {
        private final BlockingQueue<Integer> queue;

        public Consumer(BlockingQueue<Integer> queue,String name){
            this.queue = queue;
            Thread.currentThread().setName(name);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + " : Mengambil Dish....");
                    queue.take();  // blocking jika queue kosong
                    System.out.println("Dish Yang ada : " + queue.size());
                    Thread.sleep(700);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        // Membuat Producer
        Thread producer1 = new Producer(queue, "Producer-1");
        //Thread producer2 = new Producer(queue, "Producer-2");

        // Membuat Consumer
        Thread consumer1 = new Consumer(queue, "Consumer-1");
        Thread consumer2 = new Consumer(queue, "Consumer-2");

        // Menjalankan semua thread
        producer1.start();
        //producer2.start();
        consumer1.start();
        consumer2.start();
    }

}
