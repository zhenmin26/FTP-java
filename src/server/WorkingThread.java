package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkingThread extends Thread {
    private boolean quitCommand = true;


    // user of thread
    private ThreadLocal<String> USER = new ThreadLocal<String>();
    private boolean userLogin = false;

    // control socket
    private Socket controlSocket;
    private ServerSocket passiveDataSocket;

    // data socket
    private ServerSocket dataSocket;
    private String dataHost;
    private int dataPort;
    private Socket dataConnection;

    // data flow
    private BufferedReader controlIn;
    private PrintWriter controlOut;

    public WorkingThread(Socket client, int dataPort) {
        this.controlSocket = client;
        this.dataPort = dataPort;

        // "user.home": user home dir
        Repository.rootDir = System.getProperty("user.dir"); // user current working dir
        Repository.currentDir = System.getProperty("user.dir") + Repository.fileSeperator + "ftpHome";
    }

    public void run() {
        try{
            controlIn = new BufferedReader(
                    new InputStreamReader(controlSocket.getInputStream()));
            controlOut = new PrintWriter(controlSocket.getOutputStream(), true);

            msgToClient("220 Connected. Hello from FTP-Server."); //controlPort connected

            while(quitCommand) {
                // access command from client
                String userCommand = controlIn.readLine();
                System.out.println("control server receive command " + userCommand);

                // verify user input
                if(userCommand != null) {
                    CommandProcessor(userCommand);
                }
                else{
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                controlIn.close();
                controlOut.close();
                controlSocket.close();
            } catch (IOException e) {
                System.out.println("cannot stop control socket");
                e.printStackTrace();
            }
        }
    }

    private void msgToClient(String msg) {
        controlOut.println(msg);
    }

    private void CommandProcessor(String userCommand) throws IOException {
        int indexOfSpace = userCommand.indexOf(" ");
        String command = "";
        String args = "";

        if (indexOfSpace == -1) { // no args
            command = userCommand.toUpperCase();
            args = null;
        } else {
            command = userCommand.substring(0, indexOfSpace).toUpperCase();
            args = userCommand.substring(indexOfSpace + 1);
        }

        switch (command) {
            // user login
            case "USER":
                new USERCommand(args, controlOut, this);
                break;

            case "PASS":
                new PASSCommand(args, controlOut, this);
                break;

            // passive mode
            case "PASV":
                if (userLogin) {
                    new PASVCommand(dataPort, args, controlOut, this);
                } else {
                    msgToClient("Please login first");
                }
                break;

            // active mode
            case "PORT":
                if (userLogin) {
                    new PORTCommand(args, controlOut);
                } else {
                    msgToClient("Please login first");
                }
                break;

            // user retrieve file from server
            case "RETR":
                if(dataConnection != null) {
                    new RETRCommand(args, controlOut, this);
                }
                else{
                    msgToClient("Choose active/passive mode first.");
                }
                break;

            // user upload file to server
            case "STOR":
                if(dataConnection != null) {
                    new STORCommand(args, controlOut, this);
                }
                else{
                    msgToClient("Choose active/passive mode first.");
                }
                break;

            // delete file
            case "RMD":
                new RMDCommand();
                break;

            // current directory
            case "PWD":
                new PWDCommand(controlOut);
                break;

            // list content of current dir
            case "LIST":
                if(args == null){
                    new LISTCommand(Repository.currentDir, controlOut, this);
                }
                else {
                    if (userLogin == true) {
                        new LISTCommand(args, controlOut, this);
                    } else {
                        msgToClient("User not login.");
                    }
                }
                break;

            // user quit
            case "QUIT":
                msgToClient("Connection closed. Service stopped.");
                this.quitCommand = false;
                break;

            case "CWD":
                break;


            case "NLST":
                break;


            case "EPSV":
                break;

            case "SYST":
                break;

            case "FEAT":
                break;

            case "EPRT":
                break;

            case "MKD":
                break;

            case "TYPE":
                break;

            default:
                // invalid command
                msgToClient("501 Invalid command.");
                break;
        }
    }

    public boolean getUserStatus(){
        return userLogin;
    }

    public void setUserStatus(boolean isLogin){
        this.userLogin = true;
    }

    public void setUser(String user){
        this.USER.set(user);;
    }

    public void setDataHost(String host){
        this.dataHost = host;
    }

    public void setDataPort(int port){
        this.dataPort = port;
    }

    public void setDataConnection(Socket socket){
        this.dataConnection = socket;
    }

    public Socket getDataConnection(){
        return dataConnection;
    }

    public void setPassiveDataSocket(ServerSocket dataSocket){ this.passiveDataSocket = dataSocket; }

    public void closePassiveDataSocket() throws IOException {
        this.passiveDataSocket.close();
    }

    public void closeDataConnection() throws IOException {
        this.dataConnection.close();
    }
}
