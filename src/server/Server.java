package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端
 */
public class Server {
    private int controlPort = 5678;
    private ServerSocket server;

    public Server(){
        try{
            server = new ServerSocket(controlPort); // 监听5678端口
        }
        catch(IOException e){
            System.out.println("cannot create server...");
            System.exit(-1);
        }

        System.out.println("FTP Server listening on control port " + controlPort);

        int numberOfThreads = 0;

        while(true){
            try{
                // 接收客户端连接
                Socket client = server.accept();
                int dataPort = controlPort + numberOfThreads + 1;

                // 新建工作线程
                WorkingThread newWorkingThread = new WorkingThread(client, dataPort);

                System.out.println("New connection! New Thread created!");
                numberOfThreads++;

                // 启动工作线程
                newWorkingThread.start();
            }
            catch(IOException e){
                System.out.println("error from accept");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
