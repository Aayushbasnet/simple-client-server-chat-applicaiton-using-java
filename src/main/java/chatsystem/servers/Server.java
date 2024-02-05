package chatsystem.servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        final ServerSocket serverSocket;
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final int allocatedServerPort;
        final Scanner userMessage = new Scanner(System.in);

        try{
            //ServerSocket() creates server socket then binds the port and listens for the incoming connections requests from the clients.
            serverSocket = new ServerSocket(0); // port 0 :- kernel itself chooses ephemeral port
            allocatedServerPort = serverSocket.getLocalPort(); // get the allocated port number of the server socket
            System.out.println("Allocated server port is: " + allocatedServerPort);

            /*
                accept() is used to wait for a request from the client, once it receives one it accepts it and create an
                instance of the Socket class which in our case will be the clientSocket object.
                Blocks until connection from client.
             */
            clientSocket = serverSocket.accept(); // returns next completed connection from the front of the completed connection queue.
            System.out.println("Client connected");

            /*
                new InputStreamReader( clientSocket.getInputStream()) : creates a stream reader for the socket.
                However, this stream reader only reads data as Bytes,
                therefore it must be passed to BufferedReader to be converted into characters.
             */
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // reading data from the client
            out = new PrintWriter(clientSocket.getOutputStream()); // sending data to the client

            Thread senderThread = new Thread(new Runnable() {
                String toSendMessage; // variable that will contain the data written by the user
                @Override
                public void run() {
                    System.out.println("Server send thread started");
                    while(true){
                        System.out.println("Write a message sender:: ");
                        toSendMessage = userMessage.nextLine();
                        out.println(toSendMessage); // write data storied in msg in the clientSocket
                        out.flush(); // flush the output stream to ensure the message is sent immediately
                    }
                }
            });

            Thread receiveThread = new Thread(new Runnable() {
                String receivedMessage;
                @Override
                public void run() {
                    System.out.println("Server receive thread started");

                    try{
                        receivedMessage = in.readLine(); // read data from the clientSocket using "in" object
                        // While the client is still connected to the server
                        while ( clientSocket.isConnected() && receivedMessage != null && !receivedMessage.equals("exit")){
                            System.out.println("Client message received by server:: " + receivedMessage);
                            System.out.println("-------------------------------------------------------------------");
                            System.out.println("Press enter to reply");
                            receivedMessage = in.readLine();
                        }
                        System.out.println( "Client disconnected");
                        // CLosing the sockets, stream and scanner
                        out.close();
                        clientSocket.close();
                        serverSocket.close();
                        userMessage.close();
                        in.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();
            senderThread.start();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
