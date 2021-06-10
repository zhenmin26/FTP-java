package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int controlPort = 5678;
    private ServerSocket server;

    public Server() throws IOException {
        try{
            server = new ServerSocket(controlPort); // socket listening on controlPort 5678
        }
        catch(IOException e){
            System.out.println("cannot create server...");
            System.exit(-1);
        }

        System.out.println("FTP Server listening on control port " + controlPort);

        int numberOfThreads = 0;

        while(true){
            try{
                Socket client = server.accept();
                int dataPort = 6789 + numberOfThreads; //should assign a random number

                WorkingThread newWorkingThread = new WorkingThread(client, dataPort);

                System.out.println("New connection! New Thread created!");
                numberOfThreads++;
//                System.out.println("Current number of clients: " + numberOfThreads);
                newWorkingThread.start();
            }
            catch(IOException e){
                System.out.println("error from accept");
            }
        }
//        try {
//            server.close();
//        }
//        catch(IOException e){
//            System.out.println("error from stopping server");
//        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

}