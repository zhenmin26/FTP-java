package server;

import java.io.File;
import java.io.PrintWriter;

public class RMDCommand {
    public RMDCommand(String args, PrintWriter out, WorkingThread thread){
        String currentPath = Repository.currentDir;
        String file = currentPath + Repository.fileSeperator + args;
        File f = new File(file);
        if(f.exists()){
            if(f.delete()) {
                if (f.isDirectory()) {
                    out.println("250 Directory was successfully deleted.");
                } else {
                    out.println("250 File was successfully deleted.");
                }
            }
            else{
                out.println("550 Requested action not taken. File unavailable.");
            }
        }
        else{
            out.println("550 Invalid file name.");
        }
    }
}
