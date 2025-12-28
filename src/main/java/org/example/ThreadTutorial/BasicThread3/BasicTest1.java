package org.example.ThreadTutorial.BasicThread3;

public class BasicTest1 {
    /**
     * Soal 4:
     * Buat class BankAccount dengan int balance = 1000
     * Buat 2 thread yang melakukan withdraw 700 sebanyak satu kali.
     * Gunakan synchronized agar saldo tidak bisa negatif.
     * Cetak setiap kali withdraw berhasil atau gagal.
     * @return : memahami race condition dan synchronized.
     * */

    static class Bank{
        private int saldo = 1000;

        public synchronized boolean withdraw(int amount){
            if (saldo >= amount){
                System.out.println(Thread.currentThread().getName() + " Mengambil saldo sebesar " + amount);
                saldo-= amount;
                return true;
            }else {
                System.out.println(Thread.currentThread().getName() + " Gagal Mengambil Saldo sebesar " + amount);
                return false;
            }
        }
        public int getSaldo(){
            return saldo;
        }


    }
    public static void main(String[] args) {
        Bank account = new Bank();

        Runnable task = () -> account.withdraw(700);
        Thread t1 = new Thread(task,"Thread-1");
        Thread t2 = new Thread(task,"Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Isi saldo bank : " + account.getSaldo());
    }
}
