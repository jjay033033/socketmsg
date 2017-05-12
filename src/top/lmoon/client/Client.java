/**
 * 
 */
package top.lmoon.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.print.attribute.standard.MediaSize.ISO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import top.lmoon.constants.SystemConstants;
import top.lmoon.util.MessageUtil;
import top.lmoon.util.SocketUtil;
import top.lmoon.util.StringUtil;
import top.lmoon.util.SwingUtil;
import top.lmoon.util.ValidateUtil;
import top.lmoon.vo.Message;

/**
 * @author guozy
 * @date 2017-5-11
 * 
 */
public class Client extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8859859823038390137L;

	private static final Logger logger = Logger.getLogger(Client.class);

	private JTextField tfdUserName;
	private JList<String> list;
	private DefaultListModel<String> lm;
	private JTextArea allMsg;
	private JTextField tfdMsg;
	private JButton btnCon;
	private JButton btnExit;
	private JButton btnSend;

	private static String HOST = SystemConstants.IP_DEFAULT;// 自己机子，服务器的ip地址
	private static int PORT = SystemConstants.PORT_DEFAULT;// 服务器的端口号
	private Socket clientSocket;
	private static String userName;

	private static boolean isOnline = false;

	public Client() {

		super("即时通讯工具1.0");
		// 菜单条
		addJMenu();

		// 上面的面板
		JPanel p = new JPanel();
		JLabel jlb1 = new JLabel("用户标识:");
		tfdUserName = new JTextField(10);
		// tfdUserName.setEnabled(false);//不能选中和修改
		// dtfdUserName.setEditable(false);//不能修改

		// 链接按钮
		// ImageIcon icon = new ImageIcon("a.png");
		// btnCon = new JButton("", icon);
		btnCon = new JButton("上线");
		btnCon.setActionCommand("c");
		btnCon.addActionListener(this);

		// 退出按钮
		// icon = new ImageIcon("b.jpg");
		// btnExit = new JButton("", icon);
		btnExit = new JButton("下线");
		btnExit.setActionCommand("exit");

		btnExit.addActionListener(this);
		btnExit.setEnabled(false);
		p.add(jlb1);
		p.add(tfdUserName);
		p.add(btnCon);
		p.add(btnExit);
		getContentPane().add(p, BorderLayout.NORTH);

		// 中间的面板
		JPanel cenP = new JPanel(new BorderLayout());
		this.getContentPane().add(cenP, BorderLayout.CENTER);

		// 在线列表
		lm = new DefaultListModel<String>();
		list = new JList<String>(lm);
		// lm.addElement("全部");
		// list.setSelectedIndex(0);// 设置默认显示
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 只能选中一行
		list.setVisibleRowCount(2);
		JScrollPane js = new JScrollPane(list);
		Border border = new TitledBorder("在线");
		js.setBorder(border);
		Dimension preferredSize = new Dimension(70, cenP.getHeight());
		js.setPreferredSize(preferredSize);
		cenP.add(js, BorderLayout.EAST);

		// 聊天消息框
		allMsg = new JTextArea();
		allMsg.setEditable(false);
		cenP.add(new JScrollPane(allMsg), BorderLayout.CENTER);

		// 消息发送面板
		JPanel p3 = new JPanel();
		JLabel jlb2 = new JLabel("消息:");
		p3.add(jlb2);
		tfdMsg = new JTextField(20);
		p3.add(tfdMsg);
		btnSend = new JButton("发送");
		btnSend.setEnabled(false);
		btnSend.setActionCommand("send");
		btnSend.addActionListener(this);
		p3.add(btnSend);
		this.getContentPane().add(p3, BorderLayout.SOUTH);

		// *************************************************
		// 右上角的X-关闭按钮-添加事件处理
		addWindowListener(new WindowAdapter() {
			// 适配器
			@Override
			public void windowClosing(WindowEvent e) {
				if (isOnline)
					sendOffLineMsg();
				System.exit(0);
			}
		});

		setBounds(300, 300, 400, 300);
		setVisible(true);
	}

	private void addJMenu() {
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu menu = new JMenu("选项");
		menuBar.add(menu);

		JMenuItem menuItemSet = new JMenuItem("设置");
		JMenuItem menuItemHelp = new JMenuItem("帮助");
		menu.add(menuItemSet);
		menu.add(menuItemHelp);

		menuItemSet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JDialog dlg = new JDialog(Client.this);// 弹出一个界面
				// 不能直接用this

				dlg.setBounds(Client.this.getX() + 20, Client.this.getY() + 30, 350, 150);
				dlg.setLayout(new FlowLayout());
				dlg.add(new JLabel("服务器IP和端口:"));

				final JTextField tfdHost = new JTextField(10);
				tfdHost.setText(Client.HOST);
				dlg.add(tfdHost);

				dlg.add(new JLabel(":"));

				final JTextField tfdPort = new JTextField(5);
				tfdPort.setText("" + Client.PORT);
				dlg.add(tfdPort);

				JButton btnSet = new JButton("设置");
				dlg.add(btnSet);
				btnSet.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String ip = tfdHost.getText();// 解析并判断ip是否合法
						String strs[] = ip.split("\\.");
						if (strs == null || strs.length != 4) {
							JOptionPane.showMessageDialog(Client.this, "IP类型有误！");
							return;
						}
						try {
							for (int i = 0; i < 4; i++) {
								int num = Integer.parseInt(strs[i]);
								if (num > 255 || num < 0) {
									JOptionPane.showMessageDialog(Client.this, "IP类型有误！");
									return;
								}
							}
						} catch (NumberFormatException e2) {
							JOptionPane.showMessageDialog(Client.this, "IP类型有误！");
							return;
						}

						Client.HOST = ip;// 先解析并判断ip是否合法

						try {
							int port = Integer.parseInt(tfdPort.getText());
							if (port < 0 || port > 65535) {
								JOptionPane.showMessageDialog(Client.this, "端口范围有误！");
								return;
							}
							Client.PORT = port;
						} catch (NumberFormatException e1) {
							JOptionPane.showMessageDialog(Client.this, "端口类型有误！");
							return;
						}

						dlg.dispose();// 关闭这个界面
					}
				});
				dlg.setVisible(true);// 显示出来
			}
		});

		menuItemHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dlg = new JDialog(Client.this);

				dlg.setBounds(Client.this.getX() + 30, Client.this.getY() + 30, 400, 100);
				dlg.setLayout(new FlowLayout());
				dlg.add(new JLabel("版本所有@乱月"));
				dlg.setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("c")) {
			if (!ValidateUtil.isUserNameForClient(tfdUserName.getText())) {
				JOptionPane.showMessageDialog(this, "用户名输入有误，请重新输入！");
				return;
			}
			userName = tfdUserName.getText();
			connecting();// 连接服务器的动作
			if (clientSocket == null||clientSocket.isClosed()) {
				JOptionPane.showMessageDialog(this, "服务器未开启或网络未连接，无法连接！");
				return;
			}

			((JButton) (e.getSource())).setEnabled(false);
			// 获得btnCon按钮--获得源
			// 相当于btnCon.setEnabled(false);
			btnExit.setEnabled(true);
			btnSend.setEnabled(true);
			tfdUserName.setEditable(false);

		} else if (e.getActionCommand().equals("send")) {
			String msgStr = tfdMsg.getText();
			String toUser = list.getSelectedValue();
			int mode = SystemConstants.MsgUserMode.SEND_ONE;
			if (StringUtil.isNullOrEmpty(msgStr) || StringUtil.isNullOrEmpty(toUser)) {
				return;
			}
			String thisMsg = "";
			if (toUser.equals("全部")) {
				mode = SystemConstants.MsgUserMode.SEND_ALL;
			}

			Message m = new Message(SystemConstants.MsgType.USER, mode, msgStr, userName, toUser);
			try {
				SocketUtil.print(clientSocket, m);
				if(mode == SystemConstants.MsgUserMode.SEND_ONE){
					thisMsg = "  我对【" + toUser + "】说: " + msgStr;
					allMsg.append(thisMsg + SystemConstants.LINE_BREAK);
				}
				// 将发送消息的文本设为空
				tfdMsg.setText("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.error("",e1);
			}

			
		} else if (e.getActionCommand().equals("exit")) {
			sendOffLineMsg();
			setOffLineState();
		}

	}

	private void setOffLineState() {
		SocketUtil.closeSocket(clientSocket);
		// 先把自己在线的菜单清空
		lm.clear();
		this.setTitle("用户【" + userName + "】离线...");
		btnCon.setEnabled(true);
		btnExit.setEnabled(false);
		tfdUserName.setEditable(true);
		isOnline = false;
	}
	
	private void setOnLineState() {
		isOnline = true;
		this.setTitle("用户【" + userName + "】在线...");
	}

	// 向服务器发送退出消息
	private void sendOffLineMsg() {
		Message m = new Message(SystemConstants.MsgType.SYSTEM, SystemConstants.MsgSysMode.OFFLINE, userName);
		SocketUtil.printWithoutException(clientSocket, m);
	}

	private void sendOnlineMsg() {
		Message m = new Message(SystemConstants.MsgType.SYSTEM, SystemConstants.MsgSysMode.ONLINE, userName);
		SocketUtil.printWithoutException(clientSocket, m);
	}

	private void connecting() {
		try {
			clientSocket = new Socket(HOST, PORT);// 跟服务器握手
			sendOnlineMsg();
			setOnLineState();
			new ClientThread().start();// 接受服务器发来的消息---一直开着的
		} catch (UnknownHostException e) {
			e.printStackTrace();
			logger.error("", e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("", e);
		}
	}

	class ClientThread extends Thread {
		@Override
		public void run() {
			try {
				Scanner sc = new Scanner(clientSocket.getInputStream());
				while (sc.hasNextLine()) {
					String str = sc.nextLine();
					System.out.println(userName + ": " + str);
					Message m = MessageUtil.toMessage(str);
					String msgStr = "";
					if (m.getType() == SystemConstants.MsgType.SYSTEM) {
						if (m.getMode() == SystemConstants.MsgSysMode.NOTICE) {
							msgStr = "【通知 】:" + m.getContent();
						} else if (m.getMode() == SystemConstants.MsgSysMode.ONLINE) {
							if (!lm.contains(m.getFromUser())) {
								lm.addElement(m.getFromUser());// 用户上线--添加
							}
							msgStr = "【通知 】:用户【" + m.getFromUser() + "】上线了！";
						} else if (m.getMode() == SystemConstants.MsgSysMode.OFFLINE) {
							lm.removeElement(m.getFromUser());// 用户离线了--移除
							msgStr = "【通知 】:用户【" + m.getFromUser() + "】下线了！";
						} else if (m.getMode() == SystemConstants.MsgSysMode.USERS_SET) {
							lm.clear();
							lm.addElement("全部");
							SwingUtil.addStr(lm, m.getUserList(),userName);
							list.setSelectedIndex(0);// 设置默认显示
						} else if (m.getMode() == SystemConstants.MsgSysMode.SERVER_OFF) {
							setOffLineState();
						} else if (m.getMode() == SystemConstants.MsgSysMode.SERVER_ERROR) {
							setOffLineState();
							msgStr = "【错误 】:" + m.getContent();
						}
					} else if (m.getType() == SystemConstants.MsgType.USER) {
						if (m.getMode() == SystemConstants.MsgUserMode.SEND_ONE) {
							msgStr = "【" + m.getFromUser() + "】对我说: " + m.getContent();
						} else if (m.getMode() == SystemConstants.MsgUserMode.SEND_ALL) {
							msgStr = "【" + m.getFromUser() + "】说: " + m.getContent();
						}
					}
					if (!StringUtil.isNullOrEmpty(msgStr))
						allMsg.append(msgStr + SystemConstants.LINE_BREAK);
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("", e);
			}
		}
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰
		new Client();
	}

}
