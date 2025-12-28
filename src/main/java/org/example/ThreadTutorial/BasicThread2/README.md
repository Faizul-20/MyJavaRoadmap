# ThreadTutorial.BasicThread2

## Deskripsi
Proyek ini berisi berbagai contoh dan latihan tentang **Thread, Runnable, Callable, FutureTask, dan ExecutorService** di Java.  
Tujuannya untuk memahami **konsep dasar hingga lanjutan tentang concurrency** dan bagaimana Java menangani multi-threading, termasuk **virtual threads**.

---

## Struktur Paket

- **ManualMain**
    - Menunjukkan penggunaan beberapa `Runnable` dalam thread terpisah.
    - Menjalankan thread bersamaan dengan thread utama (`main`) yang tetap berjalan.

- **CallableAndFutureTask**
    - Contoh penggunaan `Callable` dan `FutureTask` untuk mendapatkan return value dari thread.
    - Menunjukkan bagaimana main thread tetap berjalan sambil menunggu hasil task.

- **BasicTest**
    - Soal 1: Membuat 3 thread (meng-extend `Thread`) dan menampilkan aktivitasnya.
    - Setiap thread menunggu 500ms per iterasi.
    - Tujuan: memahami cara membuat thread dan menjalankannya secara bersamaan.

- **BasicTest2**
    - Contoh **Callable dengan ExecutorService dan FutureTask**.
    - 3 task menambahkan angka berurutan ke start value masing-masing.
    - Memahami thread pool, submit task, dan mengambil hasil task dengan `get()`.

- **ThreadTest**
    - Contoh membuat banyak thread hingga mencapai limit resource.
    - Digunakan untuk memahami batas maksimal thread dan efek memori.

- **VirtualThreadTest**
    - Contoh penggunaan **virtual thread (Java 21)** menggunakan `Executors.newVirtualThreadPerTaskExecutor()`.
    - Menjalankan 100.000 task ringan untuk menunjukkan efisiensi virtual thread dibanding thread tradisional.

---

## Konsep yang Dipelajari

1. **Thread dasar**
    - Membuat thread dengan `extends Thread` dan `Runnable`.
    - Menjalankan beberapa thread secara bersamaan.

2. **Callable & FutureTask**
    - Mendapatkan nilai kembali dari thread.
    - Menangani `ExecutionException` dan `InterruptedException`.

3. **ExecutorService**
    - Thread pool: mengatur jumlah thread untuk menjalankan task.
    - Men-submit task menggunakan `submit()` dan menunggu hasil dengan `FutureTask.get()`.
    - `shutdown()` dan `awaitTermination()` untuk menghentikan executor secara aman.

4. **Race Condition & Sinkronisasi**
    - Contoh sederhana menunjukkan kenapa akses shared resource perlu `synchronized`.

5. **Virtual Threads (Java 21)**
    - Membuat thread ringan dalam jumlah sangat besar.
    - Thread pool virtual berbeda dengan thread tradisional, lebih efisien untuk task ringan.

---
