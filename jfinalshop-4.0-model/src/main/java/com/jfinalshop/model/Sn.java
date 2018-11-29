package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseSn;

/**
 * Model - 序列号
 * 
 * 
 */
public class Sn extends BaseSn<Sn> {
	private static final long serialVersionUID = 7977837709024723646L;
	public static final Sn dao = new Sn();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 货品 */
		goods,

		/** 订单 */
		order,

		/** 支付记录 */
		paymentLog,

		/** 收款单 */
		payment,

		/** 退款单 */
		refunds,

		/** 发货单 */
		shipping,

		/** 退货单 */
		returns
	}
}
