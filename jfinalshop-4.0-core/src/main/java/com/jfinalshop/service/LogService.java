package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.LogDao;
import com.jfinalshop.model.Log;


/**
 * Service - 日志
 * 
 * 
 */
@Singleton
public class LogService extends BaseService<Log> {

	/**
	 * 构造方法
	 */
	public LogService() {
		super(Log.class);
	}
	
	@Inject
	private LogDao logDao;
	
	/**
	 * 清空日志
	 */
	public void clear() {
		logDao.removeAll();
	}

}