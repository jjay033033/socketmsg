/**
 * 
 */
package top.lmoon.constants;

/**
 * @author guozy
 * @date 2017-5-11
 * 
 */
public class SystemConstants {

	public static final String IP_DEFAULT = "127.0.0.1";

	public static final int PORT_DEFAULT = 11111;

	public static final String LINE_BREAK = "\r\n";

	public static class Symbol {

		public static final String SPLIT = "_&&_";

		public static final String MSG = "msg";

		public static final String ADD = "cmdAdd";

		public static final String RED = "cmdRed";

		public static final String ONLINE = "online";

		public static final String OFFLINE = "offline";

		public static final String SEND = "send";

		public static final String NOTICE = "notice";

	}

	public static class MsgType {

		public static final int SYSTEM = 1;

		public static final int USER = 2;

	}

	public static class MsgUserMode {

		public static final int SEND_ONE = 1;
		
		public static final int SEND_ALL = 2;

	}

	public static class MsgSysMode {

		public static final int ONLINE = 1;

		public static final int OFFLINE = 2;

		public static final int USERS_SET = 3;

		public static final int NOTICE = 4;
		
		public static final int SERVER_OFF = 5;
		
		public static final int SERVER_ERROR = 6;

//		public static final int RED = 7;

	}
	
	public static class Error{
		public static final String USERNAME = "用户名非法或已存在！";
	}

}
