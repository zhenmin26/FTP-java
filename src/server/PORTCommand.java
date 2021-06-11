package server;

import java.io.PrintWriter;

public class PORTCommand {
    public PORTCommand(String args, PrintWriter out) {
        // set host and port
        String[] data = args.split(",");
        String host = data[0] + "." + data[1] + "." + data[2] + "." + data[3];
        int port = Integer.parseInt(data[4]) * 256 + Integer.parseInt(data[5]);
        out.println("Active mode - host: " + host + ", port: " + port);
        new DataConnection(host, port, out);
    }
}
