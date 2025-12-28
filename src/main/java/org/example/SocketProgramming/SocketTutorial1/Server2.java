package org.example.SocketProgramming.SocketTutorial1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server2 {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader inMessage;
    private PrintWriter outMessage;

    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        outMessage = new PrintWriter(clientSocket.getOutputStream(),true);
        inMessage = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
        );

        Scanner scanner = new Scanner(System.in);
        String clientmessage;

        while ((clientmessage = inMessage.readLine()) != null){
            System.out.println("[CLIENT] : " + clientmessage);
            System.out.print("Balas >> : ");
            String message = scanner.nextLine();
            outMessage.println(message);
        }




    }

    public void stopConnection() throws IOException{
        serverSocket.close();
        clientSocket.close();
        inMessage.close();
        outMessage.close();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
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
