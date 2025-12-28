# BasicThread3 – Java Thread & Executor Fundamentals

## Deskripsi

Package `BasicThread3` berisi **contoh-contoh fundamental multithreading di Java** yang dirancang untuk memahami konsep inti berikut:

* Race condition
* `synchronized` dan data consistency
* Thread lifecycle (`start`, `join`)
* Executor Framework (`ExecutorService`)
* Perbedaan tipe thread pool

Seluruh contoh **menggunakan Java Standard Library**, tanpa framework eksternal, sehingga cocok sebagai fondasi sebelum mempelajari:

* Java Concurrency tingkat lanjut
* Web server (Servlet / Spring)
* Asynchronous & reactive programming

---

## Struktur Package

```
org.example.ThreadTutorial.BasicThread3
│
├── BasicTest1.java
├── BasicTest2.java
├── CachedThreadPoolExample.java
├── ExcecutorImp.java
├── SceduledThreadPoolExample.java
└── SingleThreadExecutorExample.java
```

---

## Penjelasan Kelas

## 1. `BasicTest1` – Race Condition & `synchronized`

Simulasi **rekening bank** dengan saldo awal `1000`.

### Skenario

* Dua thread melakukan `withdraw(700)` satu kali
* Tanpa `synchronized`, saldo berpotensi menjadi negatif
* Dengan `synchronized`, hanya satu thread boleh mengakses method `withdraw` dalam satu waktu

### Konsep yang Dipelajari

* Race condition
* Mutual exclusion
* Atomicity pada critical section

### Perilaku

* Satu thread berhasil withdraw
* Thread lainnya gagal karena saldo tidak mencukupi

Ini adalah **contoh klasik concurrency bug** dan cara paling dasar untuk mencegahnya.

---

## 2. `BasicTest2` – Shared Mutable State (Tanpa Sinkronisasi)

Simulasi beberapa thread yang **mengubah variabel bersama (`amount`) tanpa `synchronized`**.

### Skenario

* 5 thread
* Masing-masing menaikkan counter 1000 kali
* Disisipkan `Thread.sleep()` untuk memperbesar peluang race condition

### Konsep yang Dipelajari

* Lost update
* Non-deterministic output
* Bahaya shared mutable state

Catatan penting:

> Nilai akhir `amount` **tidak dapat diprediksi** dan hampir pasti salah.

Ini **bukan bug kode**, tetapi bug desain concurrency.

---

## 3. `CachedThreadPoolExample` – `newCachedThreadPool`

Menggunakan `ExecutorService` dengan cached thread pool.

### Karakteristik

* Thread dibuat **secara dinamis**
* Thread idle akan di-reuse
* Cocok untuk task kecil dan singkat

### Risiko

* Jika task terlalu banyak → potensi **resource exhaustion**

Digunakan untuk memahami **kenapa manajemen thread manual itu berbahaya**.

---

## 4. `ExcecutorImp` – Fixed Thread Pool

Contoh implementasi `Executors.newFixedThreadPool(3)`.

### Karakteristik

* Jumlah thread **tetap (3)**
* Task lain akan **menunggu di queue**
* Lebih stabil dan terkontrol

Ini adalah **thread pool paling umum** untuk aplikasi server.

---

## 5. `SceduledThreadPoolExample` – Scheduled Executor

Menggunakan `ScheduledExecutorService`.

### Perilaku

* Task dijalankan berulang
* Delay awal: 2 detik
* Interval eksekusi: 3 detik

### Use Case

* Scheduler
* Heartbeat
* Periodic cleanup task

---

## 6. `SingleThreadExecutorExample` – Single Thread Execution

Menggunakan `Executors.newSingleThreadExecutor()`.

### Karakteristik

* Semua task dijalankan **secara berurutan**
* Tidak ada parallelism
* Menjamin urutan eksekusi

Cocok untuk:

* Logging
* Event queue
* Task yang **tidak thread-safe**

---

## Konsep Inti yang Dirangkum

| Konsep          | Contoh                 |
| --------------- | ---------------------- |
| Race Condition  | BasicTest1, BasicTest2 |
| synchronized    | BasicTest1             |
| Shared State    | BasicTest2             |
| ExecutorService | Semua Executor Example |
| Thread Pool     | Cached, Fixed, Single  |
| Scheduling      | ScheduledThreadPool    |

---

## Kesalahan Umum yang Sengaja Ditunjukkan

* Mengubah shared variable tanpa sinkronisasi
* Menganggap `sleep()` sebagai solusi concurrency
* Mengira banyak thread = performa lebih baik

Contoh-contoh ini **edukatif, bukan best practice final**.

---

## Rekomendasi Lanjutan

Jika ingin naik level:

1. Tambahkan `synchronized` ke `BasicTest2`
2. Gunakan `AtomicInteger`
3. Bandingkan `synchronized` vs `Lock`
4. Pelajari `CompletableFuture`
5. Hubungkan dengan kasus web server

---
