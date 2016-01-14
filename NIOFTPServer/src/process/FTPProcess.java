package process;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import message.FtpSession;
import message.ParseFTPPackage;
import message.Request;
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
		private FtpSession context;
		private Request request;
		protected Work(SocketChannel socketChannel,FtpSession context){
			this.socketChannel = socketChannel;
			this.context = context;
			ByteBuffer bb = ByteBuffer.allocate(1024);
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
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
				String requestContent = readMessage();
				request = ParseFTPPackage.parseFtppackege(requestContent);
				TestMode.processCommand(socketChannel, context, request);
		}
		
		private String readMessage()
		{
			String message = "";
			return message;
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
		while(it.hasNext())
		{
			SelectionKey key = it.next();
			SocketChannel channel = (SocketChannel) key.channel();
			FtpSession context = (FtpSession) key.attachment();
			Work word = new Work(channel,context);
			threadPool.execute(word);
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
			System.out.println("process "+processSelector.getSize());
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
