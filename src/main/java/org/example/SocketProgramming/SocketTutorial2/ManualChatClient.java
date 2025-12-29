package org.example.SocketProgramming.SocketTutorial2;

import java.io.IOException;
import java.net.ConnectException;

public class ManualChatClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        ClientWithChat client = new ClientWithChat();
        try {
            client.startConnection("localhost", 8080);
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                System.out.println("Connect to Server....");
            }
            client.makeChatRoom();
        }catch (ConnectException e){
            System.err.println("Failed To Connect Server : " + e.getMessage());
        }


    }
}
