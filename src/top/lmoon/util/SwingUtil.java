package top.lmoon.util;

import java.util.List;

import javax.swing.DefaultListModel;

public class SwingUtil {

	public static boolean containsStr(DefaultListModel<String> lm, String str) {
		for (int i = 0; i < lm.getSize(); i++) {
			if (lm.getElementAt(i).equals(str)) {
				return true;
			}
		}
		return false;
	}

	public static void addStr(DefaultListModel<String> lm, List<String> list, String me) {
		for (String str : list) {
			if (!str.equals(me))
				lm.addElement(str);
		}
	}

}
