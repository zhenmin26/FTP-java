package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

public class DataConnection {
    // socket
    public static Socket dataConnection;
    private ServerSocket passiveDataSocket;

    // data flow
    private PrintWriter dataIn;
    private PrintWriter dataOutActive;
    private PrintWriter dataOutPassive;

    // active mode
    public DataConnection(String host, int port, PrintWriter out) {
        try{
            System.out.println("Active mode - host: " + host + "port: " + port);
            this.dataConnection = new Socket(host, port);
            dataOutActive = new PrintWriter(dataConnection.getOutputStream(), true);
            out.println("230 Data connection established - active mode");
//            dataOutActive.println("230 Data connection established - active mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // passive mode
    public DataConnection(int port, PrintWriter out, WorkingThread thread) {
        try{
            System.out.println("Passive mode - dataport: " + port);
            passiveDataSocket = new ServerSocket(port);
            this.dataConnection = passiveDataSocket.accept();

            // set data connection for thread
            thread.setPassiveDataConnection(dataConnection);

            dataOutPassive = new PrintWriter(dataConnection.getOutputStream(), true);
            out.println("230 Data connection established - passive mode");
//            dataOutPassive.println("230 Data connection established - passive mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
