package org.example.ThreadTutorial.BasicThread;

public class ManualMain {
    public static void main(String[] args) throws InterruptedException {
        MyThread thread1 = new MyThread();
        thread1.setName("My Thread Class");
        //Jika Aku menjalankan daemon maka dia akan berjalan selama proses
        //Utama berjalan
        thread1.setDaemon(true);
        thread1.start();
       // Thread.sleep(3500);
        //Jika Ini di aktifkan Thread Main akan menunggu ini selesai dlu
//        System.out.println("Menunggu Thread Class Selesai : ");
//       thread1.join();
        Thread.yield();
        for (int i = 0; i < 5; i++) {
            System.out.println("ini tlisan ke " + i + "X");
            Thread.sleep(1000);
            Thread mainThread = Thread.currentThread();
            mainThread.setName("MainThread");
            System.out.println("ini adalah Thread : " + mainThread.getName());
        }
        System.out.println("Main Finished");


    }
}
