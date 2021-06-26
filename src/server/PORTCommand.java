package server;

import java.io.PrintWriter;

/**
 * PORT命令
 */
public class PORTCommand {
    /**
     * PORT 主动模式
     * @param args 传入用来生成sdata socket的参数，格式样例(127,0,0,1,22,24)
     * @param out controlSocket，用来向客户端发出消息
     * @param thread 对应的线程
     */
    public PORTCommand(String args, PrintWriter out, WorkingThread thread) {
        // 接收参数，得到IP和端口
        String[] data = (args.substring(args.indexOf("(")+1,
                args.indexOf(")"))).split(",");
        String host = data[0] + "." + data[1] + "." + data[2] + "." + data[3];
        int port = Integer.parseInt(data[4]) * 256 + Integer.parseInt(data[5]);

        // 更新当前线程对应的主动模式下数据连接的客户端IP和端口
        thread.setDataHost(host);
        thread.setDataPortActive(port);

        out.println("Active mode - host: " + host + " port: " + port);
        // 新建DataConnection对象，建立数据连接
        new DataConnection(host, port, out, thread);
    }
}
