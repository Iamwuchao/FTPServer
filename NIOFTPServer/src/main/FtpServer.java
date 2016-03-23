package main;

import java.io.*;
import java.net.*;
import java.util.*;

public class FtpServer
{

	private int counter;                //��¼��ǰ������������û�����
	public static String initDir;
	public static ArrayList users = new ArrayList();      //��ǰ���û�����
	public static ArrayList usersInfo = new ArrayList();  //�û���ϸ��Ϣ����
	
	public FtpServer()
	{
		// ����һ���̣߳�ר�����ڽ��շ������˵�ָ�
		FtpConsole fc = new FtpConsole();
		fc.start();

		// �ӱ����ļ������������û���Ϣ�� UserInfo �����У�
		loadUsersInfo();

		// ��ʾ��ӭ�ʣ����ǵ� Counter �����û���
		int counter = 1;
		int i = 0;
		try
		{

			//����21�Ŷ˿�
			ServerSocket s = new ServerSocket(21);
			for(;;)
			{
				//���ܿͻ�������
				Socket incoming = s.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			    PrintWriter out = new PrintWriter(incoming.getOutputStream(),true);
				out.println("220 Service ready for new user,�ѵ�¼�û�����"+counter);

				//Ϊ�µ�¼���û����������߳�
				FtpHandler h = new FtpHandler(incoming,i);
				h.start();

				// �ڵ�ǰ�û������� users ������һ�
				// ע�⣺users �б�������̶߳��󣬶��� UserInfo ����
				users.add(h);
				counter++;
				i++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	} // FtpServer() end
	


	public void loadUsersInfo()   //�ӱ���user.cfg�ļ��ж�ȡ�û�����Ϣ
	{
		String s = getClass().getResource("user.cfg").toString();
		s = s.substring(6,s.length());
		int p1 = 0;
		int p2 = 0;

		// ���ļ������ļ��е��û���Ϣ���� UserInfo ������
		if(new File(s).exists())
		{
			try
			{
				BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(s)));
				String line;
				String field;

				int i = 0;
				while((line = fin.readLine())!=null)  //��user.cfg���ж����û���Ϣ
				{
					UserInfo tempUserInfo = new UserInfo();
					p1 = 0;
					p2 = 0;
					i = 0;
					while((p2 = line.indexOf("|",p1))!=-1) 
					{
						field = line.substring(p1,p2);
						p2 = p2 +1;
						p1 = p2;
						switch(i)            //��ȡ�ַ����еĲ�ֵͬ�ŵ��½����û���Ϣ��
						{
							case 0:
								tempUserInfo.user = field;    
								//System.out.println(tempUserInfo.user);
								break;
							case 1:
								tempUserInfo.password = field;
								//System.out.println(tempUserInfo.password);
								break;
							case 2:
								tempUserInfo.workDir = field;
								//System.out.println(tempUserInfo.workDir);
								break;
						}
						i++;
					} //while((p2 = line.indexOf("|",p1))!=-1) end
					usersInfo.add(tempUserInfo);
				}//while((line = fin.readLine())!=null) end
				fin.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}//if(new File(s).exists()) end
	}// loadUsersInfo() end
	
	public static void main(String[] args)  
	{
		if(args.length != 0) 
		{
			initDir = args[0];    
		}
		else
		{ 
			initDir = "c:/";	   
		}
		FtpServer ftpServer = new FtpServer();

	} // main end
}

class FtpHandler extends Thread    //Ftp���������������ͻ��������ָ����
{
	Socket csocket;
	Socket dsocket;
	int id;
	String cmd = "";
	String param = "";
	String user;
	String remoteHost = " ";
	int remotePort = 0;
	String dir = FtpServer.initDir;
	String rootdir = "c:/";
	int state = 0;
	String reply;
	PrintWriter out; 
	int type = FtpState.FTYPE_IMAGE;
	String requestfile = "";
	boolean isrest = false;

