package message;

import java.nio.ByteBuffer;

public class Response {
	private ByteBuffer responseMsg;
	public Response(ByteBuffer responseMsg){
		this.responseMsg = responseMsg.asReadOnlyBuffer();
	}
	
	public ByteBuffer getResponseMsg(){
		return responseMsg;
	}
}
