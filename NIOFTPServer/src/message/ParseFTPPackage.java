package message;

public class ParseFTPPackage {
	public static Request parseFtppackege( String requestContent)
	{
		String command="";
		String requestSource = "";
		Request request = new Request(command,requestSource);
		return request;
	}
}
