# BasicThread Tutorial

Package `org.example.ThreadTutorial.BasicThread` berisi contoh-contoh dasar penggunaan **Thread** di Java, termasuk konsep **daemon threads**, **join**, **yield**, dan mekanisme **wait-notify**. Paket ini dirancang untuk membantu memahami dasar-dasar concurrency di Java.

## Struktur Package

- `ManualMain.java` – Contoh pembuatan thread manual menggunakan `Thread` class, daemon thread, `join`, dan `yield`.
- `MyThread.java` – Implementasi custom thread dengan cara me-*extend* `Thread`.
- `WaitAndNotify.java` – Contoh sinkronisasi antar thread menggunakan `wait()` dan `notify()`.
- `Yield.java` – Demonstrasi penggunaan `Thread.yield()` untuk memberikan kesempatan thread lain berjalan.

---

## 1. ManualMain.java

Menunjukkan:

- Membuat thread dengan cara meng-*extend* `Thread`.
- Mengubah nama thread menggunakan `setName()`.
- Menandai thread sebagai daemon menggunakan `setDaemon(true)`.
- Menggunakan `join()` agar thread utama menunggu thread lain selesai.
- Menggunakan `Thread.yield()` untuk memberi kesempatan thread lain berjalan.

Contoh output (sederhana):

```
ini tlisan ke 0X
ini adalah Thread : MainThread
Running in with name : My Thread Class kali-0
...
Main Finished
```

---

## 2. MyThread.java

Kelas ini adalah implementasi dasar **custom thread** dengan:

- Override method `run()`.
- Menggunakan `Thread.sleep()` untuk simulasi delay.
- Penanganan `InterruptedException` untuk menghentikan thread secara aman.
- Looping hingga kondisi tertentu (misal i > 10) lalu berhenti dengan aman.

---

## 3. WaitAndNotify.java

Simulasi **producer-consumer** (Koki dan Pelayan) dengan sinkronisasi:

- `Meja` – Shared resource yang digunakan koki dan pelayan.
- `masak()` – Method synchronized yang dijalankan koki untuk memasak.
- `ambil()` – Method synchronized yang dijalankan pelayan untuk mengambil makanan.
- `wait()` dan `notify()` digunakan untuk koordinasi antar thread, sehingga pelayan menunggu makanan siap dan koki menunggu meja kosong.

Contoh output:

```
Koki : Memasak Makanan...
Makanan Sudah Siap
Pelayan : Mengambil Makanan....
Pelayan : Makanan Kosong Lagi...
```

---

## 4. Yield.java

Menunjukkan penggunaan `Thread.yield()`:

- Memberikan kesempatan thread lain untuk berjalan.
- Tidak menjamin urutan eksekusi, tapi membantu scheduller OS memberi kesempatan thread lain.
- Cocok untuk mengurangi starvation di sistem yang sangat banyak thread.

---

## Catatan

- Gunakan daemon thread dengan hati-hati, karena mereka akan berhenti saat thread utama selesai.
- `join()` memastikan thread utama menunggu thread tertentu selesai sebelum melanjutkan.
- `wait()` dan `notify()` harus dipanggil dalam konteks synchronized agar sinkronisasi berjalan benar.
