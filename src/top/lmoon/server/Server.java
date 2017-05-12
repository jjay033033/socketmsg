/**
 * 
 */
package top.lmoon.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import top.lmoon.constants.SystemConstants;
import top.lmoon.util.MessageUtil;
import top.lmoon.util.SocketUtil;
import top.lmoon.util.ValidateUtil;
import top.lmoon.vo.Message;

/**
 * @author guozy
 * @date 2017-5-11
 * 
 */
public class Server extends JFrame {

	private static final long serialVersionUID = -3257413069018446440L;

	private static final Logger logger = Logger.getLogger(Server.class);

	private JList<String> list;
	private JTextArea area;
	private DefaultListModel<String> lm;

	// 用来保存所有在线用户的名字和Socket----池
	private ConcurrentMap<String, Socket> usersMap = new ConcurrentHashMap<String, Socket>();

	public Server() {
		JPanel p = new JPanel(new BorderLayout());
		// 最右边的用户在线列表
		lm = new DefaultListModel<String>();
		list = new JList<String>(lm);
		JScrollPane js = new JScrollPane(list);
		Border border = new TitledBorder("在线");
		js.setBorder(border);
		Dimension d = new Dimension(100, p.getHeight());
		js.setPreferredSize(d);// 设置位置
		p.add(js, BorderLayout.EAST);

		// 通知文本区域
		area = new JTextArea();
		// area.setEnabled(false);//不能选中和修改
		area.setEditable(false);
		p.add(new JScrollPane(area), BorderLayout.CENTER);
		this.getContentPane().add(p);

		// 添加菜单项
		JMenuBar bar = new JMenuBar();// 菜单条
		this.setJMenuBar(bar);
		JMenu jm = new JMenu("控制(C)");
		jm.setMnemonic('C');// 设置助记符---Alt+'C'，显示出来，但不运行
		bar.add(jm);
		final JMenuItem jmi1 = new JMenuItem("开启");
		jmi1.setAccelerator(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_MASK));// 设置快捷键Ctrl+'R'
		jmi1.setActionCommand("run");
		jm.add(jmi1);

