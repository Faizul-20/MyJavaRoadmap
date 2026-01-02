package org.example.SocketProgramming.SocketTutorial3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ClientChannel {

    private Selector selector;
    private SocketChannel socketChannel;
    private String host;
    private int port;

    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

    public ClientChannel(String host,int port){
        this.host = host;
        this.port = port;
    }

    public void start()throws IOException{

        //1. Buat Selector
        selector = Selector.open();

        //2. Buat socket Channel
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // 3. Connect (non-blocking connect)
        boolean connected = socketChannel.connect(new InetSocketAddress(
                host,port
        ));

        if (!connected){
            //Apabila Koneksi Belum Selesai, REgister untuk OP_COnnect
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }else {
            socketChannel.register(selector,SelectionKey.OP_READ);
            System.out.println("Connected to Server");
        }

        System.out.println("Connecting to " + host + ":" + port);

        new Thread(this::evenloop,"Client=EvenLoop").start();

        startInputUser();

    }

    private void evenloop(){
        try{
            while (selector.isOpen() && socketChannel.isOpen()){

                //Tunggu terus sampai intterupt
                if (selector.select(100) > 0){

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();

                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();

                        iterator.remove();

                        if (key.isValid()) continue;

                        if (key.isConnectable()){
                            handleConnect(key);
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
        }catch (IOException e){
            System.err.println("Client Eror Ketike Berjalan : " + e.getMessage());
        }
    }

    private void handleConnect(SelectionKey key) throws IOException{

        SocketChannel channel = (SocketChannel) key.channel();

        if (channel.finishConnect()){
            System.out.println("Connected to Server!!");
            key.interestOps(SelectionKey.OP_READ);
        }else {
            System.err.println("Connection Failed!");
            key.cancel();
            channel.close();;
        }

    }

    private void handleRead(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)  key.channel();

        readBuffer.clear();

        int bytesRead = socketChannel.read(readBuffer);

        if (bytesRead == -1){
            System.out.println("Server Disconneted");
            key.cancel();
            socketChannel.close();
        }

        if (bytesRead >0){
            readBuffer.flip();

            byte[] data = new byte[readBuffer.remaining()];

            readBuffer.get(data);
            String message = new String(data, StandardCharsets.UTF_8);

            System.out.println("\n[SERVER] : " + message);
            System.out.println("You : ");

        }

    }

    private void handleWrite(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)  key.channel();

        synchronized (writeBuffer){
            writeBuffer.flip();

            while (writeBuffer.hasRemaining()){
                socketChannel.write(writeBuffer);
            }

            writeBuffer.clear();
        }

        key.interestOps(SelectionKey.OP_READ);

    }

    private void sendMessage(String message) throws IOException{
        if (socketChannel != null && socketChannel.isOpen()){
            synchronized (writeBuffer){
                writeBuffer.put((message + "\n").getBytes(StandardCharsets.UTF_8));
            }

            //jadwalkan Penulisan
            SelectionKey key = socketChannel.keyFor(selector);

            if (key != null && key.isValid()){
                key.interestOps(SelectionKey.OP_WRITE);
                selector.wakeup();
            }
        }
    }

    private void startInputUser() throws IOException{
        Scanner scanner = new Scanner(System.in);

        new Thread(() -> {
            System.out.println("Ketikan Pesan ('exit' untuk Keluar) : ");

            while (true){
                System.out.print("You : ");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input)){
                    try {
                        sendMessage("exit");
                        Thread.sleep(100);
                        stop();
                    } catch (Exception e) {

                    }
                }
                try{
                    sendMessage(input);
                } catch (IOException e) {
                    System.err.println("Failed to Send Message : " + e.getMessage());
                    break;
                }
            }
            scanner.close();
        },"User Input").start();

    }

    public void stop() throws  IOException{
        if (selector != null && selector.isOpen()){
            selector.close();
        }
        if (socketChannel.isOpen() && socketChannel != null){
            socketChannel.close();
        }
        System.out.println("Client Disconnected..");
    }

    public static void main(String[] args) throws IOException {
        ClientChannel clientChannel = new ClientChannel("localhost",8080);
        clientChannel.start();
    }
}
