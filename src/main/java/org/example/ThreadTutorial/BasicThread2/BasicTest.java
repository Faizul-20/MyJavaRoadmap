package org.example.ThreadTutorial.BasicThread2;

public class BasicTest {

    /*
    *Soal 1:
    Buat 3 thread menggunakan Thread (extend Thread) yang menampilkan:
    - Thread-1 sedang berjalan
    - Thread-2 sedang berjalan
    - Thread-3 sedang berjalan
    *
    Setiap thread menunggu 500ms antara cetakan.
    Jalankan ketiganya bersamaan.
    Tujuan: memahami cara membuat thread dan menjalankannya.
    *
    * */

    public static void main(String[] args) {
        NewThread().start();
        NewThread().start();
        NewThread().start();

    }

    static Thread NewThread(){
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i <= 5; i++) {
                System.out.println("Task " + i + " Berjalan Dengan " + Thread.currentThread().getName());
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){

                }
            }
        });
        return  thread1;
    }
}
