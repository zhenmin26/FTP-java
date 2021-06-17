package server;

import java.io.PrintWriter;

/**
 * pass命令
 */
public class PASSCommand {
    /**
     * 密码
     * @param args 客户端传来的密码
     * @param out control socket
     * @param thread 对应的线程
     */
    public PASSCommand(String args, PrintWriter out, WorkingThread thread) {
        String responseToClient;
        System.out.println("PASS command -- args from user: " + args);
        // verify user
        if(Repository.userLoginStatus == true){
            responseToClient = "User already logged in.";
        }
        else {
            if (args.equals(Repository.validPassword)) {
                responseToClient = "230 Login successfully.";
                // set user status
                thread.setUserStatus(true);
            } else {
                responseToClient = "530 Wrong password.";
            }
        }
        // send response to client
        out.println(responseToClient);
        out.flush();
    }
}
