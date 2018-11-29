package com.jfinalshop.dao;

import java.util.List;

import com.jfinalshop.model.Member;
import com.jfinalshop.model.ReturnsItem;

/**
 * Dao - 退货单项
 * 
 * 
 */
public class ReturnsItemDao extends BaseDao<ReturnsItem> {

	/**
	 * 构造方法
	 */
	public ReturnsItemDao() {
		super(ReturnsItem.class);
	}
	
	/**
	 * 退货单项
	 * 
	 * @param member
	 *            
	 * @return 退货单项，若不存在则返回null
	 */
	public List<ReturnsItem> findReturnsItems(Member member, Integer status) {
		if (member == null) {
			return null;
		}
		try {
			String sql = "SELECT * FROM returns_item WHERE member_id = ? ";
			if (status == ReturnsItem.Status.refund.ordinal()) {
				sql += " AND status = " + ReturnsItem.Status.refund.ordinal();
			} else {
				sql += " AND status != " + ReturnsItem.Status.refund.ordinal();
			}
			return modelManager.find(sql, member.getId());
		} catch (Exception e) {
			return null;
		}
	}
	
	
}
