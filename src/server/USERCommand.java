package server;

import java.io.PrintWriter;

public class USERCommand {
    public USERCommand(String args, PrintWriter out){
        String responseToClient;
        System.out.println("USER command -- args from user: " + args);
        // verify user
        if(Repository.userLoginStatus == false) {
            if (args.equals(Repository.validUser)) {
                responseToClient = "331 password please";
            } else {
                responseToClient = "5xx invalid user";
            }
        }
        else{
            responseToClient = "User already logged in";
        }
        // send response to client
        out.println(responseToClient);
        out.flush();
    }
}
