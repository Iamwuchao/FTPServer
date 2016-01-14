package util;

import java.nio.charset.Charset;

public class CharsetFactory {
	private static final String DefaultCharSet = "utf-8";
	
	public static Charset getCharset(String charSet)
	{
		Charset charset = Charset.forName(charSet);
		return charset;
	}
	
	public static Charset getCharset()
	{
		Charset charset = Charset.forName(DefaultCharSet);
		return charset;
	}
}
