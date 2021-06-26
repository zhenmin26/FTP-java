package server;

import java.io.File;
import java.io.PrintWriter;

/**
 * rmd命令
 */
public class RMDCommand {
    /**
     *
     * @param args 要删除的文件名/文件夹名
     * @param out control socket
     * @param thread 对应线程
     */
    public RMDCommand(String args, PrintWriter out, WorkingThread thread){
        String currentPath = Repository.currentDir;
        String file = currentPath + Repository.fileSeperator + args;
        File f = new File(file);
        if(f.exists()){ // 判断文件是否存在
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
