package org.example.SocketProgramming.SocketTutorial3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultiServer {
    // selector di gunakan untuk Memantau banyak Channel sekaligus dan memberi tahu
    // channel mana yang sudah siap di I/O
    Selector selector;

    // Penggunaan Channel menyebabkan tidak adanya Blocking I/O
    ServerSocketChannel serversocketChannel;


    // User Tracking untuk connected Client
    private Map<SocketChannel, String> clientUsernames = new ConcurrentHashMap<>();
    private Map<String, SocketChannel> usernameChannels = new ConcurrentHashMap<>();
    private int clientActives = 0;

    // Track Server Is Running
    private boolean isRunning = false;


    // Buffer Psan Untuk ke semua Client entah BroadCast atau private
    // Model ini Menggunakan 1 User bisa banyak pesan dalam satu kirim
    private Map<SocketChannel, Queue<ByteBuffer>> pendingMessage = new ConcurrentHashMap<>();

    // Buffer pesan untuk client buat 1 user sekali masuk 1 pesan
    //private Map<SocketChannel,ByteBuffer> pendingMessage = new ConcurrentHashMap<>();


    /**
     * ALUR SETUP SERVER CHANNEL:
     * 1. Open ServerSocketChannel
     * 2. Set non-blocking (wajib untuk digunakan dengan Selector)
     * 3. Bind ke port
     * 4. Register ke Selector dengan OP_ACCEPT
     * <p>
     * URUTAN UNTUK MENDAPAT ROUTING KEY KE FUNGSI:
     * <p>
     * 1. selector.select()
     * -> Thread ter-block sampai OS mengirim event I/O
     * <p>
     * 2. selector.selectedKeys()
     * -> Mengambil SelectionKey yang READY
     * <p>
     * 3. iterator + remove()
     * -> Consume event sekali dan membersihkan selected keys
     * <p>
     * 4. key.isReadable() / isAcceptable() / isWritable()
     * -> Dispatch ke handler yang sesuai
     */

    public void startConnectioon(int port) throws IOException, InterruptedException {
        // Buka Selector
        selector = Selector.open();
        isRunning = true;


        serversocketChannel = ServerSocketChannel.open();
        serversocketChannel.configureBlocking(false);
        serversocketChannel.bind(new InetSocketAddress(port));
        serversocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //Tampilan Konsol konfigurasi Booting..
        for (int i = 1; i <= 3; i++) {
            Thread.sleep(500);
            System.out.println("Server is Booting...");
            if (i == 3 && (serversocketChannel.isRegistered() && serversocketChannel.isOpen())) {
                System.out.println("Server Successfuly Booting..");
                System.out.println("Server Started in port : " + port);
                break;
            }
        }


        while (isRunning) {

            selector.select();

            //Ini untuk pengecekan
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            //Ini akan Terus Cek ada key baru yang terus berubah2 gak
            while (keyIterator.hasNext()) {
                SelectionKey keySelected = keyIterator.next();

                keyIterator.remove();

                if (keySelected.isAcceptable()) {
                    handleAccept(keySelected);
                }
                if (keySelected.isReadable()) {
                    handleRead(keySelected);
                }
                if (keySelected.isWritable()) {
                    handleWriteV2(keySelected);

                }

            }


        }


    }

    /**
     * Menerima User Baru
     * <p>
     * Flow:
     * 1. Ambil SelectionKey lalu cast channel ke ServerSocketChannel
     * 2. Accept koneksi dan dapatkan SocketChannel client lalu configure ke non-Blocking IO
     * 3. Register SocketChannel dengan OP_READ (menerima data dari client)
     * 4. Inisialisasi konteks user (username, mapping channel)
     * 5. Masukkan welcome message ke outbound queue
     * 6. Enable OP_WRITE pada SelectionKey untuk pengiriman async
     */

    private void handleAccept(SelectionKey selectedKey) throws IOException {


        // 1.
        ServerSocketChannel serverChannel = (ServerSocketChannel) selectedKey.channel();

        //2.
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);

        //3.
        clientChannel.register(selector, SelectionKey.OP_READ);

        //4.
        String username = "user" + (clientActives + 1);
        clientUsernames.put(clientChannel, username);
        usernameChannels.put(username, clientChannel);

        //5.
        String welcomeMsg = "Welcome to the Server, " + username + "!";
        quequeMessage(clientChannel, welcomeMsg);

        // Log untuk server
        System.out.println("New Client : " + clientChannel.getRemoteAddress() + " as " + username);

    }


    /**
     * Antrian pesan per user
     *
     * Flow:
     * 1. Konversi pesan String ke ByteBuffer
     * 2. Ambil antrian dari pendingMessage, buat baru jika belum ada
     * 3. Tambahkan ByteBuffer ke antrian
     * 4. Ambil SelectionKey dari selector(global) untuk clientChannel
     * 5. Tambahkan OP_WRITE pada SelectionKey
     * 6. Bangunkan selector
     */
    private void quequeMessage (SocketChannel clientChannel,String message) {
        //1.
        ByteBuffer bufferMsg = ByteBuffer.wrap(
                message.getBytes(StandardCharsets.UTF_8)
        );

        //2.
        Queue<ByteBuffer> queue = pendingMessage.computeIfAbsent(
                clientChannel,
                k -> new ArrayDeque<>());

        //3.
        queue.add(bufferMsg);

        //4.
        SelectionKey key = clientChannel.keyFor(selector);

        // 5.
        if (key != null && key.isValid()){
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }

        //6.
        selector.wakeup();
    }

    /**
     * ALUR HANDLEWRITE:
     *
     * Flow:
     * 1. Dapatkan channel dari SelectionKey
     * 2. Ambil antrian pesan dari pendingMessage
     * 3. Kirim pesan ByteBuffer sampai habis
     * 4. Jika antrian kosong, hapus OP_WRITE dari key
     */
    @Deprecated(forRemoval = true)
    private void handleWrite(SelectionKey key) throws IOException{

        SocketChannel client = (SocketChannel) key.channel();

        Queue<ByteBuffer> quequeMessage = pendingMessage.get(client);

        while (!quequeMessage.isEmpty()){
            ByteBuffer bufferMessage = quequeMessage.peek();
            client.write(bufferMessage);
            if (bufferMessage.hasRemaining()) break;
            quequeMessage.poll();

        }

        if (quequeMessage.isEmpty()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        }

    }

    private void handleWriteV2(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        Queue<ByteBuffer> queue = pendingMessage.get(client);

        if (queue == null || queue.isEmpty()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            return;
        }

        ByteBuffer buffer = queue.peek();

        try {
            int bytesWritten = client.write(buffer);

            if (bytesWritten == -1) {
                // Client disconnected
                client.close();
                key.cancel();

                // Cleanup
                String username = clientUsernames.remove(client);
                if (username != null) {
                    usernameChannels.remove(username);
                }
                pendingMessage.remove(client);
                clientActives--;
                System.out.println("Client disconnected: " + username);
                return;
            }

            // PERBAIKAN: Cek jika buffer sudah habis TERKIRIM (hasRemaining() == false)
            if (!buffer.hasRemaining()) {
                queue.poll(); // Hapus buffer yang sudah terkirim
            }

            if (queue.isEmpty()) {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            System.err.println("Write error: " + e.getMessage());
            client.close();
            key.cancel();
        }
    }
    /**
     * ALUR HANDLEREAD :
     *
     * Flow :
     * 1. ambil channel dari SelectionKey
     * 2. Alokasikan ByteBuffer sementara
     * 3. bara dari client.read(buffer)
     *     -> bytesread = -1 : client disconnected
     * 4. flip buffer untuk siap baca
     * 5. ambil bytesArray dari buffer -> konversi String
     * 6. Proses pesan sesuai logika server
     * 7. balas pesan
     *
     *
     * */
    private void handleRead(SelectionKey key)throws IOException{
        SocketChannel client = (SocketChannel) key.channel();

        ByteBuffer bufferMessage = ByteBuffer.allocate(1024);
        int bytesRead = client.read(bufferMessage);

        if (bytesRead == -1){

            key.cancel();
            client.close();
            pendingMessage.remove(client);

            String userame = clientUsernames.remove(client);
            if (userame != null){
                usernameChannels.remove(userame);
            }
            pendingMessage.remove(client);
            clientActives--;
            System.out.println("Client Disconected " + userame);
            return;
        }

        if (bytesRead > 0){
            bufferMessage.flip();

            byte[] data = new byte[bufferMessage.remaining()];
            bufferMessage.get(data);

            String message = new String(data,StandardCharsets.UTF_8);
            String username = clientUsernames.get(client);




            System.out.println("Received From : " + username + message);

            quequeMessage(client,"Server Received : " + message);


        }

    }



    public static void main(String[] args) throws IOException,InterruptedException{
        MultiServer multiServer = new MultiServer();
        multiServer.startConnectioon(8080);
    }

}


