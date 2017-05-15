package top.lmoon.util;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import top.lmoon.constants.SystemConstants;

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
			if (!str.equals(me)){
//				System.out.println("ss:"+str);
				lm.addElement(str);
//				System.out.println(lm.toString());
			}	
		}
	}
	
	public static void printInTextArea(JTextArea ta,String content){
		if(!StringUtil.isNullOrEmpty(content)){
			ta.append(content + SystemConstants.LINE_BREAK);
			ta.setCaretPosition(ta.getText().length());
		}
		
	}

}
