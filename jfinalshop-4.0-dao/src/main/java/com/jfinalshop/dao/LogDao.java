package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Log;

/**
 * Dao - 日志
 * 
 * 
 */
public class LogDao extends BaseDao<Log> {
	
	/**
	 * 构造方法
	 */
	public LogDao() {
		super(Log.class);
	}
	
	/**
	 * 删除所有日志
	 */
	public void removeAll() {
		String sql = "DELETE FROM log";
		Db.update(sql);
	}

}