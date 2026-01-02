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

public class ServerChannelWithInput {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    // ========== TAMBAHAN BARU ==========
    // 1. Track semua client yang connected
    private Map<SocketChannel, String> clientUsernames = new ConcurrentHashMap<>();
    private Map<String, SocketChannel> usernameToChannel = new ConcurrentHashMap<>();
    private int clientCounter = 0;

    // 2. Buffer untuk broadcast ke semua client
    private Map<SocketChannel, ByteBuffer> pendingWrites = new ConcurrentHashMap<>();
    // ====================================

    public void start(int port) throws IOException, InterruptedException {
        // 1. Buat Selector
        selector = Selector.open();

        // 2. Buat Server Channel
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(port));

        // 3. Daftarkan channel ke selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        for (int i = 0; i < 3; i++) {
            Thread.sleep(1000);
            System.out.println("Server is Booting...");
        }
        System.out.println("Successfully Booting...");
        System.out.println("NIO server is started in port : " + port);
        System.out.println("Type 'help' for available commands\n");

        // ========== TAMBAHAN BARU ==========
        // 4. Start console command listener (non-blocking)
        startConsoleListener();
        // ====================================

        while (true){
            // Tunggu yang tidak Blocking
            selector.select();

            //Dapatkan semua event yang ready
            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()){
                    handleAccept(key);
                }

                if (key.isReadable()){
                    handleRead(key);
                }
                if (key.isWritable()){
                    handleWrite(key);
                }
            }

            // ========== TAMBAHAN BARU ==========
            // Cek jika ada pending broadcast
            handlePendingBroadcasts();
            // ====================================
        }
    }

    private void handleAccept(SelectionKey selectionKey) throws IOException{
        ServerSocketChannel serverChannel1 = (ServerSocketChannel) selectionKey.channel();

        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);

        //Daftarkan channel ke selector
        clientChannel.register(selector,SelectionKey.OP_READ);

        // ========== TAMBAHAN BARU ==========
        // Assign username untuk client baru
        String username = "User" + (++clientCounter);
        clientUsernames.put(clientChannel, username);
        usernameToChannel.put(username, clientChannel);

        // Kirim welcome message
        String welcomeMsg = "Welcome " + username + "! Type 'help' for commands.\n";
        queueMessageToClient(clientChannel, welcomeMsg);

        // Broadcast ke client lain
        broadcastMessage(username + " has joined the chat!", clientChannel);
        // ====================================

        System.out.println("Client Connected: " + clientChannel.getRemoteAddress() + " as " + username);

        // ========== TAMBAHAN BARU ==========
        // Tampilkan jumlah client aktif
        printActiveClients();
        // ====================================
    }

    private void handleRead(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = channel.read(buffer);

        if (bytesRead == -1){
            // ========== TAMBAHAN BARU ==========
            handleClientDisconnect(channel);
            // ====================================
            return;
        }

        if (bytesRead > 0){
            buffer.flip(); //ganti dari Write ke Read Mode

            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            String message = new String(data, StandardCharsets.UTF_8).trim();

            System.out.println("Received from " + getClientUsername(channel) + ": " + message);

            // ========== TAMBAHAN BARU ==========
            // Handle special commands
            if (handleClientCommand(channel, message)) {
                return; // Jika itu command, tidak perlu echo biasa
            }

            // Broadcast message ke semua client
            String formattedMsg = "[" + getClientUsername(channel) + "]: " + message;
            broadcastMessage(formattedMsg, channel);
            // ====================================

            // Opsional: tetap echo ke sender juga
            // Jadwal Untuk write Response
            key.interestOps(SelectionKey.OP_WRITE);
            key.attach("Echo: " + message);
        }
    }

    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        String response = (String) key.attachment();

        if (response != null) {
            ByteBuffer buffer = ByteBuffer.wrap((response + "\n").getBytes(StandardCharsets.UTF_8));

            try {
                channel.write(buffer);
            } catch (IOException e) {
                handleClientDisconnect(channel);
            }
        }

        //Kembali ke read Mode
        key.interestOps(SelectionKey.OP_READ);
        key.attach(null);
    }

    // ========== METHOD BARU ==========

    /**
     * Dapatkan username dari channel
     */
    private String getClientUsername(SocketChannel channel) {
        return clientUsernames.getOrDefault(channel, "Unknown");
    }

    /**
     * Handle client disconnect
     */
    private void handleClientDisconnect(SocketChannel channel) {
        String username = clientUsernames.get(channel);
        if (username != null) {
            System.out.println("Client disconnected: " + username);

            // Broadcast ke client lain
            broadcastMessage(username + " has left the chat!", channel);

            // Hapus dari tracking
            clientUsernames.remove(channel);
            usernameToChannel.remove(username);
            pendingWrites.remove(channel);

            // Tampilkan jumlah client aktif
            printActiveClients();
        }

        try {
            if (channel.isOpen()) {
                channel.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    /**
     * Handle special commands dari client
     */
    private boolean handleClientCommand(SocketChannel channel, String message) {
        if (message.startsWith("/")) {
            String[] parts = message.split(" ", 2);
            String command = parts[0].toLowerCase();
            String argument = parts.length > 1 ? parts[1] : "";

            switch (command) {
                case "/name":
                    if (!argument.isEmpty()) {
                        String oldName = clientUsernames.get(channel);
                        String newName = argument.trim();

                        clientUsernames.put(channel, newName);
                        usernameToChannel.remove(oldName);
                        usernameToChannel.put(newName, channel);

                        queueMessageToClient(channel, "Username changed to: " + newName + "\n");
                        broadcastMessage(oldName + " is now known as " + newName, channel);
                    }
                    break;

                case "/list":
                    StringBuilder list = new StringBuilder("Connected users: ");
                    for (String user : clientUsernames.values()) {
                        list.append(user).append(", ");
                    }
                    if (!clientUsernames.isEmpty()) {
                        list.setLength(list.length() - 2); // Remove last ", "
                    }
                    queueMessageToClient(channel, list.toString() + "\n");
                    break;

                case "/pm":
                    if (parts.length == 2) {
                        String[] pmParts = argument.split(" ", 2);
                        if (pmParts.length == 2) {
                            String targetUser = pmParts[0];
                            String pmMessage = pmParts[1];
                            sendPrivateMessage(channel, targetUser, pmMessage);
                        }
                    }
                    break;

                case "/help":
                    String help = "Available commands:\n" +
                            "/name <newname> - Change username\n" +
                            "/list - List online users\n" +
                            "/pm <user> <message> - Private message\n" +
                            "/help - Show this help\n";
                    queueMessageToClient(channel, help);
                    break;

                default:
                    queueMessageToClient(channel, "Unknown command. Type /help for list.\n");
            }
            return true;
        }
        return false;
    }

    /**
     * Kirim private message ke user tertentu
     */
    private void sendPrivateMessage(SocketChannel sender, String targetUser, String message) {
        SocketChannel targetChannel = usernameToChannel.get(targetUser);
        if (targetChannel != null && targetChannel.isOpen()) {
            String senderName = getClientUsername(sender);
            String pm = "[PM from " + senderName + "]: " + message;
            queueMessageToClient(targetChannel, pm + "\n");
            queueMessageToClient(sender, "PM sent to " + targetUser + "\n");
        } else {
            queueMessageToClient(sender, "User " + targetUser + " not found or offline\n");
        }
    }

    /**
     * Broadcast message ke semua client kecuali sender
     */
    private void broadcastMessage(String message, SocketChannel exclude) {
        for (SocketChannel client : clientUsernames.keySet()) {
            if (client != exclude && client.isOpen()) {
                queueMessageToClient(client, message + "\n");
            }
        }
    }

    /**
     * Queue message untuk dikirim ke client
     */
    private void queueMessageToClient(SocketChannel client, String message) {
        ByteBuffer buffer = pendingWrites.get(client);
        if (buffer == null) {
            buffer = ByteBuffer.allocate(1024);
            pendingWrites.put(client, buffer);
        }

        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        if (buffer.remaining() >= data.length) {
            buffer.put(data);

            // Schedule write
            SelectionKey key = client.keyFor(selector);
            if (key != null && key.isValid()) {
                key.interestOps(SelectionKey.OP_WRITE);
            }
        }
    }

    /**
     * Handle pending broadcasts
     */
    private void handlePendingBroadcasts() {
        Iterator<Map.Entry<SocketChannel, ByteBuffer>> iter = pendingWrites.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<SocketChannel, ByteBuffer> entry = iter.next();
            SocketChannel client = entry.getKey();
            ByteBuffer buffer = entry.getValue();

            if (!client.isOpen()) {
                iter.remove();
                continue;
            }

            buffer.flip();
            try {
                client.write(buffer);

                if (buffer.hasRemaining()) {
                    buffer.compact();
                } else {
                    buffer.clear();
                    iter.remove();
                }
            } catch (IOException e) {
                iter.remove();
                handleClientDisconnect(client);
            }
        }
    }

    /**
     * Tampilkan jumlah client aktif
     */
    private void printActiveClients() {
        System.out.println("Active clients: " + clientUsernames.size());
        if (!clientUsernames.isEmpty()) {
            System.out.print("Online: ");
            for (String user : clientUsernames.values()) {
                System.out.print(user + " ");
            }
            System.out.println();
        }
    }

    /**
     * Start console listener untuk admin commands
     */
    private void startConsoleListener() {
        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("\nServer Console> ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Shutting down server...");
                    System.exit(0);
                } else if (input.equalsIgnoreCase("list")) {
                    printActiveClients();
                } else if (input.equalsIgnoreCase("help")) {
                    System.out.println("Server Console Commands:");
                    System.out.println("list - Show connected clients");
                    System.out.println("broadcast <message> - Send message to all clients");
                    System.out.println("kick <username> - Disconnect a client");
                    System.out.println("exit - Shutdown server");
                } else if (input.startsWith("broadcast ")) {
                    String message = input.substring(10);
                    broadcastMessage("[SERVER BROADCAST]: " + message, null);
                    System.out.println("Broadcast sent to all clients");
                } else if (input.startsWith("kick ")) {
                    String username = input.substring(5);
                    SocketChannel target = usernameToChannel.get(username);
                    if (target != null) {
                        queueMessageToClient(target, "You have been kicked by admin\n");
                        handleClientDisconnect(target);
                        System.out.println("Kicked user: " + username);
                    } else {
                        System.out.println("User not found: " + username);
                    }
                } else if (!input.isEmpty()) {
                    System.out.println("Unknown command. Type 'help' for available commands.");
                }
            }
        });

        consoleThread.setDaemon(true);
        consoleThread.start();
    }
}