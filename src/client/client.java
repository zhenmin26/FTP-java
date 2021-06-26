//package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class client {
	private Socket ctrsocket;
	private static BufferedReader ctrin;
	private static PrintWriter ctrout;
	private ServerSocket activedataserver;
	private Socket datasocket;
	private String ip="127.0.0.1";//本机的ip地址
	private String cdir=" ";//本机的目录
	
	public client(String ip, int port) {
		try {
			  
				 ctrsocket = new Socket(ip, port);
				 ctrin = new BufferedReader(new InputStreamReader(ctrsocket.getInputStream()));//读
			     ctrout = new PrintWriter(ctrsocket.getOutputStream(),true);
			     BufferedReader wt = new BufferedReader(new InputStreamReader(System.in));//从控制台读用户输入
			     String response=ctrin.readLine();
			     System.out.println(response);//读取服务器回复
			      
			     while(!response.startsWith("230")){//在登陆成功之前，直接反馈给服务器用户输入，直到登陆成功后再解析其他命令
			        String str = wt.readLine();
			        ctrout.println(str);
			        ctrout.flush();
			        if(str.equals("quit")){
			        	System.out.println(ctrin.readLine());
			        	ctrsocket.close();
			        	wt.close();
			            break;
			        }
			        response=ctrin.readLine();
			        System.out.println(response);
			     }
			     
			     //如果用户输入quit则及时退出,如果控制端口已关闭则不再读取命令
		        String cmd="";
		        while(!cmd.equals("quit")&&!ctrsocket.isClosed()) {
				//首先读用户选择传递的方式是主动还是被动
				//System.out.println("please choose active/passive transition");
					System.out.print("miniftp> ");
					String line=wt.readLine().trim();//分隔用户输入
					String[] commandline=line.split(" ");	
					StringBuilder cmdarg=new StringBuilder();//逐个装载用户输入的参数
					if(commandline!=null) {
						cmd=commandline[0].trim();
						for(int i=1;i<commandline.length;i++) {
							cmdarg.append(commandline[i]);
							cmdarg.append(" ");
						}
					}
				    switch(cmd) {
				    case"quit":
				    	do_quit();
						wt.close();
						break;
				    case"list":
				    	do_list();
				    	break;
				    case"active":
				    	active_mode();
				    	break;
				    case"passive":
				    	passive_mode();
				    	break;
				    case"put":
				    	do_upload(cmdarg.toString().trim());
				    	break;
				    case"get":
				    	do_download(cmdarg.toString().trim());
				    	break;
				    case"delete":
				    	do_delete(cmdarg.toString().trim());
				    	break;
				    case"pwd":
				    	ctrout.println("PWD");
				    	System.out.println(ctrin.readLine());
				    	break;
				    default:
				    System.out.println("invalid command");
				    break;
				}	    
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	
	/*开启主动模式
	 * 提供一个随机端口，以“PORT +端口”的格式发送信息给服务器并等待连接
	 */
	public void active_mode() throws Exception {
		String response;
		int dataport=(int)(Math.random()*1000%999)+1024;//random dataport
		int dataport1=dataport/256;
		int dataport2=dataport%256;
		
		StringTokenizer ipparts = new StringTokenizer(this.ip, ".");
		String res="PORT "+"("+ipparts.nextToken()+","+ipparts.nextToken()+","+ipparts.nextToken()+","+ipparts.nextToken()+","+dataport1+","+dataport2+")";
		ctrout.println(res);//send information to server
		response=ctrin.readLine();
		System.out.println(response);
	//connect and listen
		activedataserver=new ServerSocket(dataport);
		 datasocket=activedataserver.accept();
		response=ctrin.readLine();
		System.out.println(response);
		if(response.startsWith("230")) {
			return;
		}
	}
	
	/*
	 * 被动模式，读取server发来的IP号和端口号并建立data连接
	 */
	public void passive_mode() throws Exception {
		String response;
		//if(!passive_mode) {
		ctrout.println("PASV");
		response=ctrin.readLine();
		if(!response.startsWith("227")) {//服务器需要反馈227开头的内容才继续操作
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
                + tokenizer.nextToken() + "." + tokenizer.nextToken();//读取服务器ip
        dataport = Integer.parseInt(tokenizer.nextToken()) * 256
                + Integer.parseInt(tokenizer.nextToken());//读取服务器开放的数据端口
        }
        //System.out.println(dataip+dataport);
       datasocket=new Socket(dataip,dataport);
        response=ctrin.readLine();
        System.out.println(response);
	//}
	}
	/*
	 * 退出命令，向服务器发送quit请求
	 */
	public void do_quit() throws Exception {
		ctrout.println("QUIT");
		System.out.println(ctrin.readLine());
		ctrsocket.close();
	}
	/*
	 * 上传命令，首先需要选择主动/被动模式确保datasocket开启
	 * 文件不存在则报错退出
	 * 通过将文件以二进制读取到缓冲区，再输出到服务器
	 */
	public void do_upload(String filename) throws Exception {
		if((datasocket==null) ||(datasocket.isClosed())) {
			System.out.println("please choose active/passive mode");
			return;
		}
		String response="";
		String path=cdir+filename;
		File file=new File(path);
		//System.out.println(path);	
		if(!file.exists()) {
			System.out.println("file does not exist");
			return;
		}
		FileInputStream fileinput = new FileInputStream(file);
        BufferedInputStream input = new BufferedInputStream(fileinput);
        ctrout.println("STOR "+filename);
        response=ctrin.readLine();
        System.out.println(response);
        if(response.startsWith("550")) {//550为服务器报错反馈，读到就返回退出
        	input.close();
        	datasocket.close();
        	return;
        }
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
	/*
	 * 下载命令，首先需要选择主动/被动模式确保datasocket打开
	 * 向服务器发送请求，若文件本地已存在，则先删除，再读取新文件
	 */
	public void do_download(String filename) throws Exception {
		if((datasocket==null)||(datasocket.isClosed())) {
			System.out.println("please choose active/passive mode");
			return;
		}
		String response;
		ctrout.println("RETR "+filename);
		response=ctrin.readLine();
		System.out.println(response);
        if(response.startsWith("550")) {
        	return;
        }
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
	//展示当前目录下文件信息
	public void do_list() throws Exception {
		ctrout.println("LIST");
		String content=ctrin.readLine();
		if(content.startsWith("550")) {
			return;
		}
		while(!content.equals("$")) {//服务器将在结尾输入$来表示结束
			System.out.println(content);
			content=ctrin.readLine();
		}
	}
	//删除指定文件
	public void do_delete(String filename) throws Exception{
		System.out.println(filename);
		ctrout.println("RMD "+filename);
		System.out.println(ctrin.readLine());
	}
	
    public static void main(String[] args) throws Exception {
    	new client("127.0.0.1",5678);
  
    }
}
    
