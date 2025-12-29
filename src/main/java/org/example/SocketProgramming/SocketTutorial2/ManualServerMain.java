package org.example.SocketProgramming.SocketTutorial2;

import java.io.IOException;

public class ManualServerMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        MultiServer multiServer = new MultiServer();
        Thread thread = new Thread(()->{
            try {
                multiServer.startConnection(8080);
            } catch (IOException e) {

            }
        });
        //thread.setDaemon(true);
        thread.start();
        for (int i = 0; i < 3; i++) {
            Thread.sleep(100);
            System.out.println("Server Booting....");
        }
        if (thread.isAlive()){
            System.out.println("Server Succesfully booting...");
        }
    }
}
