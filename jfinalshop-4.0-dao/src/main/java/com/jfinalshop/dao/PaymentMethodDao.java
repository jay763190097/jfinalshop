package com.jfinalshop.dao;

import com.jfinalshop.model.PaymentMethod;

/**
 * Dao - 支付方式
 * 
 * 
 */
public class PaymentMethodDao extends OrderEntity<PaymentMethod> {
	
	/**
	 * 构造方法
	 */
	public PaymentMethodDao() {
		super(PaymentMethod.class);
	}
}