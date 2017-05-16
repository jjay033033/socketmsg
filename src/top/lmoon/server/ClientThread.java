package top.lmoon.server;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import top.lmoon.constants.SystemConstants;
import top.lmoon.util.MessageUtil;
import top.lmoon.util.SocketUtil;
import top.lmoon.util.SwingUtil;
import top.lmoon.vo.Message;

public class ClientThread extends Thread {

	private static final Logger logger = Logger.getLogger(ClientThread.class);

	private Socket socketClient;

	public ClientThread(Socket socketClient) {
		this.socketClient = socketClient;
	}

	@Override
	public void run() {
		System.out.println("一个与客户端通讯的线程启动并开始通讯...");
		try {
			Server server = Server.getInstance();
			
			DataInputStream dis = new DataInputStream(
					socketClient.getInputStream());
			System.out.println("Server发来的字符串是：" + dis.readUTF());
			FileOutputStream fos = new FileOutputStream("res/copy.png");
			byte[] buffer = new byte[1024];
			int len;
			while (((len = dis.read(buffer))>0))
				fos.write(buffer, 0, len);
			dis.close();
			fos.flush();
			fos.close();
			socketClient.close();
			return;
//			Scanner sc = new Scanner(socketClient.getInputStream());
//			while (sc.hasNext()) {
//				String str = sc.nextLine();
//				System.out.println(str);
//				Message m = MessageUtil.toMessage(str);
//
//				if (m.getType() == SystemConstants.MsgType.USER) {
//					if (m.getMode() == SystemConstants.MsgUserMode.SEND_ONE) {
//						sendMsgToSb(str, m,server.usersMap,server.area);
//					} else if (m.getMode() == SystemConstants.MsgUserMode.SEND_ALL) {
//						sendMsgToAll(str,server.usersMap);
//					}
//
//				} else if (m.getType() == SystemConstants.MsgType.SYSTEM
//						&& m.getMode() == SystemConstants.MsgSysMode.OFFLINE) {
//					// 服务器显示
//					SwingUtil.printInTextArea(server.area,
//							"用户[ " + m.getFromUser() + " ]已退出!" + server.usersMap.get(m.getFromUser()));
//
//					// 从在线用户池中把该用户删除
//					server.usersMap.remove(m.getFromUser());
//
//					// 服务器的在线列表中把该用户删除
//					server.lm.removeElement(m.getFromUser());
//
//					// 通知其他用户，该用户已经退出
//					sendExitMsgToAll(str,server.usersMap);
//				}
//
//			}

		} catch (IOException e) {
			e.printStackTrace();
			logger.error("", e);
		}

	}

	// 通知其他用户。该用户已经退出
	private void sendExitMsgToAll(String str, ConcurrentMap<String, Socket> usersMap) throws IOException {
		Iterator<String> userNames = usersMap.keySet().iterator();

		while (userNames.hasNext()) {
			String userName = userNames.next();
			Socket s = usersMap.get(userName);
			SocketUtil.print(s, str);
		}

	}

	private void sendMsgToAll(String str, ConcurrentMap<String, Socket> usersMap) {
		Iterator<String> userNames = usersMap.keySet().iterator();
		// 遍历每一个在线用户，把聊天消息发给他
		while (userNames.hasNext()) {
			String userName = userNames.next();
			Socket s = usersMap.get(userName);
			SocketUtil.printWithoutException(s, str);
		}
	}

	// 服务器把客户端的聊天消息转发给相应的其他客户端
	private void sendMsgToSb(String str, Message m, ConcurrentMap<String, Socket> usersMap, JTextArea area) {
		Socket s = usersMap.get(m.getToUser());
		try {
			SocketUtil.print(s, str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			SwingUtil.printInTextArea(area,
					"发送消息失败:From【" + m.getFromUser() + "】To【" + m.getToUser() + "】.原因：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
	}

}
