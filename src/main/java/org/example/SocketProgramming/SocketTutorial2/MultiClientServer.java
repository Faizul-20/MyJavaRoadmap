package org.example.SocketProgramming.SocketTutorial2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiClientServer {

    static class EchoMultiServer{
        private ServerSocket serverSocket;
        public void startConnection(int port) throws IOException {
            serverSocket = new ServerSocket(port);
            while (true){
               EchoClientHandler echo =  new EchoClientHandler(serverSocket.accept());
                echo.start();
            }
        }

        public  void stop() throws IOException {
            serverSocket.close();
        }

        public ServerSocket getServerSocket() {
            return serverSocket;
        }

        public void setServerSocket(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
    }

    static class  EchoClientHandler extends Thread{
        private Socket clientSocket;
        private PrintWriter outMesssage;
        private BufferedReader inMessage;

        public EchoClientHandler(Socket socket){
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                outMesssage = new PrintWriter(clientSocket.getOutputStream(), true);
                inMessage = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );

                String inputLine;
                while ((inputLine = inMessage.readLine()) != null){
                    if (".".equals(inputLine)){
                        outMesssage.println("bye client");
                        break;
                    }
                    outMesssage.println(inputLine);
                }
                inMessage.close();
                outMesssage.close();
                clientSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        EchoMultiServer echoMultiServer = new EchoMultiServer();
            Thread threadServer = new Thread(() -> {
                try {
                    echoMultiServer.startConnection(8080);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            //threadServer.setDaemon(true);
            threadServer.start();

        for (int i = 0; i < 3; i++) {
            System.out.println("Server Booting...");
            Thread.sleep(1000);
        }

       if (echoMultiServer.getServerSocket() != null){
           System.out.println("Server Berhasil Booting...");
       }

        Client client = new Client();
        Client client1 = new Client();
        Client client2 = new Client();
        Thread threadClient1 = new Thread(() -> {
            try {
                client1.startConnection("localhost",8080);
                List<String> words = Arrays.asList(
                        "Hai","Hallo","kamu Ganteng"
                );
                int i = 0;
                while (i < words.size()){
                    Thread.sleep(1000);
                    String respon = client1.sendMessage(words.get(i));
                    System.out.println("Respon : " + respon);
                    i++;
                }
                client1.stopConnection();
                Thread.interrupted();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread threadClient2 = new Thread(() -> {
            try {
                client.startConnection("localhost",8080);
                List<String> words = Arrays.asList(
                        "Hai","Hallo","kamu Ganteng"
                );
                int i = 0;
                while (i < words.size()){
                    Thread.sleep(1000);
                    String respon = client.sendMessage(words.get(i));
                    System.out.println("Respon : " + respon);
                    i++;
                }
                client.stopConnection();
                Thread.interrupted();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread threadClient3 = new Thread(() -> {
            try {
                client2.startConnection("localhost",8080);
                List<String> words = Arrays.asList(
                        "Hai","Hallo","kamu Ganteng"
                );
                int i = 0;
                while (i < words.size()){
                    Thread.sleep(1000);
                    String respon = client2.sendMessage(words.get(i));
                    System.out.println("Respon : " + respon);
                    i++;
                }
                client2.stopConnection();
                Thread.interrupted();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threadClient1.start();
        threadClient2.start();
        threadClient3.start();

        System.out.println("Program selesai...");
        threadServer.interrupt();
        //echoMultiServer.stop();
        Thread.interrupted();
    }
}
