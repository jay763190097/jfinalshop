package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderItem;

/**
 * Dao - 订单项
 * 
 * 
 */
public class OrderItemDao extends BaseDao<OrderItem> {
	
	/**
	 * 构造方法
	 */
	public OrderItemDao() {
		super(OrderItem.class);
	}
	
	/**
	 * 删除
	 */
	public boolean deleteByOrderId(Long orderId) {
		return modelManager.deleteByOrderId(orderId);
	}
	
	/**
	 * 查找评论数量
	 * 
	 * @param member
	 * 			会员
	 * @param status
	 * 			状态
	 * @param isReview
	 * 			是否评论
	 * @param pageable
	 * 			分页
	 * @return
	 * 		订单项分页
	 */
	public Page<OrderItem> findPendingOrderItems(Member member, Order.Status status, Boolean isReview, Pageable pageable) {
		String select = "SELECT r.id AS reviewId , i.* ";
		String sqlExceptSelect = " FROM `order_item` i "
							   + " LEFT JOIN `order` o ON i.order_id = o.id "
							   + " LEFT JOIN review r on  r.order_item_id = i.id WHERE 1 = 1 ";
		
		if (isReview != null) {
			sqlExceptSelect += " AND i.is_review = " + isReview;
		}
		if (member != null) {
			sqlExceptSelect += " AND o.member_id = " + member.getId();
		}
		if (status != null) {
			sqlExceptSelect += " AND o.`status` = " + status.ordinal();
		}
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
	}
	
	
	/**
	 * 查找评论数量
	 * 
	 * @param member
	 * 			会员
	 * @param status
	 * 			状态
	 * @param isReview
	 * 			是否评论
	 * @return
	 * 		订单项数量
	 */
	public Long count(Member member, Order.Status status, Boolean isReview) {
		String sqlExceptSelect = " FROM `order_item` i "
							   + " LEFT JOIN `order` o ON i.order_id = o.id "
							   + " LEFT JOIN review r on  r.order_item_id = i.id WHERE 1 = 1 ";
		
		if (isReview != null) {
			sqlExceptSelect += " AND i.is_review = " + isReview;
		}
		if (member != null) {
			sqlExceptSelect += " AND o.member_id = " + member.getId();
		}
		if (status != null) {
			sqlExceptSelect += " AND o.`status` = " + status.ordinal();
		}
		return super.count(sqlExceptSelect);
	}
}