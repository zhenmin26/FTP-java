import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
/*通过server主动连接client提供的端口从而建立data连接
 * 但control端应该是不受影响的
 */
//自动填充版本
public class controlconnection {
	private String user;
	private String pass;
	private Socket ctrsocket;
	private Socket datasocket;
	private static BufferedReader ctrin;
	private static PrintWriter ctrout;
	private boolean passive_mode=false;
	private String cdir="/Users/mayining/eclipse-workspace/FTP-master/client-directory/";
//	private String sdir;
	public controlconnection(String ip, String username, String password) {
		try {
			ctrsocket=new Socket(ip,5678);
			setUsername(username);
			setPassword(password);
			ctrin = new BufferedReader(new InputStreamReader(ctrsocket.getInputStream()));
            ctrout = new PrintWriter(new OutputStreamWriter(ctrsocket.getOutputStream()), true);
			startCtrConnection();
			System.out.println("please choose active/passive transition");
			while(true) {
				//首先读用户选择传递的方式是主动还是被动
				BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));
			    String str=wt.readLine();
				if(!str.equals("quit")) {
					//主动模式下，读取用户需要的操作及参数
					if(str.equals("active")) {
						System.out.println("active");
						active_mode();
						processcmd();
					}
					if(str.equals("passive") ){
						passive_mode();
						processcmd();
	
					}else{
						System.out.println("invalid command,please rewrite");
					}
				}else {
					do_quit();
					ctrsocket.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//终端版本
public controlconnection() {
	try {
		 Socket client = new Socket("127.0.0.1", 5678);
		 ctrin = new BufferedReader(new InputStreamReader(client.getInputStream()));//读
	     ctrout = new PrintWriter(client.getOutputStream(),true);

		BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));//从控制台读内容
	      String response=ctrin.readLine();
	      System.out.println(response);
	      
	    while(!response.startsWith("230")){
	        String str = wt.readLine();
	        ctrout.println(str);
	        ctrout.flush();
	        if(str.equals("end")){
	            break;
	        }
	        response=ctrin.readLine();
	        System.out.println(response);
	        }
	        
	    while(true) {
	    	    String cmd = wt.readLine();
				if(!cmd.equals("quit")) {
				if(cmd.equals("active")) {
					active_mode();
					BufferedReader wt1 = new BufferedReader(new InputStreamReader(System.in));
					
				}
				if(cmd.equals("passive") ){
					passive_mode();
					BufferedReader wt1 = new BufferedReader(new InputStreamReader(System.in));
				
				}else{
					System.out.println("invalid command,please rewrite");
				}
				}else {
					do_quit();
					client.close();
					break;
				}
	    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
}
	
				
	
	
	public void setUsername(String username) {
		this.user=username;
	}
	
	public void setPassword(String password) {
		this.pass=password;
	}
	
	public void startCtrConnection() throws Exception {
		String response;
		do {
			response=ctrin.readLine();
		}while(!response.startsWith("220"));
		ctrout.println("USER "+user);
		ctrout.flush();
		//String response;
		response = ctrin.readLine();
		if(!response.startsWith("331")) {
			throw new IOException("invalid username, the response is "+response);
		}
		System.out.println(response);
		ctrout.println("PASS "+pass);
		ctrout.flush();
		response=ctrin.readLine();
		if(!response.startsWith("230")) {
			throw new IOException("invalid password, the response is "+response);	
		}
		System.out.println(response);
	}
	
	/*开启主动模式
	 * 提供一个随机端口，以“PORT +端口”的格式发送信息给服务器并等待连接
	 */
	public void active_mode() throws Exception {
		String response;
		int dataport=(int)(Math.random()*100000%9999)+1024;//random dataport
		System.out.println(dataport);
		ctrout.println("PORT "+dataport);//send information to server
		
		response=ctrin.readLine();
		System.out.println(response);
		ServerSocket dataSocketserver=new ServerSocket(dataport);
		datasocket=dataSocketserver.accept();//connect and listen
		response=ctrin.readLine();
		System.out.println(response);
		
	}
	
	/*
	 * 被动模式，读取server发来的IP号和端口号并建立data连接
	 */
	public Socket passive_mode() throws Exception {
		String response;
		//if(!passive_mode) {
			ctrout.println("PASV");
		response=ctrin.readLine();
		if(!response.startsWith("2271")) {
			throw new Exception("cannot change to passive mode");
		}
		int begin = response.indexOf('(');
        int end = response.indexOf(')', begin + 1);
        String dataip="";
        int dataport=0;
        if (end > 0) {
            String info = response.substring(begin + 1, end);
            StringTokenizer tokenizer = new StringTokenizer(info, ",");
        dataip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                + tokenizer.nextToken() + "." + tokenizer.nextToken();
        dataport = Integer.parseInt(tokenizer.nextToken()) * 256
                + Integer.parseInt(tokenizer.nextToken());
        }
        Socket dataSocket=new Socket(dataip,dataport);
        response=ctrin.readLine();
        System.out.println(response);
        return dataSocket;
	//}
	}
	public void processcmd() throws Exception {
		BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));
		String line=wt.readLine().trim();
		String[] commandline=line.split(" ");
		String cmd="";
		System.out.print("ftp> ");
		StringBuilder cmdarg=new StringBuilder();
		if(commandline!=null) {
			cmd=commandline[0].trim();
			for(int i=1;i<commandline.length;i++) {
				cmdarg.append(commandline[i]);
				cmdarg.append(" ");
			}
		}
			switch(cmd) {
				case "put":	
					do_upload(cmdarg.toString().trim());
				case "get":
					do_download(cmdarg.toString().trim());
				case"quit":
					do_quit();
				case"delete":
					do_delete(cmdarg.toString().trim());
				case"list":
					do_list();
			}
	}
	
