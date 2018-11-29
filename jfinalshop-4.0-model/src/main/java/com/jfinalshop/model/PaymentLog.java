package com.jfinalshop.model;

import java.math.BigDecimal;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePaymentLog;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 支付记录
 * 
 * 
 */
public class PaymentLog extends BasePaymentLog<PaymentLog> {
	private static final long serialVersionUID = 3606586430085312064L;
	public static final PaymentLog dao = new PaymentLog();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 预存款充值 */
		recharge,

		/** 订单支付 */
		payment
	}

	/**
	 * 状态
	 */
	public enum Status {

		/** 等待支付 */
		wait,

		/** 支付成功 */
		success,

		/** 支付失败 */
		failure
	}
	
	/** 会员 */
	private Member member;

	/** 订单 */
	private Order order;
	
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
	}
	
	/**
	 * 状态名称
	 */
	public Status getStatusName() {
		return Status.values()[getStatus()];
	}
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		if (ObjectUtils.isEmpty(member)) {
			member = Member.dao.findById(getMemberId());
		}
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		if (ObjectUtils.isEmpty(member)) {
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
	 * 获取有效金额
	 * 
	 * @return 有效金额
	 */
	public BigDecimal getEffectiveAmount() {
		BigDecimal effectiveAmount = getAmount().subtract(getFee());
		return effectiveAmount.compareTo(BigDecimal.ZERO) >= 0 ? effectiveAmount : BigDecimal.ZERO;
	}
	
	/**
	 * 删除
	 * @param orderId
	 * @return
	 */
	public boolean deleteByOrderId(Long orderId) {
		return Db.deleteById("payment_log", "order_id", orderId);
	}
}
