package org.example.SocketProgramming.SocketTutorial1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader inMessage;
    private PrintWriter outMessage;

    public void startConnection(String ip,int port) throws IOException {
        socket = new Socket(ip,port);
        inMessage = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        outMessage = new PrintWriter(socket.getOutputStream(),true);

    }

    public String sendMessage (String message) throws  IOException{
        outMessage.println(message);
        String result ="[SERVER] : " + inMessage.readLine();
        return result;
    }

    public void stopConnection() throws IOException{
        socket.close();
        inMessage.close();
        outMessage.close();
    }

}