	public void do_quit() {
		
	}
	
	public void do_upload(String filename) throws Exception {
		String response="";
		String path=cdir+filename;
		File file=new File(path);
		if(!file.exists()) {
			System.out.println("file doesnot exist");
			return;
		}
		FileInputStream fileinput = new FileInputStream(file);
        BufferedInputStream input = new BufferedInputStream(fileinput);
        ctrout.println("STOR "+filename);
        response=ctrin.readLine();
        System.out.println(response);
        BufferedOutputStream dataout=new BufferedOutputStream(datasocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            dataout.write(buffer, 0, bytesRead);//将待传文件读到缓冲区,并从缓冲区中读入到dataout中，传到server
        }
        dataout.flush();
        dataout.close();
        input.close();
        datasocket.close();
        response = ctrin.readLine();
        System.out.println(response);
	}
	
	public void do_download(String filename) throws Exception {
		String response;
		ctrout.println("RETR "+filename);
		response=ctrin.readLine();
		BufferedInputStream datain = new BufferedInputStream(datasocket.getInputStream());
		//检查本地文件夹中如果有这个文件，则直接删除
		File outfile=new File(cdir+filename);
		if(outfile.exists()) {
			outfile.delete();
		}
		BufferedOutputStream output=new BufferedOutputStream(new FileOutputStream(new File(cdir, filename)));
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = datain.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead); //数据通过datasocket读到缓存区，再写入本地
        }
        datain.close();
        output.flush();
        output.close();
        datasocket.close();
        response = ctrin.readLine();
        System.out.println(response);
	}
	
	public void do_list() throws Exception {
		ctrout.println("LIST");
		String content=ctrin.readLine();
		while(!content.equals("$")) {
			System.out.println(content);
			content=ctrin.readLine();
		}
	}
	
	public void do_delete(String filename) throws Exception{
		
	}
	
    public static void main(String[] args) throws Exception {
    	String user="admin";
    	String pass="admin";
    	new controlconnection("127.0.0.1",user,pass);//自动填充版本
  
    }
}
    
