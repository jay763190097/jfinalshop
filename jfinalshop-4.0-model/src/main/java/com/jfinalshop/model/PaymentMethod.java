package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BasePaymentMethod;

/**
 * Model - 支付方式
 * 
 * 
 */
public class PaymentMethod extends BasePaymentMethod<PaymentMethod> {
	private static final long serialVersionUID = 6265962135239167560L;
	public static final PaymentMethod dao = new PaymentMethod();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 款到发货 */
		deliveryAgainstPayment,

		/** 货到付款 */
		cashOnDelivery
	}

	/**
	 * 方式
	 */
	public enum Method {

		/** 在线支付 */
		online,

		/** 线下支付 */
		offline
	}

	/** 配送方式 */
	private List<ShippingMethod> shippingMethods = new ArrayList<ShippingMethod>();

	/** 订单 */
	private List<Order> order = new ArrayList<Order>();
	
	/**
	 * 类型名称
	 */
	public PaymentMethod.Type getTypeName() {
		return PaymentMethod.Type.values()[getType()];
	}
	
	/**
	 * 方式名称
	 */
	public PaymentMethod.Method getMethodName() {
		return PaymentMethod.Method.values()[getMethod()];
	}
	
	
	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public List<ShippingMethod> getShippingMethods() {
		if (CollectionUtils.isEmpty(shippingMethods)) {
			String sql = "SELECT sm.* FROM `shipping_payment_method` spm LEFT JOIN `shipping_method` sm ON spm.`shipping_methods` = sm.`id` WHERE spm.`payment_methods` = ?";
			shippingMethods = ShippingMethod.dao.find(sql, getId());
		}
		return shippingMethods;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethods
	 *            配送方式
	 */
	public void setShippingMethods(List<ShippingMethod> shippingMethods) {
		this.shippingMethods = shippingMethods;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		if (CollectionUtils.isEmpty(order)) {
			String sql = "SELECT * FROM `order` WHERE payment_method_id = ?";
			order = Order.dao.find(sql, getId());
		}
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param order
	 *            订单
	 */
	public void setOrder(List<Order> order) {
		this.order = order;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<ShippingMethod> shippingMethods = getShippingMethods();
		if (shippingMethods != null) {
			for (ShippingMethod shippingMethod : shippingMethods) {
				shippingMethod.getPaymentMethods().remove(this);
			}
		}
		List<Order> orders = getOrder();
		if (orders != null) {
			for (Order order : orders) {
				order.setPaymentMethod(null);
			}
		}
	}
}
