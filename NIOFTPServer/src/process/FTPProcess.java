package process;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import globalInfo.SelectionStatus;
import message.FtpSession;
import message.ParseFTPPackage;
import message.Request;
import message.Response;
import selector.ProcessSelector;
import serviceMode.TestMode;
import util.CharsetFactory;

public class FTPProcess implements Runnable{
	private final ProcessSelector processSelector;
	
	//线程池
	private final ExecutorService threadPool;
	private final static int MAX_Thread = 5;
	//状态锁
	private final Object statusLock;
	
	//是否已经关闭
	private volatile boolean isClosed;
	
	//将每个ftp请求封装为一个 work，交由线程池处理
	class Work implements Runnable{
		private SocketChannel socketChannel;
		private FtpSession session;
		
		private final SelectionStatus readyStatus;
		protected Work(SocketChannel socketChannel,FtpSession session,SelectionStatus readyStatus){
			this.socketChannel = socketChannel;
			this.session = session;
			this.readyStatus = readyStatus;
			/*ByteBuffer bb = ByteBuffer.allocate(1024);
			int length=0;
			try {
				length = socketChannel.read(bb);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(length>0){
				Charset cs = CharsetFactory.getCharset();
				String message = cs.decode(bb).toString();
				System.out.println("message: "+message);
			}*/
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			if(readyStatus == SelectionStatus.READ)
			{
				int length;
				try {
					length = this.socketChannel.read(buffer);
					if(length>0)
					{
						buffer.flip();
						Request request = ParseFTPPackage.parseFtppackege(buffer);
						TestMode.processCommand(socketChannel, session, request);
					}
					buffer.clear();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						socketChannel.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					session.close();
				}
				
			}
			else if(readyStatus == SelectionStatus.WRITE)
			{
				
			}
		}
	}
	
	public FTPProcess() throws IOException
	{
		processSelector = new ProcessSelector();
		threadPool = Executors.newFixedThreadPool(MAX_Thread);
		statusLock = new Object();
		isClosed = false;
	}
	
	public void start(){
		Thread thread = new Thread(this);
		thread.start();
		System.out.println("process start");
	}
	
	public void stop(){
		if(!isClosed) return;
 		synchronized(statusLock){
			isClosed = false;
			try {
				processSelector.stop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
 		if(!threadPool.isShutdown()){
 			threadPool.shutdown();
 		}
	}
	
	public boolean register(SocketChannel socketChannel,int ops)
	{
		if(socketChannel == null) return false;
		try {
			if(socketChannel.isBlocking()) socketChannel.configureBlocking(false);
			String response = "220 Service ready for new user";
			Charset cs  = CharsetFactory.getCharset("ASCII");
			ByteBuffer bb = cs.encode(response);
			socketChannel.write(bb);
			bb.clear();
			System.out.println("send response");
			processSelector.register(socketChannel, ops);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void process(Iterator<SelectionKey> it)
	{
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		Charset cs  = CharsetFactory.getCharset("ASCII");
		while(it.hasNext())
		{
			SelectionKey key = it.next();
			//SocketChannel channel = (SocketChannel) key.channel();
			FtpSession session = (FtpSession) key.attachment();
			//Work word = new Work(channel,context);
			//threadPool.execute(word);
			if(key.isReadable()){
				buffer.flip();
				SocketChannel channel = (SocketChannel) key.channel();
				try {
					channel.read(buffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				buffer.flip();
				CharBuffer cb = cs.decode(buffer);
				System.out.println("get message from client " + cb.toString());
				String response = "331 Service ready for new user";
				ByteBuffer bb = cs.encode(response);
				session.setResponse(new Response(bb));
			}
			else if(key.isWritable())
			{
				
				SocketChannel channel = (SocketChannel) key.channel();
				String response = "331 Service ready for new user";
				ByteBuffer bb = cs.encode(response);
				try {
					channel.write(bb);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(channel!=null)
					{
						try {
							channel.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					//e.printStackTrace();
				}
				bb.clear();
			}
			it.remove();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isClosed)
		{
			Iterator<SelectionKey> it=null;
			synchronized(statusLock){
				if(!isClosed)
				{
					it = processSelector.select();
				}
			}
		//	System.out.println("process "+processSelector.getSize());
			if(it != null)
			{
				process(it);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
