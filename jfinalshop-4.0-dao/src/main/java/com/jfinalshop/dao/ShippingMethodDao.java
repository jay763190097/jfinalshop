package com.jfinalshop.dao;

import com.jfinalshop.model.ShippingMethod;


/**
 * Dao - 配送方式
 * 
 * 
 */
public class ShippingMethodDao extends OrderEntity<ShippingMethod> {
	
	/**
	 * 构造方法
	 */
	public ShippingMethodDao() {
		super(ShippingMethod.class);
	}
}