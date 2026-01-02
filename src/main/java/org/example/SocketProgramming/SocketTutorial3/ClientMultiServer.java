package org.example.SocketProgramming.SocketTutorial3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ClientMultiServer {
    private Selector selector;
    private SocketChannel channel;
    private String host;
    private int port;
    private volatile boolean running = true;

    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

    public ClientMultiServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startConnecttoServer() throws IOException, InterruptedException {
        selector = Selector.open();
        channel = SocketChannel.open();
        channel.configureBlocking(false);

        // Mulai koneksi non-blocking
        boolean connected = channel.connect(new InetSocketAddress(host, port));

        for (int i = 0; i < 3; i++) {
            Thread.sleep(500);
            System.out.println("Mencoba Menyambungkan ke Server host " + host + " port : " + port);
        }

        if (!connected) {
            channel.register(selector, SelectionKey.OP_CONNECT);
            System.out.println("[Connect] : Registering OP_CONNECT");
        } else {
            channel.register(selector, SelectionKey.OP_READ);
            System.out.println("[Read] : Connected directly");
        }

        new Thread(this::eventLoopClient, "client-Eventloop").start();
        inputUser();
    }

    private void eventLoopClient() {
        try {
            while (running && selector.isOpen()) {
                // TAMBAHKAN INI: Wait for events
                int readyChannels = selector.select(1000);

                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    // PERBAIKAN: ganti isAcceptable() dengan isConnectable()
                    if (key.isConnectable()) {
                        handleConnect(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable()) {
                        handleWrite(key);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[LOG ERROR] IOException: " + e.getMessage() + ", Time: " + LocalDateTime.now());
        } finally {
            try {
                stop();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private void handleConnect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        try {
            if (channel.finishConnect()) {
                System.out.println("Connected to Server!");
                key.interestOps(SelectionKey.OP_READ);
            } else {
                System.out.println("Connection Failed!");
                key.cancel();
                channel.close();
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            key.cancel();
            channel.close();
        }
    }

    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        synchronized (writeBuffer) {
            writeBuffer.flip(); // Prepare for reading

            try {
                while (writeBuffer.hasRemaining()) {
                    int written = channel.write(writeBuffer);
                    if (written == 0) {
                        // Socket buffer full, need to wait
                        break;
                    }
                }

                if (!writeBuffer.hasRemaining()) {
                    // All data written
                    writeBuffer.clear();
                    key.interestOps(SelectionKey.OP_READ);
                }
            } catch (IOException e) {
                System.err.println("Write error: " + e.getMessage());
                key.cancel();
                channel.close();
                running = false;
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        readBuffer.clear();

        try {
            int bytesRead = channel.read(readBuffer);

            if (bytesRead == -1) {
                System.out.println("Server Disconnected..");
                key.cancel();
                channel.close();
                running = false;
                return;
            }

            if (bytesRead > 0) {
                readBuffer.flip();
                byte[] data = new byte[readBuffer.remaining()];
                readBuffer.get(data);
                String message = new String(data, StandardCharsets.UTF_8);
                System.out.println("From Server: " + message);
            }
        } catch (IOException e) {
            System.err.println("Read error: " + e.getMessage());
            key.cancel();
            channel.close();
            running = false;
        }
    }

    private void sendMessage(String message) throws IOException {
        if (channel != null && channel.isOpen() && channel.isConnected()) {
            synchronized (writeBuffer) {
                // Check if buffer has enough space
                byte[] messageBytes = (message + "\n").getBytes(StandardCharsets.UTF_8);

                if (writeBuffer.position() + messageBytes.length > writeBuffer.capacity()) {
                    System.err.println("Buffer full, cannot send message");
                    return;
                }

                writeBuffer.put(messageBytes);
            }

            SelectionKey key = channel.keyFor(selector);
            if (key != null && key.isValid()) {
                // Enable writing
                key.interestOps(SelectionKey.OP_WRITE);
                selector.wakeup(); // Wake up selector
            }
        }
    }

    private void inputUser() {
        Scanner input = new Scanner(System.in);

        new Thread(() -> {
            System.out.println("Ketikan Pesan ('exit' untuk keluar):");

            while (running) {
                try {
                    System.out.print("You >> ");
                    String inputUser = input.nextLine();

                    if (inputUser.equalsIgnoreCase("exit")) {
                        sendMessage("exit");
                        Thread.sleep(1000);
                        stop();
                        break;
                    }

                    if (!inputUser.trim().isEmpty()) {
                        sendMessage(inputUser);
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    break;
                }
            }

            input.close();
        }, "User Input").start();
    }

    public void stop() throws IOException {
        running = false;

        if (selector != null && selector.isOpen()) {
            selector.close();
        }
        if (channel != null && channel.isOpen()) {
            channel.close();
        }

        System.out.println("Client Disconnected...");
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ClientMultiServer clientMultiServer = new ClientMultiServer("localhost", 8080);
        clientMultiServer.startConnecttoServer();
    }
}