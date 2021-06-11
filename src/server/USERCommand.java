package server;

import java.io.PrintWriter;

public class USERCommand {
    public USERCommand(String args, PrintWriter out, WorkingThread thread){
        String responseToClient;
        System.out.println("USER command -- args from user: " + args);
        // verify user
        if(thread.getUserStatus() == false) {
            if (args.equals(Repository.validUser)) {
                thread.setUser(args);
                responseToClient = "331 Please specify the password.";
            } else {
                responseToClient = "501 Invalid user.";
            }
        }
        else{
            responseToClient = "User already logged in.";
        }
        // send response to client
        out.println(responseToClient);
        out.flush();
    }
}
