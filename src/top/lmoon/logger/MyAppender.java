/**
 * 
 */
package top.lmoon.logger;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

/**
 * @author guozy
 * @date 2017-5-19
 * 
 */
public class MyAppender extends DailyRollingFileAppender{

	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		return this.getThreshold().equals(priority);
	}
	
	

}
