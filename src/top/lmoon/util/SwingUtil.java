package top.lmoon.util;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import top.lmoon.constants.ResConstants;
import top.lmoon.constants.SystemConstants;

public class SwingUtil {
	
	private static final Logger logger = Logger.getLogger(ResConstants.LOG_COMMON);

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
	
	public static void printInTextPane(JTextPane tp,String content){
		if(!StringUtil.isNullOrEmpty(content)){
			StyledDocument doc = tp.getStyledDocument();
			try {
				doc.insertString(doc.getLength(), content+ SystemConstants.LINE_BREAK, new SimpleAttributeSet());
				tp.setCaretPosition(tp.getDocument().getLength());
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("",e);
			}
//			ta.append(content + SystemConstants.LINE_BREAK);
//			ta.setCaretPosition(ta.getText().length());
		}
	}

}
