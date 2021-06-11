package server;

import java.io.PrintWriter;

public class PASVCommand {
    private int dataPort;

    public PASVCommand(int dataPort, String args, PrintWriter out, WorkingThread thread) {
        this.dataPort = dataPort;

        // set host and port
        String serverIP = "127.0.0.1";
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
