package server;

import java.io.*;

public class RETRCommand {
    private BufferedInputStream fin;
    private BufferedOutputStream fout;

    /**
     * RETR 将文件从服务端传送到客户端
     * @param file
     * @param out
     * @param thread
     * @throws IOException
     */
    public RETRCommand(String file, PrintWriter out, WorkingThread thread) throws IOException {
        File f = new File(Repository.currentDir + Repository.fileSeperator + file);
        fin = null;
        fout = null;
        if(!f.exists()){ // 判断文件是否存在
            out.println("550 File does not exist.");
        }
        // 文件传输过程
        else{
            out.println("Data transfer starts");

            fin = new BufferedInputStream(new FileInputStream(f));
            fout = new BufferedOutputStream(thread.getDataConnection().getOutputStream());

            // buffer
            byte[] buffer = new byte[1024];
            int len = 0;
            try {
                while((len = fin.read(buffer, 0, 1024)) != -1){
                    fout.write(buffer, 0, len);
                    fout.flush();
                }
            }
            catch (IOException e) {
                out.println("Could not read from file.");
                e.printStackTrace();
            }
        }

        // 关闭文件流
        try {
            if(fin != null) {
                fin.close();
            }
            if(fout != null) {
                fout.close();
            }
        } catch (IOException e) {
            out.println("Could not close file stream.");
            e.printStackTrace();
        }

        out.println("226 File transferred successfully");

        // 关闭数据连接和socket(如果是被动模式)
        if(thread.getPassiveDataSocket() != null) {
            thread.closePassiveDataSocket();
            thread.setPassiveDataSocket(null);
        }
        thread.closeDataConnection();
        thread.setDataConnection(null);
    }
}
