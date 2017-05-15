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
import java.net.Socket;
import java.net.UnknownHostException;

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
	protected JList<String> list;
	protected DefaultListModel<String> lm;
	protected JTextArea allMsg;
	private JTextField tfdMsg;
	private JButton btnCon;
	private JButton btnExit;
	private JButton btnSend;

	protected static String HOST = SystemConstants.IP_DEFAULT;// 自己机子，服务器的ip地址
	protected static int PORT = SystemConstants.PORT_DEFAULT;// 服务器的端口号
	protected static Socket clientSocket;
	protected static String userName;

	private static boolean isOnline = false;
	
	private static Client instance;
	
	public static Client getInstance(){
		if(instance == null){
			instance = new Client();
		}
		return instance;
	}

	private Client() {

		super("闲聊");
		
		// 菜单条
		addJMenu();

		// 上面的面板
		JPanel p = new JPanel();
		JLabel jlb1 = new JLabel("用户标识:");
		tfdUserName = new JTextField(10);

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
		allMsg.setLineWrap(true);
		allMsg.setWrapStyleWord(true);
		allMsg.setEditable(false);
		cenP.add(new JScrollPane(allMsg), BorderLayout.CENTER);

		// 消息发送面板
		JPanel p3 = new JPanel();
		JLabel jlb2 = new JLabel("消息:");
		p3.add(jlb2);
//		tfdMsg = new JTextArea(2, 20);
//		tfdMsg.setLineWrap(true);
//		tfdMsg.setWrapStyleWord(true);
		tfdMsg = new JTextField(20);
		p3.add(tfdMsg);
		btnSend = new JButton("发送");
//		btnSend.setEnabled(false);
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
		setOffLineState();
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
		
		ConfigItemListener cil = new ConfigItemListener(Client.this);
		menuItemSet.addActionListener(cil);

		HelpItemListener hil = new HelpItemListener(Client.this);
		menuItemHelp.addActionListener(hil);
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
			if (clientSocket == null || clientSocket.isClosed()) {
				JOptionPane.showMessageDialog(this, "服务器未开启或网络未连接，无法连接！");
				return;
			}
			// 获得btnCon按钮--获得源
			// ((JButton) (e.getSource())).setEnabled(false);

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
				if (mode == SystemConstants.MsgUserMode.SEND_ONE) {
					thisMsg = "  我对【" + toUser + "】说: " + msgStr;
					SwingUtil.printInTextArea(allMsg,thisMsg);
				}
				// 将发送消息的文本设为空
				tfdMsg.setText("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.error("", e1);
			}

		} else if (e.getActionCommand().equals("exit")) {
			sendOffLineMsg();
			setOffLineState();
		}

	}

	protected void setOffLineState() {
		SocketUtil.closeSocket(clientSocket);
		// 先把自己在线的菜单清空
		lm.clear();
		btnCon.setEnabled(true);
		btnExit.setEnabled(false);
		btnSend.setEnabled(false);
		tfdUserName.setEditable(true);
		tfdMsg.setEditable(false);
		isOnline = false;
		this.setTitle("闲聊 · 离线...");
	}

	protected void setOnLineState() {
		btnCon.setEnabled(false);
		btnExit.setEnabled(true);
		btnSend.setEnabled(true);
		tfdUserName.setEditable(false);
		tfdMsg.setEditable(true);
		isOnline = true;
		this.setTitle("闲聊 · 在线...");
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

	

	public static void main(String[] args) {
		// JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰
		Client.getInstance();
	}

}
