package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Model;

/**
 * Entity - 排序基类
 * @param <M>
 * 
 * 
 */
public abstract class OrderEntity<M extends Model<M>> extends BaseDao <M> {

	public OrderEntity(Class<M> entityClass) {
		super(entityClass);
	}

	/** "排序"属性名称 */
	public static final String ORDER_NAME = "orders";

	/** 排序 */
	private Integer orders;

	/**
	 * 获取排序
	 * 
	 * @return 排序
	 */
	public Integer getOrders() {
		return orders;
	}

	/**
	 * 设置排序
	 * 
	 * @param order
	 *            排序
	 */
	public void setOrders(Integer orders) {
		this.orders = orders;
	}


}
