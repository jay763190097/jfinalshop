package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.DepositLogDao;
import com.jfinalshop.model.DepositLog;
import com.jfinalshop.model.Member;

/**
 * Service - 预存款记录
 * 
 * 
 */
@Singleton
public class DepositLogService extends BaseService<DepositLog> {

	/**
	 * 构造方法
	 */
	public DepositLogService() {
		super(DepositLog.class);
	}
	
	@Inject
	private DepositLogDao depositLogDao;
	
	/**
	 * 查找预存款记录分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 预存款记录分页
	 */
	public Page<DepositLog> findPage(Member member, Pageable pageable) {
		return depositLogDao.findPage(member, pageable);
	}

}