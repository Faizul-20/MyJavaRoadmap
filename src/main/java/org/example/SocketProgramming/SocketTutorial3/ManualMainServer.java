package org.example.SocketProgramming.SocketTutorial3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ManualMainServer {
    public static void main(String[] args) throws IOException {
        String msg = "Aku Suka Kamu ";
        byte[] byteBuffer = msg.getBytes(StandardCharsets.UTF_8);

        System.out.println(Arrays.toString(byteBuffer));

    }
}
