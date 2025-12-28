package org.example.ThreadTutorial.BasicThread3;

public class BasicTest2 {
    static class SharedCount{
        private int amount = 0;

        public void increase(){
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(10);
                    amount+=1;
                    System.out.println("Thread : "
                    + Thread.currentThread().getName() + " Menambah " + 1 +
                            " Sehingga Amount : " + amount);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            System.out.println(Thread.currentThread().getName() + " : telah selesai...");
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    public static void main(String[] args) {
        SharedCount sharedCount = new SharedCount();
        Runnable task = () -> sharedCount.increase();
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        Thread thread3 = new Thread(task);
        Thread thread4 = new Thread(task);
        Thread thread5 = new Thread(task);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        try{
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
        }catch (InterruptedException e){

        }
    }
}