	int parseInput(String s)  //���������ָ������һ�����������ڵ��þ��巽��ʱ��ʶ��
	{
		int p = 0;
		int i = -1;

		// �ж��Ƿ��в���������У��򽫲���������Param�����У������������cmd������
		p = s.indexOf(" ");
		if(p == -1)
			cmd = s;
		else 
			cmd = s.substring(0,p);

		if(p >= s.length() || p ==-1)
			param = "";
		else
			param = s.substring(p+1,s.length());
		cmd = cmd.toUpperCase();

		//���������cmd�����У�����������Param������
		if(cmd.equals("USER"))
				i = 1;
		if(cmd.equals("PASS"))
				i = 2;
		if(cmd.equals("ACCT"))
				i = 3;
	  	if(cmd.equals("CDUP"))	
				i = 4;
		if(cmd.equals("SMNT"))
				i = 5;
		if(cmd.equals("CWD"))	
				i = 6;
		if(cmd.equals("QUIT"))	// �˳�ϵͳ
				i = 7;
	  	if(cmd.equals("REIN"))
				i = 8;
		if(cmd.equals("PORT"))	
				i = 9;
		if(cmd.equals("PASV"))  //���뱻�����䷽ʽ
				i = 10;
		if(cmd.equals("TYPE"))	// �鿴״̬ģʽ
				i = 11;
	  	if(cmd.equals("STRU"))
				i = 12;
		if(cmd.equals("MODE"))
				i = 13;
		if(cmd.equals("RETR"))	
				i = 14;
		if(cmd.equals("STOR"))	
				i = 15;
	  	if(cmd.equals("STOU"))
				i = 16;
		if(cmd.equals("APPE"))
				i = 17;
		if(cmd.equals("ALLO"))
				i = 18;
		if(cmd.equals("REST"))
				i = 19;
	  	if(cmd.equals("RNFR"))
				i = 20;
		if(cmd.equals("RNTO"))
				i = 21;
		if(cmd.equals("ABOR"))	
				i = 22;
		if(cmd.equals("DELE"))	// ɾ���ļ�
				i = 23;
	  	if(cmd.equals("RMD"))	//ɾ��Ŀ¼
				i = 24;
		if(cmd.equals("XMKD"))	
				i = 25;				
		if(cmd.equals("MKD"))  //����Ŀ¼
				i = 25;
		if(cmd.equals("PWD"))  //��ʾԶ�������ĵ�ǰ����Ŀ¼
				i = 26;
		if(cmd.equals("LIST"))	
				i = 27;
	  	if(cmd.equals("NLST"))
				i = 28;
		if(cmd.equals("SITE"))
				i = 29;
		if(cmd.equals("SYST"))
				i = 30;
		if(cmd.equals("HELP"))  //��ʾftp�ڲ�����cmd�İ�����Ϣ��
				i = 31;
	  	if(cmd.equals("NOOP"))
				i = 32;
		if(cmd.equals("XPWD"))
				i = 33;
	 return i;
	}//parseInput() end

	int validatePath(String s)  
	{
		File f = new File(s);
		if(f.exists() && !f.isDirectory())
		{
			String s1 = s.toLowerCase();
			String s2 = rootdir.toLowerCase();
			if(s1.startsWith(s2))
				return 1;
			else
				return 0;
		}
		f = new File(addTail(dir)+s);
		if(f.exists() && !f.isDirectory())
		{
			String s1 = (addTail(dir)+s).toLowerCase();
			String s2 = rootdir.toLowerCase();
			if(s1.startsWith(s2))
				return 2;
			else 
				return 0;
		}
		return 0;
	}// validatePath() end

	boolean checkPASS(String s) 
	{
		for(int i = 0; i<FtpServer.usersInfo.size();i++)
		{
			if(((UserInfo)FtpServer.usersInfo.get(i)).user.equals(user) && 
				((UserInfo)FtpServer.usersInfo.get(i)).password.equals(s))  //�жϸ��û��Ƿ����
			{
				rootdir = ((UserInfo)FtpServer.usersInfo.get(i)).workDir;
				dir = ((UserInfo)FtpServer.usersInfo.get(i)).workDir;  
				return true;
			}
		}
		return false;
	}// checkPASS() end

	boolean commandUSER()  //��Ӧ�ͻ���Userָ�����л��û�
	{
		if(cmd.equals("USER"))
		{
			reply = "331 User name okay, need password";
			user = param;
		  	state = FtpState.FS_WAIT_PASS;
			return false;
		}
		else
		{
			reply = "501 Syntax error in parameters or arguments";
			return true;
		}

	}//commandUser() end

