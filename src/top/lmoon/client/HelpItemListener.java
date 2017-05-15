package top.lmoon.client;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class HelpItemListener implements ActionListener {

	private Frame owner;
	
	public HelpItemListener(Frame owner) {
		super();
		this.owner = owner;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dlg = new JDialog(owner);

		dlg.setBounds(owner.getX() + 30, owner.getY() + 30, 400, 100);
		dlg.setLayout(new FlowLayout());
		dlg.add(new JLabel("有问题请联系我 1192743812@qq.com"));
		dlg.setVisible(true);
	}

}
