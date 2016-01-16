package message;

import java.nio.ByteBuffer;

public class ParseFTPPackage {
	public static Request parseFtppackege(ByteBuffer buffer)
	{
		String command="";
		String requestSource = "";
		Request request = new Request(command,requestSource);
		return request;
	}
}
