package org.example.ThreadTutorial.BasicThread;

public class WaitAndNotify {
    static class Meja {
        private boolean makananSiap = false;

        public synchronized void masak() throws InterruptedException{
            while(makananSiap){
                wait();
            }
            System.out.println("Koki : Memasak Makanan..." );
            Thread.sleep(1000);

            makananSiap = true;
            System.out.println("Makanan Sudah Siap");
        }
        public synchronized void ambil() throws InterruptedException{
            while (!makananSiap){
                wait();
            }

            System.out.println("Pelayan : Mengambil Makanan....");

            Thread.sleep(1000);

            makananSiap = false;
            System.out.println("Pelayan : Makanan Kosong Lagi...");

            notify();
        }
    }

    static class Koki extends Thread{
        private final Meja meja;

        public Koki(Meja meja){
            this.meja = meja;
        }

        @Override
        public void run() {
            try{
                while (true){
                    meja.masak();
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Pelayan extends Thread{
        private final Meja meja;

        public Pelayan(Meja meja){
            this.meja = meja;
        }

        @Override
        public void run() {
            try{
                while (true){
                    meja.ambil();
                }

            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        Meja meja = new Meja();

        new Koki(meja).start();
        new Pelayan(meja).start();
    }
}
