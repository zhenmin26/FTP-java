package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkingThread extends Thread{
    // file path
    private String rootDir;
    private String currentDir;
    final String fileSeperator = File.separator;

    // user of thread
    public static final ThreadLocal<String> USER = new ThreadLocal<String>();

    // control socket
    private Socket controlSocket;

    // data socket
    private ServerSocket dataSocket;
    private int dataPort;

    // data flow
    private BufferedReader controlIn;
    private PrintWriter controlOut;

    public WorkingThread(Socket client, int dataPort){
        this.controlSocket = client;
        this.dataPort = dataPort;

        // "user.home": user home dir
        this.rootDir = System.getProperty("user.dir"); // user current working dir
        this.currentDir = System.getProperty("user.dir") + fileSeperator + "ftpHome";
    }

    public void run() {
        try{
            controlIn = new BufferedReader(
                    new InputStreamReader(controlSocket.getInputStream()));
            controlOut = new PrintWriter(controlSocket.getOutputStream(), true);

            controlOut.println("2xx Hello from FTP-Server"); //controlPort connected

            while(true) {
                // access command from client
                String userCommand = controlIn.readLine();
                System.out.println("control server receive command " + userCommand);

                CommandProcessor(userCommand);
//                if(userCommand.equals("end")){
//                    break;
//                }
            }
//            System.out.println("outside while loop, ready to close control socket");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                controlSocket.close();
            } catch (IOException e) {
                System.out.println("cannot stop control socket");
                e.printStackTrace();
            }
        }
    }

    private void CommandProcessor(String userCommand){
        int indexOfSpace = userCommand.indexOf(" ");
        String command = "";
        String args = "";

        if(indexOfSpace == -1) { // no args
            command = userCommand.toUpperCase();
            args = null;
        }
        else {
            command = userCommand.substring(0,indexOfSpace).toUpperCase();
            args = userCommand.substring(indexOfSpace+1);

        }

        switch(command){
            // user login
            case "USER":
                new USERCommand(args, controlOut);
                break;

            case "PASS":
                new PASSCommand(args, controlOut);
                break;

            case "CWD":
                new CWDCommand(args, controlOut);
                break;

            case "LIST":

                break;

            case "NLST":

                break;

            case "PWD":

                break;

            case "QUIT":

                break;

            case "PASV":

                break;

            case "EPSV":

                break;

            case "SYST":

                break;

            case "FEAT":

                break;

            case "PORT":

                break;

            case "EPRT":

                break;

            case "RETR":

                break;

            case "MKD":

                break;

            case "RMD":

                break;

            case "TYPE":

                break;

            case "STOR":

                break;

            default:
                // invalid command
                break;
        }

    }
}
