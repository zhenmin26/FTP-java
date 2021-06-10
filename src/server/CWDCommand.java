package server;

import java.io.PrintWriter;

public class CWDCommand {
    public CWDCommand(String args, PrintWriter out){
        String responseToClient;
        System.out.println("CWD command -- args from user: " + args);
        // verify user
        if(Repository.userLoginStatus == true){
            responseToClient = "user status: login";
        }
        else{
            responseToClient = "user status: not login";
        }
        // send response to client
        out.println(responseToClient);
        out.flush();
    }
}
