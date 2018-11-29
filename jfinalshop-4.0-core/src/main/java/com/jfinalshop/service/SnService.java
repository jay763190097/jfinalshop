package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.Sn;

/**
 * Service - 序列号
 * 
 * 
 */
@Singleton
public class SnService {

	@Inject
	private SnDao snDao;
	
	/**
	 * 生成序列号
	 * 
	 * @param type
	 *            类型
	 * @return 序列号
	 */
	public String generate(Sn.Type type) {
		return snDao.generate(type);
	}

}