package server;

import java.io.PrintWriter;

public class PASVCommand {
    public PASVCommand(String args, PrintWriter out) {
        // set port
        int port = 8888;
        new DataConnection(port, out);
    }
}
