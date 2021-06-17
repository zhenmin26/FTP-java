package server;

import java.io.PrintWriter;

/**
 * PASV命令
 */
public class PASVCommand {
    private int dataPort;

    /**
     * PASV 被动模式
     * @param dataPort 数据端口
     * @param out controlSocket，用来向客户端发出消息
     * @param thread 对应的线程
     */
    public PASVCommand(int dataPort, PrintWriter out, WorkingThread thread) {
        this.dataPort = dataPort;

        // set host and port
//        String serverIP = "127.0.0.1"; // local test
        String serverIP = "172.20.10.7"; // remote test
        String[] data = serverIP.split("\\.");
        int p1 = dataPort / 256;
        int p2 = dataPort % 256;

        thread.setDataHost(serverIP);
        thread.setDataPort(dataPort);

        out.println("227 Entering Passive mode (" +
                data[0] + "," + data[1] + "," + data[2] + "," + data[3] + ","
                + p1 + "," + p2 + ")");

        new DataConnection(dataPort, out, thread);
    }
}
