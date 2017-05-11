/**
 * 
 */
package top.lmoon.vo;

import java.io.Serializable;

/**
 * @author guozy
 * @date 2017-5-11
 * 
 */
public class Message implements Serializable{
	
	private static final long serialVersionUID = -3771284607662403708L;

	private String cmd;
	
	private int type;

	private String content;
	
	private String fromeUser;
	
	private String toUser;
	
	
	public Message(String msg){
		
	}


	public String getCmd() {
		return cmd;
	}


	public void setCmd(String cmd) {
		this.cmd = cmd;
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


	public String getFromeUser() {
		return fromeUser;
	}


	public void setFromeUser(String fromeUser) {
		this.fromeUser = fromeUser;
	}


	public String getToUser() {
		return toUser;
	}


	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	
	

}
