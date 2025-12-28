package org.example.ThreadTutorial.TestThread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


//Error
public class ProducerConsumer {

    static class Dish{
        private static BlockingQueue<Integer> blockingQueue;

        public void addDish(){
            while (blockingQueue.size() <= 10){
                try {
                    System.out.println(Thread.currentThread().getName() + " : " + "Menambah Dish...." );
                    Thread.sleep(1000);
                    blockingQueue.add(1);
                    System.out.println("Dish Yang ada : " + blockingQueue.size());

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

            if (blockingQueue.size() > 0){
                Thread.currentThread().notify();
            }

        }

        public void removeDish(){
            while (blockingQueue.size() >= 10){
                try {
                    System.out.println(Thread.currentThread().getName() + " : " + "Mengambil Dish...." );
                    Thread.sleep(1000);
                    blockingQueue.add(1);
                    System.out.println("Dish Yang ada : " + blockingQueue.size());

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

            if (blockingQueue.size() < 10){
                Thread.currentThread().notify();
            }
        }

    }

    static class Producer extends Thread{
        private final Dish dish;

        public Producer(Dish dish){
            this.dish = dish;
        }

        @Override
        public void run() {
            dish.addDish();
        }


    }

    static class Consument extends Thread{
        private final Dish dish;

        public Consument(Dish dish){
            this.dish = dish;
        }

        @Override
        public void run() {
            dish.removeDish();
        }
    }

    public static void main(String[] args) {
        Dish dish = new Dish();
        Thread thread1 = new  Producer(dish);
        Thread thread2 = new Consument(dish);

        thread1.start();
        thread2.start();
    }
}
