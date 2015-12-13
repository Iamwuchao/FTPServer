package util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;


public class NioSocketClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			SocketChannel socketChannel=null;
			ByteBuffer buf = ByteBuffer.allocate(1024*1024);//1M
			boolean isconnected=false;
			try {
				socketChannel = SocketChannel.open();
				isconnected = socketChannel.connect(new InetSocketAddress("192.168.9.119",8081));
				System.out.println("connecting");
				while(!isconnected&&!socketChannel.finishConnect())
				{
					System.out.println("waiting");
				}
				if(isconnected)
					System.out.println("connected");
				else
					System.out.println("fail");
				
				File file = new File("/mnt/hgfs/share");
				
				if(!file.exists())
				{
					throw new FileNotFoundException();
				}
				FileInputStream fis = new FileInputStream(file);
				FileChannel fc = fis.getChannel();
				while(buf.hasRemaining())
				{
					socketChannel.write(buf);
				}
				System.out.println("finish");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(socketChannel!=null)
					try {
						socketChannel.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
	}

}
