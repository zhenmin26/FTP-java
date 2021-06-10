package server;

import java.io.PrintWriter;

public class PASSCommand {
    public PASSCommand(String args, PrintWriter out){
        String responseToClient;
        System.out.println("PASS command -- args from user: " + args);
        // verify user
        if(args.equals(Repository.validPassword)){
            responseToClient = "2xx Login successfully";
            // set user status
            Repository.userLoginStatus = true;
        }
        else{
            responseToClient = "5xx Wrong password";
        }
        // send response to client
        out.println(responseToClient);
        out.flush();
    }
}
