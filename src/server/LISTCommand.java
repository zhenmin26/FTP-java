package server;

import java.io.File;
import java.io.PrintWriter;

public class LISTCommand {
    public LISTCommand(String name, PrintWriter out, WorkingThread thread){
        File f = new File(name);
        if(f.exists() && f.isDirectory()){
            String[] files = f.list();
            for(int i=0; i<files.length; i++) {
                out.println(i + " - " + files[i]);
            }
            out.println("end");
        }
        else if(f.exists() && f.isFile()){
            out.println("File " + f.getName());
        }
        else{
            out.println("550 Invalid File/ Directory");
        }
    }
}
