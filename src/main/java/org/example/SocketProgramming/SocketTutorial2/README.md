# Socket Programming Tutorial 2 (Java)

Package ini berisi implementasi **Socket Programming menggunakan Java** dengan beberapa pendekatan:
- Client–Server dasar
- Client interaktif (manual chat)
- Multi-client server menggunakan **Thread**
- Multi-client server menggunakan **Thread Pool (ExecutorService)**

Tujuan utama project ini adalah **memahami konsep fundamental socket, blocking I/O, concurrency, dan lifecycle koneksi client–server**, bukan sekadar membuat chat aplikasi.

---

## 📦 Package Structure

```
org.example.SocketProgramming.SocketTutorial2
│
├── Client.java
├── ClientWithChat.java
├── ManualChatClient.java
├── ManualServerMain.java
├── MultiClientServer.java
└── MultiServer.java

```


---

## 🧠 Konsep yang Dipelajari

- `Socket` dan `ServerSocket`
- Blocking I/O (`BufferedReader`, `PrintWriter`)
- Thread per client
- Thread Pool (`ExecutorService`)
- Multi-client handling
- Graceful shutdown server
- Sinkronisasi input manual (server ↔ client)

---

## 📄 Penjelasan Kelas

### 1️⃣ Client.java
Client sederhana untuk:
- Membuka koneksi ke server
- Mengirim satu pesan
- Menerima balasan server

**Cocok untuk testing echo server atau basic request–response.**

Fitur utama:
- `startConnection(ip, port)`
- `sendMessage(message)`
- `stopConnection()`

---

### 2️⃣ ClientWithChat.java
Client interaktif yang memungkinkan **chat dua arah secara manual**.

Karakteristik:
- Client menunggu pesan dari server
- Client membalas pesan via `Scanner`
- Komunikasi berjalan **blocking**

Metode penting:
- `makeChatRoom()`

Catatan penting:
- Client akan berhenti jika menerima pesan `"exit"` dari server.

---

### 3️⃣ ManualChatClient.java
Entry point untuk menjalankan **ClientWithChat**.

Alur:
1. Client mencoba koneksi ke `localhost:8080`
2. Simulasi delay koneksi
3. Masuk ke mode chat interaktif

Digunakan untuk:
- Testing manual komunikasi client ↔ server
- Observasi blocking behavior

---

### 4️⃣ ManualServerMain.java
Entry point server berbasis **MultiServer**.

- Server dijalankan dalam thread terpisah
- Menampilkan status booting
- Digunakan untuk server **berbasis Thread Pool**

---

### 5️⃣ MultiClientServer.java
Contoh **multi-client echo server** menggunakan:
- 1 thread per client (`extends Thread`)

Komponen utama:
- `EchoMultiServer` → menerima koneksi
- `EchoClientHandler` → menangani satu client

Flow:
- Client kirim pesan
- Server echo kembali pesan
- Client kirim `"."` → koneksi ditutup

Catatan:
- Pendekatan ini **tidak scalable** untuk banyak client karena setiap client = 1 thread.

---

### 6️⃣ MultiServer.java
Implementasi server **production-style** menggunakan **Thread Pool**.

Fitur utama:
- `ExecutorService (FixedThreadPool)`
- Monitoring thread pool (active, queue, max)
- Graceful shutdown

Flow server:
1. Server accept client
2. Client ditangani oleh thread pool
3. Server & client saling kirim pesan
4. Client kirim `"exit"` → koneksi ditutup

⚠️ Catatan penting:
- Server menggunakan `Scanner(System.in)` → **input server bersifat global**
- Ini sengaja untuk menunjukkan **masalah desain blocking input pada multi-client**

---

## ▶️ Cara Menjalankan

### 1. Jalankan Server
Pilih salah satu:
- `ManualServerMain`
- `MultiClientServer` (punya main method)
- `MultiServer` (via `ManualServerMain`)

### 2. Jalankan Client
- `ManualChatClient` → untuk chat manual
- Atau gunakan `Client.java` dalam multi-thread scenario

---

## ⚠️ Catatan Teknis & Kritik Desain

- `Scanner(System.in)` di server **bukan praktik produksi**
- Input server akan mem-block semua client
- Cocok hanya untuk **pembelajaran thread & blocking I/O**
- Untuk produksi:
    - Gunakan non-blocking I/O (NIO)
    - Pisahkan logic input server
    - Gunakan message queue atau event-driven model

---

## 🎯 Tujuan Pembelajaran (Learning Outcome)

Setelah memahami kode ini, kamu seharusnya:
- Paham **apa itu blocking**
- Mengerti **kenapa thread pool lebih baik dari thread per client**
- Bisa menjelaskan **perbedaan socket vs framework (Servlet / Spring)**

