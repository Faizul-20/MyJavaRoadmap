package org.example.SocketProgramming.SocketTutorial1;

import java.io.IOException;

public class ManualMain2 {
    public static void main(String[] args) {
        Server2 server2 = new Server2();

        Thread thread = new Thread(()->{
            try {
                server2.startServer(8080);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.setName("Server");
        thread.start();
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
                System.out.println(thread.getName() + " is booting....");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        if (server2.getServerSocket() != null){
            System.out.println("Srver Berhasil Booting...");
        }else {
            System.out.println("Server Gagal...");
            throw new RuntimeException();
        }

    }
}
