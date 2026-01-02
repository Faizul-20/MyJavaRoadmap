package org.example.SocketProgramming.SocketTutorial3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class ServerChannel {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

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
        System.out.println("Sucsessfully Booting...");
        System.out.println("Nio server is started in port : " + port);

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
        }

    }

    private void handleAccept(SelectionKey selectionKey) throws IOException{
        ServerSocketChannel serverChannel1 = (ServerSocketChannel) selectionKey.channel();

        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);

        //Daftarkan channel ke selector
        clientChannel.register(selector,SelectionKey.OP_READ);

        System.out.println("Client Connected" + clientChannel.getRemoteAddress());

    }

    private void handleRead(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = channel.read(buffer);

        if (bytesRead == -1){
            channel.close();
            return;
        }

        if (bytesRead > 0){
            buffer.flip(); //ganti dari Write ke Read Mode

            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            String message = new String(data, StandardCharsets.UTF_8);

            System.out.println("Received : " + message);

            //Jadwal Untuk write Response
            key.interestOps(SelectionKey.OP_WRITE);
            key.attach("Echo: " + message);
        }
    }

    private void handleWrite(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();

        String response = (String) key.attachment();

        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));

        //Kembali ke read Mode
        key.interestOps(SelectionKey.OP_READ);
        key.attach(null);
    }
}
