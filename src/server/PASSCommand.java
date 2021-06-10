package server;

import java.io.PrintWriter;

public class PASSCommand {
    public PASSCommand(String args, PrintWriter out) {
        String responseToClient;
        System.out.println("PASS command -- args from user: " + args);
        // verify user
        if(Repository.userLoginStatus == true){
            responseToClient = "User already logged in";
        }
        else {
            if (args.equals(Repository.validPassword)) {
                responseToClient = "230 Login successfully";
                // set user status
                Repository.userLoginStatus = true;
            } else {
                responseToClient = "530 Wrong password";
            }
        }
        // send response to client
        out.println(responseToClient);
        out.flush();
    }
}
