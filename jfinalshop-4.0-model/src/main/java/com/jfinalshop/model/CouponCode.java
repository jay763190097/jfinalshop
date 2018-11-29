package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseCouponCode;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 优惠码
 * 
 * 
 */
public class CouponCode extends BaseCouponCode<CouponCode> {
	private static final long serialVersionUID = -7860219025745106172L;
	public static final CouponCode dao = new CouponCode();
	
	/** 优惠券 */
	private Coupon coupon;

	/** 会员 */
	private Member member;

	/** 订单 */
	private Order order;
	
	/**
	 * 获取优惠券
	 * 
	 * @return 优惠券
	 */
	public Coupon getCoupon() {
		if (ObjectUtils.isEmpty(coupon)) {
			coupon = Coupon.dao.findById(getCouponId());
		}
		return coupon;
	}

	/**
	 * 设置优惠券
	 * 
	 * @param coupon
	 *            优惠券
	 */
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
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
		if (ObjectUtils.isEmpty(order)) {
			String sql = "SELECT * FROM `order`  WHERE `coupon_code_id` = ?";
			order = Order.dao.findFirst(sql, getId());
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
	 * 删除前处理
	 */
	public void preRemove() {
		if (getOrder() != null) {
			getOrder().setCouponCode(null);
		}
	}
}
