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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

import top.lmoon.constants.ResConstants;
import top.lmoon.constants.SystemConstants;
import top.lmoon.util.MessageUtil;
import top.lmoon.util.SocketUtil;
import top.lmoon.util.SwingUtil;
import top.lmoon.vo.Message;

/**
 * @author guozy
 * @date 2017-5-11
 * 
 */
public class Server extends JFrame {

	private static final long serialVersionUID = -3257413069018446440L;

	private static final Logger logger = Logger.getLogger(ResConstants.LOG_SERVER);

	private JList<String> list;
	protected JTextArea area;
	protected DefaultListModel<String> lm;

	// 用来保存所有在线用户的名字和Socket----池
	protected ConcurrentMap<String, Socket> usersMap = new ConcurrentHashMap<String, Socket>();

	private static Server instance;

	public static Server getInstance() {
		if (instance == null) {
			instance = new Server();
		}
		return instance;
	}

	private Server() {

		super("闲聊服务器");
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
			for (Socket s : usersMap.values()) {
				SocketUtil.printWithoutException(s, m);
			}
		}
	}

	private void startServer() {
		try {
			ServerSocket server = new ServerSocket(SystemConstants.PORT_DEFAULT);
			SwingUtil.printInTextArea(area, "启动服务：" + server);
			new ServerThread(server).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	



	public static void main(String[] args) {
		// JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰
		Server.getInstance();
	}

}
