package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.OrderLogDao;
import com.jfinalshop.model.OrderLog;

/**
 * Service - 订单记录
 * 
 * 
 */
@Singleton
public class OrderLogService extends BaseService<OrderLog> {

	/**
	 * 构造方法
	 */
	public OrderLogService() {
		super(OrderLog.class);
	}
	
	@Inject
	private OrderLogDao orderLogDao;
	
	/**
	 * 根据订单查找日志
	 * @param orderId
	 * @return
	 */
	public OrderLog findOrderLog(Long orderId) {
		return orderLogDao.findOrderLog(orderId);
	}
}