	boolean commandPASS()  
	{
		if(cmd.equals("PASS"))
		{
			if(checkPASS(param))
			{
				reply = "230 User logged in, proceed";
				state = FtpState.FS_LOGIN;
				System.out.println("Message: user "+user+" Form "+remoteHost+"Login");
				System.out.print("->");
				return false;
			}
			else
			{
				reply = "530 Not logged in";
				return true;
			}
		}
		else
		{
			reply = "501 Syntax error in parameters or arguments";
			return true;
		}

	}//commandPass() end

	void errCMD()
	{
		reply = "500 Syntax error, command unrecognized";
	}	
	
	boolean commandCDUP() 
	{
		dir = FtpServer.initDir;
		File f = new File(dir);
		if(f.getParent()!=null &&(!dir.equals(rootdir)))
		{
			dir = f.getParent();
			reply = "200 Command okay";
		}
		else
		{
			reply = "550 Current directory has no parent";
		}
		
		return false;
	}// commandCDUP() end

	boolean commandCWD() 
	{
		File f = new File(param);
		String s = "";
		String s1 = "";
		if(dir.endsWith("/"))
			s = dir;
		else
			s = dir + "/";
		File f1 = new File(s+param);
		
		if(f.isDirectory() && f.exists())
		{
			if(param.equals("..") || param.equals("..\\"))
			{
				if(dir.compareToIgnoreCase(rootdir)==0)
				{
					reply = "550 The directory does not exists";
					//return false;
				}
				else
				{
					s1 = new File(dir).getParent();
					if(s1!=null)
					{
						dir = s1;
						reply = "250 Requested file action okay, directory change to "+dir;
					}
					else
						reply = "550 The directory does not exists";
				}
			}
			else if(param.equals(".") || param.equals(".\\"))
			{
			}
			else 
			{
				dir = param;
				reply = "250 Requested file action okay, directory change to "+dir;
			}		
		}
		else if(f1.isDirectory() && f1.exists())
		{
			dir = s+param;
			reply = "250 Requested file action okay, directory change to "+dir;
		}
		else
			reply = "501 Syntax error in parameters or arguments";
		
		return false;
	} // commandCDW() end

	boolean commandQUIT() //ʵ��QUITָ��˳�Ftp
	{
		reply = "221 Service closing control connection";
		return true;
	}// commandQuit() end

	boolean commandPORT()  
	{
		int p1 = 0;
		int p2 = 0;
		int[] a = new int[6];
		int i = 0;
		try
		{
			while((p2 = param.indexOf(",",p1))!=-1)
			{
				 a[i] = Integer.parseInt(param.substring(p1,p2));
				 p2 = p2+1;
				 p1 = p2;
				 i++;
			}
			a[i] = Integer.parseInt(param.substring(p1,param.length()));
		}
		catch(NumberFormatException e)
		{
			reply = "501 Syntax error in parameters or arguments";
			return false;
		}
		
		remoteHost = a[0]+"."+a[1]+"."+a[2]+"."+a[3];
		remotePort = a[4] * 256+a[5];
		reply = "200 Command okay";
		return false;
	}//commandPort() end	
	
	boolean commandLIST()  
	{
		try
		{
			dsocket = new Socket(remoteHost,remotePort,InetAddress.getLocalHost(),20);
			PrintWriter dout = new PrintWriter(dsocket.getOutputStream(),true);
			if(param.equals("") || param.equals("LIST"))
			{
				out.println("150 Opening ASCII mode data connection for /bin/ls. ");
				File f = new File(dir);
				String[] dirStructure = f.list();
				String fileType;
				for(int i =0; i<dirStructure.length;i++)
				{
					if(dirStructure[i].indexOf(".")!=-1)
					{
						fileType = "- ";
					}
					else
					{
						fileType = "d ";
					}
					dout.println(fileType+dirStructure[i]);
				}
			} 
			dout.close();
			dsocket.close();
			reply = "226 Transfer complete !";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			reply = "451 Requested action aborted: local error in processing";
			return false;
		}
		
		return false;
	}// commandLIST() end

