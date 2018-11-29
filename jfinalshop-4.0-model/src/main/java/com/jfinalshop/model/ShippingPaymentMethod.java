package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseShippingPaymentMethod;

/**
 * Model - 配送支付方式
 * 
 * 
 */
public class ShippingPaymentMethod extends BaseShippingPaymentMethod<ShippingPaymentMethod> {
	private static final long serialVersionUID = -4528760543324881490L;
	public static final ShippingPaymentMethod dao = new ShippingPaymentMethod();
	
	/**
	 * 根据shipping_methods删除参数
	 * @param shipping_methods
	 * @return
	 */
	public boolean delete(Long shippingMethods) {
		return Db.deleteById("shipping_payment_method", "shipping_methods", shippingMethods);
	}
}
