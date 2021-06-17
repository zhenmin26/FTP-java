package server;

import java.io.PrintWriter;

/**
 * pwd命令
 */
public class PWDCommand {
    /**
     * PWD 向用户返回当前所在目录
     * @param out
     */
    public PWDCommand(PrintWriter out){
        out.println("257 \"" + Repository.currentDir + "\"");
    }
}