		JMenuItem jmi2 = new JMenuItem("退出");
		jmi2.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_MASK));// 设置快捷键Ctrl+'R'
		jmi2.setActionCommand("exit");
		jm.add(jmi2);

		// 监听
		ActionListener a1 = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("run")) {
					startServer();
					jmi1.setEnabled(false);// 内部方法~访问的只能是final对象
				} else if (e.getActionCommand().equals("exit")) {
					exitSystem();
				}
			}
		};

		jmi1.addActionListener(a1);
		jmi2.addActionListener(a1);

		// *************************************************
		// 右上角的X-关闭按钮-添加事件处理
		addWindowListener(new WindowAdapter() {
			// 适配器
			@Override
			public void windowClosing(WindowEvent e) {
				exitSystem();
			}
		});

		Toolkit tk = Toolkit.getDefaultToolkit();
		int width = (int) tk.getScreenSize().getWidth();
		int height = (int) tk.getScreenSize().getHeight();
		this.setBounds(width / 4, height / 4, width / 2, height / 2);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);// 关闭按钮器作用

		setVisible(true);
	}

	private void exitSystem() {
		sendServerOffMsg();
		System.exit(0);
	}

	private void sendServerOffMsg() {
		if (!usersMap.isEmpty()) {
			Message m = new Message(SystemConstants.MsgType.SYSTEM, SystemConstants.MsgSysMode.SERVER_OFF);
			// for (Iterator<Entry<String, Socket>> it =
			// usersMap.entrySet().iterator(); it.hasNext();) {
			// Entry<String, Socket> e = it.next();
			// }
			for (Socket s : usersMap.values()) {
				SocketUtil.printWithoutException(s, m);
			}
		}
	}

	protected void startServer() {
		try {
			ServerSocket server = new ServerSocket(SystemConstants.PORT_DEFAULT);
			area.append("启动服务：" + server + SystemConstants.LINE_BREAK);
			new ServerThread(server).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class ServerThread extends Thread {
		private ServerSocket server;

		public ServerThread(ServerSocket server) {
			this.server = server;
		}

		@Override
		public void run() {
			try {// 和客户端握手
				while (true) {
					Socket socketClient = server.accept();
					Scanner sc = new Scanner(socketClient.getInputStream());
					if (sc.hasNext()) {
						String str = sc.nextLine();
						Message m = MessageUtil.toMessage(str);
						if (m.getType() == SystemConstants.MsgType.SYSTEM
								&& m.getMode() == SystemConstants.MsgSysMode.ONLINE) {
							String userName = m.getFromUser();
							if(!ValidateUtil.isUserNameForServer(userName, usersMap)){
								Message errorM = new Message(SystemConstants.MsgType.SYSTEM, SystemConstants.MsgSysMode.SERVER_ERROR, SystemConstants.Error.USERNAME, "");
								SocketUtil.printWithoutException(socketClient, errorM);
								continue;
							}
							area.append("用户[ " + userName + " ]登录 " + socketClient + SystemConstants.LINE_BREAK);// 在客户端通知
							lm.addElement(userName);// 添加到用户在线列表

							new ClientThread(socketClient).start();// 专门为这个客户端服务

							usersMap.put(userName, socketClient);// 把当前登录的用户加到“在线用户”池中

							msgAll(str);// 把“当前用户登录的消息即用户名”通知给所有其他已经在线的人
							msgSelf(socketClient);// 通知当前登录的用户，有关其他在线人的信息
						}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	class ClientThread extends Thread {
		private Socket socketClient;

		public ClientThread(Socket socketClient) {
			this.socketClient = socketClient;
		}

		@Override
		public void run() {
			System.out.println("一个与客户端通讯的线程启动并开始通讯...");
			try {
				Scanner sc = new Scanner(socketClient.getInputStream());
				while (sc.hasNext()) {
					String str = sc.nextLine();
					System.out.println(str);
					Message m = MessageUtil.toMessage(str);

					if (m.getType() == SystemConstants.MsgType.USER) {
						if (m.getMode() == SystemConstants.MsgUserMode.SEND_ONE) {
							sendMsgToSb(str,m);
						} else if (m.getMode() == SystemConstants.MsgUserMode.SEND_ALL) {
							sendMsgToAll(str);
						}

					} else if (m.getType() == SystemConstants.MsgType.SYSTEM
							&& m.getMode() == SystemConstants.MsgSysMode.OFFLINE) {
						// 服务器显示
						area.append("用户[ " + m.getFromUser() + " ]已退出!" + usersMap.get(m.getFromUser())
								+ SystemConstants.LINE_BREAK);

						// 从在线用户池中把该用户删除
						usersMap.remove(m.getFromUser());

						// 服务器的在线列表中把该用户删除
						lm.removeElement(m.getFromUser());

						// 通知其他用户，该用户已经退出
						sendExitMsgToAll(str);
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
				logger.error("",e);
			}

		}

	}

	// 通知其他用户。该用户已经退出
	private void sendExitMsgToAll(String str) throws IOException {
		Iterator<String> userNames = usersMap.keySet().iterator();

		while (userNames.hasNext()) {
			String userName = userNames.next();
			Socket s = usersMap.get(userName);
			SocketUtil.print(s, str);
			// PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			// String str = SystemConstants.Symbol.MSG +
			// SystemConstants.Symbol.SPLIT + SystemConstants.Symbol.NOTICE
			// + SystemConstants.Symbol.SPLIT + "用户[ " + msgs[3] + " ]已退出！";
			// pw.println(str);
			// pw.flush();
			//
			// str = SystemConstants.Symbol.RED + SystemConstants.Symbol.SPLIT +
			// SystemConstants.Symbol.NOTICE
			// + SystemConstants.Symbol.SPLIT + "" + msgs[3];
			// pw.println(str);
			// pw.flush();
		}

	}

	public void sendMsgToAll(String str) {
		Iterator<String> userNames = usersMap.keySet().iterator();
		// 遍历每一个在线用户，把聊天消息发给他
		while (userNames.hasNext()) {
			String userName = userNames.next();
			Socket s = usersMap.get(userName);
			SocketUtil.printWithoutException(s, str);
		}
	}

	// 服务器把客户端的聊天消息转发给相应的其他客户端
	public void sendMsgToSb(String str,Message m){
		Socket s = usersMap.get(m.getToUser());
		try {
			SocketUtil.print(s, str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			area.append("发送消息失败:From【"+m.getFromUser()+"】To【"+m.getToUser()+"】.原因："+e.getMessage());
			e.printStackTrace();
			logger.error("",e);
		}
	}

	/**
	 * 把“当前用户登录的消息即用户名”通知给所有其他已经在线的人
	 * 
	 * @param userName
	 */
	// 技术思路:从池中依次把每个socket(代表每个在线用户)取出，向它发送userName
	public void msgAll(String str) {
		Iterator<Socket> it = usersMap.values().iterator();
		while (it.hasNext()) {
			Socket s = it.next();
			SocketUtil.printWithoutException(s, str);
		}
	}

	/**
	 * 通知当前登录的用户，有关其他在线人的信息
	 * 
	 * @param socketClient
	 */
	// 把原先已经在线的那些用户的名字发给该登录用户，让他给自己界面中的lm添加相应的用户名
	public void msgSelf(Socket s) {
		List<String> userList = new ArrayList<String>(usersMap.keySet());
		Message m = new Message(SystemConstants.MsgType.SYSTEM, SystemConstants.MsgSysMode.USERS_SET, userList);
		SocketUtil.printWithoutException(s, m);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰
		new Server();
	}

}
