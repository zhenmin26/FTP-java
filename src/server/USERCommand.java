package server;

import java.io.PrintWriter;

/**
 * user命令
 */
public class USERCommand {
    /**
     * 用户名
     * @param args 客户端传来的用户名
     * @param out control socket
     * @param thread 对应的线程
     */
    public USERCommand(String args, PrintWriter out, WorkingThread thread){
        String responseToClient;
        System.out.println("USER command -- args from user: " + args);
        // 验证用户
        if(!thread.getUserStatus()) { // 判断用户是否已经登陆
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
