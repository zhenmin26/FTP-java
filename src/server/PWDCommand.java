package server;

import java.io.PrintWriter;

public class PWDCommand {
    public PWDCommand(PrintWriter out){
        out.println("257 \"" + Repository.currentDir + "\"");
    }
}
