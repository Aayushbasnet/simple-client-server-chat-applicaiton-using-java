package chatsystem.clients;

import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner clientMessageInput = new Scanner(System.in);

        Scanner userInput = new Scanner(System.in);

        System.out.println("Enter the host address::");
        String host = userInput.nextLine();
        System.out.println("Enter the server socket port number::");
        int socketPort = userInput.nextInt();

        try{
            clientSocket = new Socket(host, socketPort);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());

            Thread senderThread = new Thread(new Runnable() {
                String clientMessageToBeSent;
                @Override
                public void run() {
                    while(true){
                        System.out.println("Write a message client::");
                        clientMessageToBeSent = clientMessageInput.nextLine();
                        out.println(clientMessageToBeSent);
                        out.flush();
                    }
                }
            });

            System.out.println("Receiver send thread started");
            senderThread.start();

            Thread receiveThread = new Thread(new Runnable(){
                String receivedMessage;
                @Override
                public void run(){
                    try {
                        receivedMessage = in.readLine();
                        if(!Objects.equals(receivedMessage, "exit")){
                            System.out.println("Server message received by client::");
                            receivedMessage = in.readLine();
                        }
                        System.out.println("Server has been disconnected");
                        out.close();
                        clientSocket.close();
                        userInput.close();
                        clientMessageInput.close();
                        in.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
