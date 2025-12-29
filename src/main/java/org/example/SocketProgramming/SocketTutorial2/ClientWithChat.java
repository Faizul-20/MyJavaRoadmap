package org.example.SocketProgramming.SocketTutorial2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ClientWithChat {
    private Socket socket;
    private BufferedReader inMessage;
    private PrintWriter outMessage;



    public void startConnection(String ip, int port) throws IOException {
        socket = new Socket(ip,port);
        inMessage = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        outMessage = new PrintWriter(socket.getOutputStream(),true);

    }

    public void makeChatRoom() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--Selamat Datang di Chat Room--");
        String serverMessage;
        try{
        while ((serverMessage = inMessage.readLine()) != null){
            System.out.println(serverMessage);
            System.out.print("Balas >> ");
            String outMessage = scanner.nextLine();
            this.outMessage.println(outMessage);
            if (serverMessage.equalsIgnoreCase("exit")) {
                break;
            }
        }
        }catch (ConnectException e){
            System.out.println("Terjadi masalah Server : " + e.getMessage());
        }
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getInMessage() {
        return inMessage;
    }

    public void setInMessage(BufferedReader inMessage) {
        this.inMessage = inMessage;
    }

    public PrintWriter getOutMessage() {
        return outMessage;
    }

    public void setOutMessage(PrintWriter outMessage) {
        this.outMessage = outMessage;
    }

}
