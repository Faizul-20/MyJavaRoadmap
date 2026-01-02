package org.example.SocketProgramming.SocketTutorial2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiServer {
    private ServerSocket serverSocket;
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void  startConnection(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server Connected to port : " + port);
        try {


            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client connected...");
                printPoolStatus();
                executorService.execute(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        }catch (IOException e){
            System.out.println("Server Error " + e.getMessage());
        }finally {
            stop();
        }
    }

    private void handleClient(Socket clientSocket) throws IOException{
        Scanner scanner = new Scanner(System.in);
        String clientId = Thread.currentThread().getName() + "-" + clientSocket.getPort();
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            ); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                out.println("Hello !! You are Handled By : " + clientId);

                String message;
                while ((message = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(message)) {
                        out.println("Good Bye!! " + clientId);
                        break;
                    }
                    String response = "[" + clientId + "] : " + message;

                    System.out.println(response);
                    System.out.print("Balas >> ");
                    String callBack = scanner.nextLine();
                    out.println("[SERVER] : " + callBack);
                }


            } catch (IOException e) {
                System.out.println("Eror with client : " + clientId + " " + e.getMessage());

            } finally {
                try {
                    clientSocket.close();
                    System.out.println(clientId + " disconected");
                } catch (IOException e) {
                    // Lupakan saja
                }
            }

    }

    private void printPoolStatus(){
        ThreadPoolExecutor executor = (ThreadPoolExecutor)  executorService;
        System.out.println("Keadaan Server :  ");
        System.out.println("Jumlah Thread Aktif :" + executor.getActiveCount());
        System.out.println("Jumlah Antrian : " + executor.getQueue().size());
        System.out.println("Jumlah Thread Tresedia : " + executor.getPoolSize());
        System.out.println("Jumlah Maximum Thread:" + executor.getMaximumPoolSize());

    }
    public void stop(){
        try{
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {

        }

        executorService.shutdown();
        try{
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)){
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("Server dan ThreadPool telah di shutdoen");
    }

    public static void main(String[] args) throws IOException{
        MultiServer multiServer = new MultiServer();
        multiServer.startConnection(8080);
    }
}
