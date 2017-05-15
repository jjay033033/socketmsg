package top.lmoon.client;

import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import top.lmoon.constants.SystemConstants;
import top.lmoon.util.MessageUtil;
import top.lmoon.util.SwingUtil;
import top.lmoon.vo.Message;

public class ClientThread extends Thread {

	private static final Logger logger = Logger.getLogger(ClientThread.class);

	@Override
	public void run() {
		try {
			Client client = Client.getInstance();
			Scanner sc = new Scanner(Client.clientSocket.getInputStream());
			while (sc.hasNextLine()) {
				String str = sc.nextLine();
				System.out.println(Client.userName + ": " + str);
				Message m = MessageUtil.toMessage(str);
				String msgStr = "";
				if (m.getType() == SystemConstants.MsgType.SYSTEM) {
					switch (m.getMode()) {
					case SystemConstants.MsgSysMode.NOTICE:
						msgStr = "【通知 】:" + m.getContent();
						break;
					case SystemConstants.MsgSysMode.ONLINE:
						if (!client.lm.contains(m.getFromUser())) {
							client.lm.addElement(m.getFromUser());// 用户上线--添加
						}
						msgStr = "【通知 】:用户【" + m.getFromUser() + "】上线了！";
						break;
					case SystemConstants.MsgSysMode.OFFLINE:
						client.lm.removeElement(m.getFromUser());// 用户离线了--移除
						msgStr = "【通知 】:用户【" + m.getFromUser() + "】下线了！";
						break;
					case SystemConstants.MsgSysMode.USERS_SET:
						client.lm.addElement("全部");//
						client.list.setSelectedIndex(0);// 设置默认显示
						SwingUtil.addStr(client.lm, m.getUserList(), Client.userName);
						break;
					case SystemConstants.MsgSysMode.SERVER_OFF:
						client.setOffLineState();
						msgStr = SystemConstants.SysMsg.SERVER_OFF;
						break;
					case SystemConstants.MsgSysMode.SERVER_ERROR:
						client.setOffLineState();
						msgStr = "【错误 】:" + m.getContent();
						break;
					default:
						break;
					}

				} else if (m.getType() == SystemConstants.MsgType.USER) {
					switch (m.getMode()) {
					case SystemConstants.MsgUserMode.SEND_ONE:
						msgStr = "【" + m.getFromUser() + "】对我说: " + m.getContent();
						break;
					case SystemConstants.MsgUserMode.SEND_ALL:
						msgStr = "【" + m.getFromUser() + "】说: " + m.getContent();
						break;
					default:
						break;
					}
				}
				SwingUtil.printInTextArea(client.allMsg, msgStr );
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("", e);
		}
	}
}
