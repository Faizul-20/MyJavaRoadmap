package org.example.SocketProgramming.SocketTutorial1;

import java.io.IOException;
import java.util.Scanner;

public class ManualMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try{
        Server server = new Server();
        Thread Threadserver = new Thread(() -> {
            try {
                server.startServer(8080);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Threadserver.setDaemon(true);
        Threadserver.setName("Server");
        Threadserver.start();

        for (int i = 1; i <= 3; i++) {
            System.out.println(Threadserver.getName() + " Is Booting....");
            Threadserver.sleep(1000);
        }
        if (server.getServerSocket() != null){
            System.out.println("Server Berhasil Booting");
        }else {
            System.out.println("Server gagal booting...");
            throw new InterruptedException("Server gagal");
        }

        Client client = new Client();

        client.startConnection("localhost",8080);
        while (true){
            System.out.print("Masukan Pesan Server : ");
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")){
                break;
            }
            client.sendMessage(message);
        }

        client.stopConnection();
        server.stopConnection();
        Threadserver.interrupt();


        }catch (InterruptedException | IOException e){
            System.out.println("Masalah Terjadi " + e);
        }

    }
}
