package top.lmoon.util;

import java.net.Socket;
import java.util.Map;

public class ValidateUtil {
	
	private static final String REGEX_USERNAME = "^[\u4e00-\u9fa5_a-zA-Z0-9]+$";
	
	public static boolean isUserNameForClient(String userName){
		if (userName == null
				|| userName.trim().isEmpty()
				|| !userName.matches(REGEX_USERNAME)
				|| userName.equals("全部")) {
			return false;
		}
		return true;
	}
	
	public static boolean isUserNameForServer(String userName,Map<String, Socket> usersMap){
		return isUserNameForClient(userName) && !usersMap.containsKey(userName);
	}

}
