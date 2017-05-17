package top.lmoon.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import top.lmoon.constants.ResConstants;
import top.lmoon.vo.Message;

public class SocketUtil {

	private static final Logger logger = Logger.getLogger(ResConstants.LOG_COMMON);

	public static void print(Socket s, String str) throws IOException {
		PrintWriter pw = new PrintWriter(s.getOutputStream(), false);// 加true为自动刷新
		print(pw, str);
	}

	public static void print(Socket s, Message m) throws IOException {
		print(s, MessageUtil.toStr(m));
	}

	public static void printWithoutException(Socket s, String str) {
		try {
			print(s, str);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("socket print 出错", e);
		}
	}

	public static void printWithoutException(Socket s, Message m) {
		printWithoutException(s, MessageUtil.toStr(m));
	}

	private static void print(PrintWriter pw, String str) {
		logger.debug(str);//调试打印信息
		pw.println(str);
		pw.flush();
	}

	// public static void print(PrintWriter pw, Message m) {
	// print(pw, MessageUtil.toStr(m));
	// }

	public static void closeSocket(Socket s) {
		try {
			if (s != null) {
				s.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("", e);
		}
	}

}
