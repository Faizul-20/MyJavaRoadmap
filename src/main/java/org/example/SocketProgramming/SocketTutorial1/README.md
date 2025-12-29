# SocketTutorial1

## Deskripsi

`SocketTutorial1` adalah contoh implementasi **Socket Programming dasar di Java** menggunakan API `java.net.Socket` dan `java.net.ServerSocket`. Package ini bertujuan untuk membantu memahami:

* Komunikasi client–server berbasis TCP
* Blocking I/O menggunakan `BufferedReader` dan `PrintWriter`
* Manajemen thread sederhana untuk menjalankan server
* Alur hidup koneksi (connect → communicate → close)

Proyek ini **tidak menggunakan framework** (Spring, Netty, dsb.) sehingga cocok sebagai fondasi sebelum masuk ke HTTP, Servlet, atau Spring Boot.

---

## Struktur Package

```
org.example.SocketProgramming.SocketTutorial1
|
├── Client.java
├── Server.java
├── Server2.java
├── ManualMain.java
├── ManualMain2.java
└── ManualMain3.java
```

---

## Penjelasan Kelas

### 1. `Client`

Berperan sebagai **TCP client**.

Tanggung jawab utama:

* Membuka koneksi ke server (`Socket`)
* Mengirim pesan ke server
* Menerima balasan dari server
* Menutup koneksi dengan aman

Metode penting:

* `startConnection(String ip, int port)` → Membuka koneksi
* `sendMessage(String message)` → Kirim pesan & terima respons
* `stopConnection()` → Menutup semua resource

Catatan teknis:

* `readLine()` bersifat **blocking**
* Hanya mendukung **1 server – 1 client**

---

### 2. `Server`

Server otomatis dengan **logika respons sederhana**.

Perilaku:

* Menerima koneksi client
* Membaca pesan client
* Mengirim respons otomatis

Logika respons:

* Jika client mengirim `"Halo Server"` → server membalas `"halo Client"`
* Selain itu → `SERVER ACCEPTED!!`

Metode penting:

* `startServer(int port)`
* `stopConnection()`

Catatan:

* Menggunakan `accept()` → **blocking call**
* Loop `while(readLine() != null)` akan berhenti saat client disconnect

---

### 3. `Server2`

Server **interaktif manual** (server membalas lewat input terminal).

Perbedaan dengan `Server`:

* Tidak ada logika otomatis
* Admin/server mengetik balasan secara manual

Cocok untuk:

* Simulasi chat dua arah
* Memahami alur request–response secara eksplisit

---

### 4. `ManualMain`

Menjalankan **server dan client dalam satu proses JVM**.

Alur:

1. Server dijalankan dalam thread daemon
2. Client dibuat setelah server siap
3. User mengetik pesan dari sisi client
4. Server membalas otomatis

Catatan penting:

* Menggunakan `Thread.sleep()` untuk simulasi booting
* Tidak cocok untuk produksi, hanya **eksperimen lokal**

---

### 5. `ManualMain2`

Entry point untuk **menjalankan Server2 saja**.

Digunakan ketika:

* Client dijalankan dari proses/JVM terpisah
* Ingin simulasi server real-world

---

### 6. `ManualMain3`

Entry point untuk **client standalone**.

Digunakan bersama `ManualMain2`:

* Server dijalankan lebih dulu
* Client menyambung dan berkomunikasi

---

## Cara Menjalankan

### Opsi 1: Server & Client Satu Program

Jalankan:

```
ManualMain
```

---

### Opsi 2: Server & Client Terpisah (Direkomendasikan)

**Terminal 1 – Server:**

```
ManualMain2
```

**Terminal 2 – Client:**

```
ManualMain3
```

---

## Konsep Penting yang Dipelajari

* TCP connection lifecycle
* Blocking I/O (`readLine()`)
* Perbedaan:

    * Server otomatis vs server interaktif
    * Single-thread vs multi-thread sederhana
* Resource management (`close()`)

---