	boolean commandTYPE() //��Ӧ�ͻ���TYPEָ��,�л��ļ�����ķ�ʽ��������"A"ʱΪASCII���䣬������"I"ʱΪ�����Ʊ��봫��
	{
		if(param.equals("A"))
		{
			type = FtpState.FTYPE_ASCII;
			reply = "200 Command okay Change to ASCII mode";
		}
		else if(param.equals("I"))
		{
			type = FtpState.FTYPE_IMAGE;
			reply = "200 Command okay Change to BINARY mode";
		}
		else
			reply = "504 Command not implemented for that parameter";
			
		return false;
	}//commandTYPE() end

	boolean commandRETR() 
	{
		requestfile = param;
		File f =  new File(requestfile);
  		if(!f.exists())
		{
	  		f = new File(addTail(dir)+param);
			if(!f.exists())
			{
	   			reply = "550 File not found";
	   			return  false;
			}
			requestfile = addTail(dir)+param;
		}
  
  		if(isrest)
		{
     
		}
		else
		{
	 		if(type==FtpState.FTYPE_IMAGE)
			{
				try
				{
					out.println("150 Opening Binary mode data connection for "+ requestfile);
					dsocket = new Socket(remoteHost,remotePort,InetAddress.getLocalHost(),20);
    				BufferedInputStream  fin = new BufferedInputStream(new FileInputStream(requestfile));
	  				PrintStream dout = new PrintStream(dsocket.getOutputStream(),true);
					byte[] buf = new byte[1024];
					int l = 0;
					while((l=fin.read(buf,0,1024))!=-1)
					{
			  			dout.write(buf,0,l);
					}
		 			fin.close();
     				dout.close();
		 			dsocket.close();
		 			reply ="226 Transfer complete !";

				}
				catch(Exception e)
				{
					e.printStackTrace();
					reply = "451 Requested action aborted: local error in processing";
					return false;
				}

			}
			if(type==FtpState.FTYPE_ASCII)
			{
	  			try
				{
					out.println("150 Opening ASCII mode data connection for "+ requestfile);
					dsocket = new Socket(remoteHost,remotePort,InetAddress.getLocalHost(),20);
    				BufferedReader  fin = new BufferedReader(new FileReader(requestfile));
	  				PrintWriter dout = new PrintWriter(dsocket.getOutputStream(),true);
					String s;
					while((s=fin.readLine())!=null)
					{
		   				dout.println(s);
					}
		 			fin.close();
     				dout.close();
		 			dsocket.close();
		 			reply ="226 Transfer complete !";
				}
				catch(Exception e)
				{
					e.printStackTrace();
					reply = "451 Requested action aborted: local error in processing";
					return false;
				}
			}
		}
  		return false;

	}//commandRETR() end

	boolean commandSTOR() 
	{
		if(param.equals(""))
		{
			reply = "501 Syntax error in parameters or arguments";
			return false;
		}
		requestfile = addTail(dir)+param;
		if(type == FtpState.FTYPE_IMAGE)
		{
			try
			{
				out.println("150 Opening Binary mode data connection for "+ requestfile);
				dsocket = new Socket(remoteHost,remotePort,InetAddress.getLocalHost(),20);
				BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(requestfile));
				BufferedInputStream din = new BufferedInputStream(dsocket.getInputStream());
				byte[] buf = new byte[1024];
				int l = 0;
				while((l = din.read(buf,0,1024))!=-1)
				{
					fout.write(buf,0,l);
				}//while()
				din.close();
				fout.close();
				dsocket.close();
				reply = "226 Transfer complete !";
			}
			catch(Exception e)
			{
				e.printStackTrace();
				reply = "451 Requested action aborted: local error in processing";
				return false;
			}
		}
		if(type == FtpState.FTYPE_ASCII)
		{
			try
			{
				out.println("150 Opening ASCII mode data connection for "+ requestfile);
				dsocket = new Socket(remoteHost,remotePort,InetAddress.getLocalHost(),20);
				PrintWriter fout = new PrintWriter(new FileOutputStream(requestfile));
				BufferedReader din = new BufferedReader(new InputStreamReader(dsocket.getInputStream()));
				String line;
				while((line = din.readLine())!=null)
				{
					fout.println(line);					
				}
				din.close();
				fout.close();
				dsocket.close();
				reply = " 226 Transfer complete !";
			}
			catch(Exception e)
			{
				e.printStackTrace();
				reply = "451 Requested action aborted: local error in processing";
				return false;
			}
		}
		return false;
	}//commandSTOR() end
	
