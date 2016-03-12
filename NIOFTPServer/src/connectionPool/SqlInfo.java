package connectionPool;

public class SqlInfo {
	private static String url;
	private static String userName;
	private static String passWord;
	private static int maxSize=10;
	private static int addCount=1;
	private static long timeOut;

	public static long getTimeOut() {
		return timeOut;
	}

	public static String getUrl() {
		return url;
	}
	
	public static String getUserName() {
		return userName;
	}
	
	public static String getPassWord() {
		return passWord;
	}
	
	public static int getMaxSize() {
		return maxSize;
	}

	public static int getAddCount() {
		return addCount;
	}
}
