package com.jfinalshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePromotionGift<M extends BasePromotionGift<M>> extends Model<M> implements IBean {

	public void setGiftPromotions(java.lang.Long giftPromotions) {
		set("gift_promotions", giftPromotions);
	}

	public java.lang.Long getGiftPromotions() {
		return get("gift_promotions");
	}

	public void setGifts(java.lang.Long gifts) {
		set("gifts", gifts);
	}

	public java.lang.Long getGifts() {
		return get("gifts");
	}

}