	boolean commandPWD() // ��Ӧ�ͻ���pwdָ���ʾ�������ϵĵ�ǰĿ¼
	{  
		reply = "257 " + dir + " is current directory.";
		return false;
	}//commandPWD() end
	
	boolean commandNOOP()  
	{
		reply = "200 OK.";
		return false;
	}//commandNOOP() end
	
	boolean commandABOR() 
	{
		try
		{
			dsocket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			reply = "451 Requested action aborted: local error in processing";
			return false; 
		}
		reply = "421 Service not available, closing control connection";
		return false;
	}//commandABOR() end
	
	boolean commandDELE()  //��ӦDELEɾ���ļ�ָ��
	{
		int i = validatePath(param);
		if(i == 0)
		{
			reply = "550 Request action not taken";
	    	return false;
		}
		if(i == 1)
    	{
	    	File f = new File(param);
			f.delete();
    	}
		if(i == 2)
		{
			File f= new File(addTail(dir)+param);
			f.delete();
		} 
		
		reply = "250 Request file action ok,complete";
		return false;

	}//commandDELE() end

	boolean commandMKD() //��Ӧ�ͻ���MKDָ��,����Ŀ¼
	{
		String s1 = param.toLowerCase();
		String s2 = rootdir.toLowerCase();
		if(s1.startsWith(s2))
		{
			File f = new File(param);
			if(f.exists())
			{
				reply = "550 Request action not taken";
				return false;
			}
			else 
			{
				f.mkdirs();
				reply = "250 Request file action ok,complete";
			}
		}
		else 
		{
			File f = new File(addTail(dir)+param);
			if(f.exists())
			{
				reply = "550 Request action not taken";
				return false;
			}
			else 
			{
				f.mkdirs();
				reply = "250 Request file action ok,complete";
			}
		}
		
		return false;
	}//commandMKD() end

	String addTail(String s)
	{
		if(!s.endsWith("/"))
			s = s + "/";
		return s;
	}
	
	public FtpHandler(Socket s,int i) 
	{
		csocket = s;
		id = i;	
	}
	
