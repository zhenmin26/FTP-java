package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int controlPort = 5678;
    private ServerSocket server;

    public Server(){
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
                int dataPort = controlPort + numberOfThreads + 1; //should assign a random number

                WorkingThread newWorkingThread = new WorkingThread(client, dataPort);

                System.out.println("New connection! New Thread created!");
                numberOfThreads++;
                newWorkingThread.start();
            }
            catch(IOException e){
                System.out.println("error from accept");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

}
