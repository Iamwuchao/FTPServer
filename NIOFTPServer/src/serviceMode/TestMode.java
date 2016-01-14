package serviceMode;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import message.FtpSession;
import message.Request;

public class TestMode {
	
	 public static void processCommand(SocketChannel socketChannel,FtpSession context,Request request)
	 {
		 ByteBuffer buf = ByteBuffer.allocate(1024);
		 String str = "SUCCESS";
		 buf.put(str.getBytes());
		 try {
			socketChannel.write(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
