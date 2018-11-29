package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BasePromotionGift;

/**
 * Model - 促销赠品
 * 
 * 
 */
public class PromotionGift extends BasePromotionGift<PromotionGift> {
	private static final long serialVersionUID = 6280660523396086070L;
	public static final PromotionGift dao = new PromotionGift();
	
	/**
	 * 根据促销ID删除
	 * @param adminId
	 * @return
	 */
	public boolean delete(Long promotions) {
		return Db.deleteById("promotion_gift", "gift_promotions", promotions);
	}
}
