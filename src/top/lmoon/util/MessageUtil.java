package top.lmoon.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import top.lmoon.constants.ResConstants;
import top.lmoon.vo.Message;

public class MessageUtil {

	private static final Logger logger = Logger.getLogger(MessageUtil.class);
	
	public static Message toMessage(String str){
		Message m  = new Message();
		try{
			JSONObject jo = JSONObject.fromObject(str);
			m = (Message) JSONObject.toBean(jo, Message.class);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("toBean出错",e);
		}
		return m;
	}
	
	public static String toStr(Message m){
		String str = "";
		try{
			JSONObject jo = JSONObject.fromObject(m);
			str = jo.toString();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("toStr出错",e);
		}
		return str;
	}
	
	public static void main(String[] args) {
		Set s = new HashSet<>();
		s.add("a");
		s.add("c");
		s.add("b");
		s.add("d");
		Message m = new Message();
		Map map = new HashMap<>();
		map.put("sets", s);
		m.setDataMap(map);
		System.out.println(JSONObject.fromObject(m).toString());
	}


}
