import java.io.BufferedReader;
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
	private static BufferedReader ctrin;
	private static PrintWriter ctrout;
	private boolean passive_mode=false;
//	private String ip="127.0.0.1";
//	private int serverport=21; 这两部分有什么必要？
	
	public controlconnection(String ip, String username, String password) {
		try {
			Socket ctrsocket=new Socket(ip,5678);
			setUsername(username);
			setPassword(password);
			ctrin = new BufferedReader(new InputStreamReader(ctrsocket.getInputStream()));
            ctrout = new PrintWriter(new OutputStreamWriter(ctrsocket.getOutputStream()), true);
			startCtrConnection();
			System.out.println("please choose active/passive transition");
			while(true) {
				BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));
				if(!wt.readLine().equals("quit")) {
					if(wt.readLine().equals("active")) {
						active_mode();
						String line=wt.readLine().trim();
						String[] commandline=line.split(" ");
						String cmd="";
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
									do_upload_active(cmdarg.toString().trim());
							}
						
					}
					if(wt.readLine().equals("passive") ){
						passive_mode();
			
					
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
	 * 返回一个datasocket，或者将这个写为实例变量？
	 */
	public Socket active_mode() throws Exception {
		String response;
		int dataport=(int)(Math.random()*100000%9999)+1024;//random dataport
		System.out.println(dataport);
		ctrout.println("PORT "+dataport);//send information to server
		response=ctrin.readLine();
		System.out.println(response);
		ServerSocket dataSocketserver=new ServerSocket(dataport);
		Socket dataSocket=dataSocketserver.accept();//connect and listen
		response=ctrin.readLine();
		System.out.println(response);
		return dataSocket;
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
	
	public void do_quit() {
		
	}
	
	public void do_upload_active(String filename) {
		
	}
	
	public void do_upload_passive(String filename) {
		
	}
	
	public void do_download_active(String filename) {
		
	}
	
	public void do_download_passive(String filename) {
		
	}
	
    public static void main(String[] args) throws Exception {
    	String user="admin";
    	String pass="admin";
    	new controlconnection("127.0.0.1",user,pass);//自动填充版本
  
    }
}
    
