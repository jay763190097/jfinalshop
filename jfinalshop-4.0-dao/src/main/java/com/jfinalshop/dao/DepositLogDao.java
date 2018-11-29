package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.DepositLog;
import com.jfinalshop.model.Member;

/**
 * Dao - 预存款记录
 * 
 * 
 */
public class DepositLogDao extends BaseDao<DepositLog> {
	
	/**
	 * 构造方法
	 */
	public DepositLogDao() {
		super(DepositLog.class);
	}
	
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
		if (member == null) {
			return null;
		}
		String sqlExceptSelect = "FROM deposit_log WHERE member_id = " + member.getId();
		return super.findPage(sqlExceptSelect, pageable);
	}

}