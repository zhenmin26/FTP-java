package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

public class DataConnection {
    // socket
    private Socket dataConnection;
    private ServerSocket passiveDataSocket;

    // active mode
    /**
     * 主动模式
     * @param host 客户端指定的IP地址
     * @param port 客户端指定的端口
     * @param out control socket
     * @param thread 对应线程
     */
    public DataConnection(String host, int port, PrintWriter out, WorkingThread thread) {
        try{
            System.out.println("Active mode - host: " + host +
                    " port: " + port);
            // 新建数据连接
            this.dataConnection = new Socket(host, port);
            // 在相应线程内更新数据连接
            thread.setDataConnection(dataConnection);
            thread.setPassiveDataSocket(null);
            // 提示客户端数据连接已经建立
            out.println("230 Data connection established - active mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // passive mode

    /**
     * 被动模式
     * @param port 端口
     * @param out control socket
     * @param thread 对应线程
     */
    public DataConnection(int port, PrintWriter out, WorkingThread thread) {
        try{
            System.out.println("Passive mode - dataport: " + port);
            // 接收客户端连接
            passiveDataSocket = new ServerSocket(port);
            this.dataConnection = passiveDataSocket.accept();
            // 在相应咸亨内更新数据连接和socket
            thread.setDataConnection(dataConnection);
            thread.setPassiveDataSocket(passiveDataSocket);
            // 提示客户端数据连接已经建立
            out.println("230 Data connection established - passive mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
