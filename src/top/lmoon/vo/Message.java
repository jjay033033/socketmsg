/**
 * 
 */
package top.lmoon.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author guozy
 * @date 2017-5-11
 * 
 */
public class Message implements Serializable{
	
	private static final long serialVersionUID = -3771284607662403708L;
	
	private int type;
	
	private int mode;

	private String content;
	
	private String fromUser;
	
	private String toUser;
	
	private List<String> userList;
	
	private Map dataMap;
	
	public Message(){
		
	}

	public Message(int type, int mode) {
		super();
		this.type = type;
		this.mode = mode;
	}

	public Message(int type, int mode, String fromUser) {
		super();
		this.type = type;
		this.mode = mode;
		this.fromUser = fromUser;
	}

	public Message(int type, int mode, List userList) {
		super();
		this.type = type;
		this.mode = mode;
		this.userList = userList;
	}

	public Message(int type, int mode, String content, String fromUser, String toUser, List userList, Map dataMap) {
		super();
		this.type = type;
		this.mode = mode;
		this.content = content;
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.userList = userList;
		this.dataMap = dataMap;
	}

	public Message(int type, int mode, String content, String fromUser, String toUser) {
		super();
		this.type = type;
		this.mode = mode;
		this.content = content;
		this.fromUser = fromUser;
		this.toUser = toUser;
	}


	public Message(int type, int mode, String content, String fromUser) {
		super();
		this.type = type;
		this.mode = mode;
		this.content = content;
		this.fromUser = fromUser;
	}


	public List<String> getUserList() {
		return userList;
	}

	public void setUserList(List<String> userList) {
		this.userList = userList;
	}

	public Map getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map dataMap) {
		this.dataMap = dataMap;
	}

	public int getMode() {
		return mode;
	}


	public void setMode(int mode) {
		this.mode = mode;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getFromUser() {
		return fromUser;
	}


	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}


	public String getToUser() {
		return toUser;
	}


	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	@Override
	public String toString() {
		return "Message [type=" + type + ", mode=" + mode + ", content=" + content + ", fromUser=" + fromUser
				+ ", toUser=" + toUser + ", userList=" + userList + ", dataMap=" + dataMap + "]";
	}
	
	

}
