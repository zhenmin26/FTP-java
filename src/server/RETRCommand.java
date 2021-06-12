package server;

import java.io.*;

public class RETRCommand {
    public RETRCommand(String file, PrintWriter out, WorkingThread thread) throws IOException {
        File f = new File(Repository.currentDir + Repository.fileSeperator + file);
        BufferedInputStream fin = null;
        BufferedOutputStream fout = null;
        if(!f.exists()){
            out.println("550 File does not exist.");
        }
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

        // close stream
        try {
            fin.close();
            fout.close();
        } catch (IOException e) {
            out.println("Could not close file stream.");
            e.printStackTrace();
        }

        out.println("226 File transferred successfully");

        if(thread.getPassiveDataSocket() != null) {
            thread.closePassiveDataSocket();
            thread.setPassiveDataSocket(null);
        }
        thread.closeDataConnection();
        thread.setDataConnection(null);
    }
}
