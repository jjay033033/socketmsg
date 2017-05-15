package top.lmoon.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ConfigConfirmListener implements ActionListener {
	
	private JTextField tfdHost;
	private JTextField tfdPort;
	private JDialog dlg;
	
	

	public ConfigConfirmListener(JTextField tfdHost, JTextField tfdPort, JDialog dlg) {
		super();
		this.tfdHost = tfdHost;
		this.tfdPort = tfdPort;
		this.dlg = dlg;
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		String ip = tfdHost.getText();// 解析并判断ip是否合法
		String strs[] = ip.split("\\.");
		if (strs == null || strs.length != 4) {
			JOptionPane.showMessageDialog(dlg, "IP类型有误！");
			return;
		}
		try {
			for (int i = 0; i < 4; i++) {
				int num = Integer.parseInt(strs[i]);
				if (num > 255 || num < 0) {
					JOptionPane.showMessageDialog(dlg, "IP类型有误！");
					return;
				}
			}
		} catch (NumberFormatException e2) {
			JOptionPane.showMessageDialog(dlg, "IP类型有误！");
			return;
		}

		Client.HOST = ip;// 先解析并判断ip是否合法

		try {
			int port = Integer.parseInt(tfdPort.getText());
			if (port < 0 || port > 65535) {
				JOptionPane.showMessageDialog(dlg, "端口范围有误！");
				return;
			}
			Client.PORT = port;
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(dlg, "端口类型有误！");
			return;
		}
		System.out.println(Client.HOST);
		dlg.dispose();// 关闭这个界面
	}

}
