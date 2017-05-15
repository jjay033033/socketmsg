package top.lmoon.client;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ConfigItemListener implements ActionListener {

	private Frame owner;
	
	public ConfigItemListener(Frame owner) {
		super();
		this.owner = owner;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final JDialog dlg = new JDialog(owner);// 弹出一个界面
		// 不能直接用this

		dlg.setBounds(owner.getX() + 20, owner.getY() + 30, 350, 150);
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
		ConfigConfirmListener cfl = new ConfigConfirmListener(tfdHost, tfdPort, dlg);
		btnSet.addActionListener(cfl);

		dlg.setVisible(true);// 显示出来
	}

}
