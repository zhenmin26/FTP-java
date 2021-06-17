package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * list命令
 */
public class LISTCommand {
    /**
     *
     * @param name 文件夹名
     * @param out control socket
     * @param thread 对应的线程
     * @throws IOException
     */
    public LISTCommand(String name, PrintWriter out, WorkingThread thread) throws IOException {
        File f = new File(name);
        if(f.exists() && f.isDirectory()){
            File[] files = f.listFiles();
            String content = "";
            for(int i=0; i<files.length; i++) {
                out.println(i + " - " + files[i].getName() + " - " + ((files[i].isFile())?"File":"Directory") + " - " + files[i].length() + " bytes");
            }
            out.println("$");
        }
        else if(f.exists() && f.isFile()){
            out.println("File " + f.getName());
        }
        else{
            out.println("550 Invalid File/ Directory");
        }
    }
}
