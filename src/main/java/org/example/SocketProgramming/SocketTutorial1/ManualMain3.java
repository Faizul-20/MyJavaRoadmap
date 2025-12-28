package org.example.SocketProgramming.SocketTutorial1;

import java.io.IOException;
import java.util.Scanner;

public class ManualMain3 {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        Scanner scanner = new Scanner(System.in);

        client.startConnection("localhost",8080);
        while (true){
            System.out.print("Balas >> : ");
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")){
                break;
            }
            String respon = client.sendMessage(message);
            System.out.println(respon);

        }
    }
}
