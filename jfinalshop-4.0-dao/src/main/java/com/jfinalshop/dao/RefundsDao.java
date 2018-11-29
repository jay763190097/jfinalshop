package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Refunds;

/**
 * Dao - 退款单
 * 
 * 
 */
public class RefundsDao extends BaseDao<Refunds> {
	
	/**
	 * 构造方法
	 */
	public RefundsDao() {
		super(Refunds.class);
	}
	
	/**
	 * 查找退款单分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 退款单分页
	 */
	public Page<Refunds> findPage(Member member, Pageable pageable) {
		String select = " SELECT  * ";
		String sqlExceptSelect = "FROM refunds WHERE 1 = 1";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		sqlExceptSelect += " ORDER BY " + CREATE_DATE + " DESC ";
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
	}
}