package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePromotionCoupon;

/**
 * Model - 促销优惠券
 * 
 * 
 */
public class PromotionCoupon extends BasePromotionCoupon<PromotionCoupon> {
	private static final long serialVersionUID = 6402689975757641260L;
	public static final PromotionCoupon dao = new PromotionCoupon();
	
	/**
	 * 根据促销ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long promotions) {
		return Db.deleteById("promotion_coupon", "promotions", promotions);
	}
}
