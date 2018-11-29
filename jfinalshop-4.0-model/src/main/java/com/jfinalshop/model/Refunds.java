package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseRefunds;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 退款单
 * 
 * 
 */
public class Refunds extends BaseRefunds<Refunds> {
	private static final long serialVersionUID = 1255306939293158909L;
	public static final Refunds dao = new Refunds();
	
	/**
	 * 方式
	 */
	public enum Method {

		/** 在线支付 */
		online,

		/** 线下支付 */
		offline,

		/** 预存款支付 */
		deposit
	}

	/** 订单 */
	private Order order;
	
	/**
	 * 方式
	 */
	public Refunds.Method getMethodName() {
		return Refunds.Method.values()[getMethod()];
	}
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		if (ObjectUtils.isEmpty(order)) {
			order = Order.dao.findById(getOrderId());
		}
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param order
	 *            订单
	 */
	public void setOrder(Order order) {
		this.order = order;
	}
	
	
	/**
	 * 设置支付方式
	 * 
	 * @param paymentMethod
	 *            支付方式
	 */
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		setPaymentMethod(paymentMethod != null ? paymentMethod.getName() : null);
	}

	/**
	 * 设置操作员
	 * 
	 * @param operator
	 *            操作员
	 */
	public void setOperator(Admin operator) {
		setOperator(operator != null ? operator.getUsername() : null);
	}
	
	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
}
