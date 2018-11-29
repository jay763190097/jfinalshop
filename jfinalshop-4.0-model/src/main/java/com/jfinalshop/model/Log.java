package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseLog;

/**
 * Model - 日志
 * 
 * 
 */
public class Log extends BaseLog<Log> {
	private static final long serialVersionUID = -1062809507544115617L;
	public static final Log dao = new Log();
	
	/** "日志内容"属性名称 */
	public static final String LOG_CONTENT_ATTRIBUTE_NAME = Log.class.getName() + ".CONTENT";
	
	/**
	 * 设置操作员
	 * 
	 * @param operator
	 *            操作员
	 */
	public void setOperator(Admin operator) {
		setOperator(operator != null ? operator.getUsername() : null);
	}
}
