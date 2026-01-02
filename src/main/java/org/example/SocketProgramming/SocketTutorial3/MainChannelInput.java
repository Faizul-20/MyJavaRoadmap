package org.example.SocketProgramming.SocketTutorial3;

import java.io.IOException;

public class MainChannelInput {
    public static void main(String[] args) throws IOException {
        try {
            ServerChannelWithInput serverChannel = new ServerChannelWithInput();
            serverChannel.start(8080);

        }catch (Exception e){
            System.err.println("Failed : " + e.getMessage());
        }

    }
}
