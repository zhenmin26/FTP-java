package server;

import java.io.PrintWriter;

public class PORTCommand {
    public PORTCommand(String args, PrintWriter out) {
        // set host and port
        String host = "test";
        int port = 9999;
        new DataConnection(host, port, out);
    }
}
