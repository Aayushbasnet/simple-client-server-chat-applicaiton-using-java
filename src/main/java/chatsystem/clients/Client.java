package chatsystem.clients;

import java.io.*;
import java.net.*;
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
            /*
                No need to use clientSocket.connect() as we can directly specify ip and port in new Socket(host, port).
                Flow:
                socket() -> connect(serverAddress)
                where serverAddress is, InetSocketAddress serverAddress = new InetSocketAddress(String host(eg: localhost), int portNumber);
             */
            clientSocket = new Socket(host, socketPort);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());

            Thread senderThread = new Thread(new Runnable() {
                String clientMessageToBeSent;
                @Override
                public void run() {
                    System.out.println("Client send thread started");
                    while(true){
                        System.out.println("Write a message client::");
                        clientMessageToBeSent = clientMessageInput.nextLine();
                        out.println(clientMessageToBeSent);
                        out.flush();
                    }
                }
            });

            Thread receiveThread = new Thread(new Runnable(){
                String receivedMessage;
                @Override
                public void run(){
                    System.out.println("Client receive thread started");

                    try {
                        receivedMessage = in.readLine();
                        while(clientSocket.isConnected() && receivedMessage != null && !receivedMessage.equals("exit")){
                            System.out.println("Server message received by client::" + receivedMessage);
                            System.out.println("-------------------------------------------------------------------");
                            System.out.println("Press enter to reply");
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
            senderThread.start();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
