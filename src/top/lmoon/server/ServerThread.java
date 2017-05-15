package top.lmoon.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;

import top.lmoon.constants.SystemConstants;
import top.lmoon.util.MessageUtil;
import top.lmoon.util.SocketUtil;
import top.lmoon.util.SwingUtil;
import top.lmoon.util.ValidateUtil;
import top.lmoon.vo.Message;

public class ServerThread extends Thread {
	private ServerSocket serverSocket;

	public ServerThread(ServerSocket server) {
		this.serverSocket = server;
	}

	@Override
	public void run() {
		try {// 和客户端握手
			Server server = Server.getInstance();
			while (true) {
				Socket socketClient = serverSocket.accept();
				Scanner sc = new Scanner(socketClient.getInputStream());
				if (sc.hasNext()) {
					String str = sc.nextLine();
					Message m = MessageUtil.toMessage(str);
					if (m.getType() == SystemConstants.MsgType.SYSTEM
							&& m.getMode() == SystemConstants.MsgSysMode.ONLINE) {
						String userName = m.getFromUser();
						if (!ValidateUtil.isUserNameForServer(userName, server.usersMap)) {
							Message errorM = new Message(SystemConstants.MsgType.SYSTEM,
									SystemConstants.MsgSysMode.SERVER_ERROR, SystemConstants.Error.USERNAME, "");
							SocketUtil.printWithoutException(socketClient, errorM);
							continue;
						}
						SwingUtil.printInTextArea(server.area,"用户[ " + userName + " ]登录 " + socketClient );// 在客户端通知
						server.lm.addElement(userName);// 添加到用户在线列表

						new ClientThread(socketClient).start();// 专门为这个客户端服务

						server.usersMap.put(userName, socketClient);// 把当前登录的用户加到“在线用户”池中

						msgAll(str, socketClient,server.usersMap);// 把“当前用户登录的消息即用户名”通知给所有其他已经在线的人
						msgSelf(socketClient,server.usersMap);// 通知当前登录的用户，有关其他在线人的信息
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 把“当前用户登录的消息即用户名”通知给所有其他已经在线的人
	 * 
	 * @param userName
	 */
	// 技术思路:从池中依次把每个socket(代表每个在线用户)取出，向它发送userName
	private void msgAll(String str, Socket socket,ConcurrentMap<String, Socket> usersMap) {
		Iterator<Socket> it = usersMap.values().iterator();
		while (it.hasNext()) {
			Socket s = it.next();
			if (s != socket)
				SocketUtil.printWithoutException(s, str);
		}
	}

	/**
	 * 通知当前登录的用户，有关其他在线人的信息
	 * 
	 * @param socketClient
	 */
	// 把原先已经在线的那些用户的名字发给该登录用户，让他给自己界面中的lm添加相应的用户名
	private void msgSelf(Socket s,ConcurrentMap<String, Socket> usersMap) {
		List<String> userList = new ArrayList<String>(usersMap.keySet());
		Message m = new Message(SystemConstants.MsgType.SYSTEM, SystemConstants.MsgSysMode.USERS_SET, userList);
		SocketUtil.printWithoutException(s, m);
	}
}
