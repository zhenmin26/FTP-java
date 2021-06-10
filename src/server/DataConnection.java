package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

public class DataConnection {
    // socket
    private Socket activeDataConnection;
    private ServerSocket passiveDataSocket;
    private Socket passiveDataConnection;

    // data flow
    private PrintWriter dataIn;
    private PrintWriter dataOut;

    // active mode
    public DataConnection(String host, int port, PrintWriter out) {
        try{
            activeDataConnection = new Socket(host, port);
            dataOut = new PrintWriter(activeDataConnection.getOutputStream(), true);
            out.println("230 Data connection established - active mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // passive mode
    public DataConnection(int port, PrintWriter out) {
        try{
            passiveDataSocket = new ServerSocket(port);
            passiveDataConnection = passiveDataSocket.accept();
            out.println("230 Data connection established - passive mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
