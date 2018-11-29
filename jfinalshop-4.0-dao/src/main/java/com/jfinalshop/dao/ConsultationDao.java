package com.jfinalshop.dao;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Member;

/**
 * Dao - 咨询
 * 
 * 
 */
public class ConsultationDao extends BaseDao<Consultation> {
	
	/**
	 * 构造方法
	 */
	public ConsultationDao() {
		super(Consultation.class);
	}
	
	/**
	 * 查找咨询
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 咨询，不包含咨询回复
	 */
	public List<Consultation> findList(Member member, Goods goods, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM consultation WHERE for_consultation_id IS NULL";
		if (member != null) {
			sql += " AND member_id = " + member.getId() ;
		}
		if (goods != null) {
			sql += " AND goods_id = " + goods.getId();
		}
		if (isShow != null) {
			sql += " AND is_show = " + isShow;
		}
		return super.findList(sql, null, count, filters, orders);
	}

	/**
	 * 查找咨询分页
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 咨询分页，不包含咨询回复
	 */
	public Page<Consultation> findPage(Member member, Goods goods, Boolean isShow, Pageable pageable) {
		String sqlExceptSelect = "FROM consultation WHERE for_consultation_id IS NULL";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (goods != null) {
			sqlExceptSelect += " AND goods_id = " + goods.getId();
		}
		if (isShow != null) {
			sqlExceptSelect += " AND is_show = " + isShow;
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查找咨询数量
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isShow
	 *            是否显示
	 * @return 咨询数量，不包含咨询回复
	 */
	public Long count(Member member, Goods goods, Boolean isShow) {
		String sqlExceptSelect = "FROM consultation WHERE for_consultation_id IS NULL ";
		if (member != null) {
			sqlExceptSelect += " AND member_id = " + member.getId();
		}
		if (goods != null) {
			sqlExceptSelect += " AND goods_id = " + goods.getId();
		}
		if (isShow != null) {
			sqlExceptSelect += " AND is_show = " + isShow;
		}
		return super.count(sqlExceptSelect);
	}


}