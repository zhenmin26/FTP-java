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
        // set host and port

        String[] data = (args.substring(args.indexOf("(")+1, args.indexOf(")"))).split(",");
        String host = data[0] + "." + data[1] + "." + data[2] + "." + data[3];
        int port = Integer.parseInt(data[4]) * 256 + Integer.parseInt(data[5]);

        thread.setDataHost(host);
        thread.setDataPort(port);

        out.println("Active mode - host: " + host + " port: " + port);
        new DataConnection(host, port, out, thread);
    }
}
