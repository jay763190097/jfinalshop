package com.jfinalshop.dao;

import com.jfinalshop.model.OrderLog;

/**
 * Dao - 订单记录
 * 
 * 
 */
public class OrderLogDao extends BaseDao<OrderLog> {
	
	/**
	 * 构造方法
	 */
	public OrderLogDao() {
		super(OrderLog.class);
	}
	
	/**
	 * 根据订单查找日志
	 * @param orderId
	 * @return
	 */
	public OrderLog findOrderLog(Long orderId) {
		if (orderId == null) {
			return null;
		}
		try {
			String sql = "SELECT * FROM `order_log` WHERE order_id = ? AND `type` = " + OrderLog.Type.cancel.ordinal();
			return modelManager.findFirst(sql, orderId);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 删除
	 */
	public void deleteByOrderId(Long orderId) {
		modelManager.deleteByOrderId(orderId);
	}
}