package server;

import java.io.*;

public class STORCommand {
    private BufferedInputStream fin;
    private BufferedOutputStream fout;

    public STORCommand(String args, PrintWriter out, WorkingThread thread) throws IOException {
        String fileName = args;
        File f = new File(Repository.currentDir + Repository.fileSeperator + args);

        if(f.exists()){
            out.println("550 File already exists");
        }
        else{
            fin = new BufferedInputStream(thread.getDataConnection().getInputStream());
            fout = new BufferedOutputStream(new FileOutputStream(f));
            out.println("Start receiving file " + args);

            byte[] buffer = new byte[1024];
            int len = 0;
            try{
                while((len = fin.read(buffer, 0, 1024)) != -1){
                    fout.write(buffer, 0, len);
                }
            } catch (IOException e){
                out.println("Could not read from or write to file");
                e.printStackTrace();
            }

            // close stream
            try{
                fin.close();
                fout.close();
            } catch (IOException e){
                out.println("Could not close io stream");
                e.printStackTrace();
            }

            out.println("226 File "  + f.getName() + " transfer successful");
        }

        if(thread.getPassiveDataSocket() != null) {
            thread.closePassiveDataSocket();
            thread.setPassiveDataSocket(null);
        }
        thread.closeDataConnection();
        thread.setDataConnection(null);
    }
}