	public void run()   //�����ͻ��������ָ��
	{
		String str = "";
		int parseResult;
		
		try
		{
			BufferedReader in = new BufferedReader
								(new InputStreamReader(csocket.getInputStream()));
			out = new PrintWriter
								(csocket.getOutputStream(),true);
			state  = FtpState.FS_WAIT_LOGIN;
			boolean finished = false;
			while(!finished)
			{
				str = in.readLine();
				if(str == null) finished = true;
				else
				{
					parseResult = parseInput(str);
					System.out.println("Command:"+cmd+" Parameter:"+param);
					System.out.print("->");
					switch(state)
					{
						case FtpState.FS_WAIT_LOGIN:
								finished = commandUSER();
								break;
						case FtpState.FS_WAIT_PASS:
								finished = commandPASS();
								break;
						case FtpState.FS_LOGIN:
						{
							switch(parseResult)
							{
								case -1:
									errCMD();
									break;
								case 4:
									finished = commandCDUP();
									break;
								case 6:
									finished = commandCWD();
									break;
								case 7:
									finished = commandQUIT();
									break;
								case 9:
									finished = commandPORT();
									break;
								case 27:
									finished = commandLIST();
									break;
								case 11:
									finished = commandTYPE();
									break;
								case 14:
									finished = commandRETR();
									break;
								case 15:
									finished = commandSTOR();
									break;
								case 26:
								case 33:
									finished = commandPWD();
									break;
								case 32:
									finished = commandNOOP();
									break;
								case 22:
									finished = commandABOR();
									break;
								case 23:
									finished = commandDELE();
									break;
								case 25:
									finished = commandMKD();
									break;

							}// switch(parseResult) end
						}// case FtpState.FS_LOGIN: end
						break;
						

					}// switch(state) end
				} // else
				out.println(reply);
			} //while
			csocket.close();
		} //try
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

class FtpConsole extends Thread  //���ܷ�����ָ�����̣߳����ݴ�����ָ��ʵ�־������Ӧ
{
	BufferedReader cin;
	String conCmd;
	String conParam;
	
	int consoleQUIT() 
	{
		System.exit(0);
		return 0;
	}//consoleQUIT() end
	
	boolean consoleLISTUSER() //ʵ�ַ�������LISTUSERָ�� �г������û���Ϣ
	{
		System.out.println("username \t\t workdirectory");
		for(int i = 0 ; i<FtpServer.usersInfo.size();i++)
		{
			System.out.println(((UserInfo)FtpServer.usersInfo.get(i)).user+" \t\t\t "+((UserInfo)FtpServer.usersInfo.get(i)).workDir);
		}
		return false;
	}//consoleLISTUSER() end
	
	boolean consoleLIST()  //ʵ�ַ�������Listָ�� �г��Ѿ����ӵ��û�����IP
	{
		int i = 0;
  		for(i=0;i<FtpServer.users.size();i++)
		{
			System.out.println((i+1)+":"+((FtpHandler)(FtpServer.users.get(i))).user + " From " +((FtpHandler)(FtpServer.users.get(i))).csocket.getInetAddress().toString());
		}

  	    return false;
	}//consoleLIST() end
	
	boolean validateUserName(String s) //�ж��û��Ƿ��Ѿ�����
	{
		for(int i = 0 ; i<FtpServer.usersInfo.size();i++)
		{
			if(((UserInfo)FtpServer.usersInfo.get(i)).user.equals(s))
				return false;	
		}
		return true;
	}//validateUserName() end

	boolean consoleADDUSER()  //������������һ�û�
	{
		System.out.print("please enter username:");
		try
		{
			cin = new BufferedReader(new InputStreamReader(System.in));
			UserInfo tempUserInfo = new UserInfo();
			String line = cin.readLine();
			if(line != "")
			{
				if(!validateUserName(line))     //�ж��û��Ƿ����
				{
					System.out.println("user "+line+" already exists!");
					return false;               //���û��Ѿ�����ʱ�����û����û��Ѿ����ڣ�ע��ʧ��	                              
				}
			}
			else
			{
				System.out.println("username cannot be null!");
				return false;
			}
			tempUserInfo.user = line;
			System.out.print("enter password :");
			line = cin.readLine();
			if(line != "")
				tempUserInfo.password = line;  
			else
			{
				System.out.println("password cannot be null!");
				return false;
			}
			System.out.print("enter the initial directory: ");
			line = cin.readLine();
			if(line != "")
			{
				File f = new File(line);
				if(!f.exists())
					f.mkdirs();
				tempUserInfo.workDir = line;
			}
			else
			{
				System.out.println("the directory cannot be null!");
				return false;
			}
			FtpServer.usersInfo.add(tempUserInfo);
			saveUserInfo();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return false;
	}//consoleADDUSER() end
	
	void saveUserInfo()  //�ڴ������û�ʱ���û���Ϣ���浽user.cfg�ļ���
	{
		String s = "";
		try
		{
			BufferedWriter fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("user.cfg")));
			for(int i = 0; i < FtpServer.usersInfo.size();i++)
			{
				s = ((UserInfo)FtpServer.usersInfo.get(i)).user+"|"+((UserInfo)FtpServer.usersInfo.get(i)).password+"|"+((UserInfo)FtpServer.usersInfo.get(i)).workDir+"|";
				fout.write(s);    //��username,password,�Ͳ����ļ���ƴ���ַ������м���"|"����
				fout.newLine();
			}
			fout.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}//saveUserInfo() end
	
	boolean consoleDELUSER()   //�ڷ�������ɾ��һ�û���Ϣ
	{
		String s = "";
		//System.out.println(conParam);
		if(conParam.equals(""))
		{
			System.out.println("usage:deluser username");
			return false;      //��û�����û���������£���ʾ�û�����
		}
		for(int i=0;i<FtpServer.usersInfo.size();i++)
		{
			s = ((UserInfo)FtpServer.usersInfo.get(i)).user;
			if(s.equals(conParam))
			{
				System.out.println("User "+conParam+" deleted");
                FtpServer.usersInfo.remove(i);
				saveUserInfo();
				return false;
			}
		}
		System.out.println("User "+conParam+" not exists");					
		return false;

	}//consoleDELUSER() end
	
	boolean consoleHELP()   //ʵ�ַ�������Helpָ��г�����ָ��
	{
		if(conParam.equals(""))
		{
			System.out.println("adduser :add new user");
			System.out.println("deluser <username> :delete a user");
			System.out.println("quit  :quit");
			System.out.println("list  :list all user connect to server");
			System.out.println("listuser : list all account of this server");
			System.out.println("help :show  this help");
		}
		else if(conParam.equals("adduser"))
			System.out.println("adduser :add new user");
		else if(conParam.equals("deluser"))
			System.out.println("deluser <username> :delete a user");
		else if(conParam.equals("quit"))
			System.out.println("quit  :quit");
		else if(conParam.equals("list"))
			System.out.println("list  :list all user connect to server");
		else if(conParam.equals("listuser"))
			System.out.println("listuser : list all account of this server");
		else if(conParam.equals("help"))
			System.out.println("help :show  this help");
		else
			return false;
		return false;
		
	}//consoleHELP() end
	
	boolean consoleERR()    //����������ʱ��ʾ"bad command!"
	{
		System.out.println("bad command!");
		return false;
	}//consoleERR() end

	public FtpConsole()     //������������ʱ��ʾ"ftp server started!"
	{
		System.out.println("ftp server started!");
		cin = new BufferedReader(new InputStreamReader(System.in));
	}
	public void run()   //�����߳� �����������˵�ָ��
	{
		boolean ok = false;
		String input = "";
		while(!ok)
		{
			System.out.print("->");
			try
			{
				input = cin.readLine(); 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			switch(parseInput(input))  //������ָ��ز�ͬ����ֵʱִ�в�ͬ�ķ���
			{
				case 1:
					consoleQUIT();   
					break;
				case 8:
					ok = consoleLISTUSER();
					break;
				case 0:
					ok = consoleLIST();
					break;
				case 2:
					ok = consoleADDUSER();
					break;
				case 3:
				 	ok = consoleDELUSER();
				 	break;
				 case 7:
				 	ok = consoleHELP();
				 	break;
				 case -1:
				 	ok = consoleERR();
				 	break;
			}
		}//while end
	}// run() end

	int parseInput(String s)    //�����벻ͬ��ָ��ʱ���ز�ͬ������ֵ�����ڵ��þ��巽��ʱ��ʶ��
	{
		String upperCmd;
		int p = 0;
		conCmd = "";
		conParam = "";
		p = s.indexOf(" ");
		if(p == -1)             
			conCmd = s;
		else 
			conCmd = s.substring(0,p);  //�����������Ϊ��ʱ�Ѳ�����������conCmd

		if(p >= s.length() || p ==-1)
			conParam = "";
		else
			conParam = s.substring(p+1,s.length());
		upperCmd = conCmd.toUpperCase();

        //���ݲ�ͬ������������ز�ͬ����ֵ�����ݲ�ͬ����ֵ�ٵ�����ӳ�ķ���

		if(upperCmd.equals("LIST"))
			return 0;
		else if(upperCmd.equals("QUIT")||upperCmd.equals("EXIT"))
			return 1;
		else if(upperCmd.equals("ADDUSER"))
			return 2;
		else if(upperCmd.equals("DELUSER"))
			return 3;
		else if(upperCmd.equals("EDITUSER"))
			return 4;
		else if(upperCmd.equals("ADDDIR"))
			return 5;
		else if(upperCmd.equals("REMOVEDIR"))
			return 6;
		else if(upperCmd.equals("HELP") ||upperCmd.equals("?"))
			return 7;
		else if(upperCmd.equals("LISTUSER"))
			return 8;						
		return -1;
	}// parseInput end
}

class FtpState
{
	final static int FS_WAIT_LOGIN = 0;
	final static int FS_WAIT_PASS = 1;
	final static int FS_LOGIN = 2;
	final static int FTYPE_ASCII = 0;
	final static int FTYPE_IMAGE  = 1;
	final static int FMODE_STREAM = 0;
	final static int FMODE_COMPRESSED = 1;
	final static int FSTRU_FILE = 0;
	final static int FSTRU_PAGE = 1;
}

class UserInfo
{
	String user;
	String password;
	String workDir;
}
