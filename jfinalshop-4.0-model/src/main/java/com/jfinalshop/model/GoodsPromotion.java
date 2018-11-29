package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseGoodsPromotion;

/**
 * Model - 货品促销
 * 
 * 
 */
public class GoodsPromotion extends BaseGoodsPromotion<GoodsPromotion> {
	private static final long serialVersionUID = 3546983568702184327L;
	public static final GoodsPromotion dao = new GoodsPromotion();
	
	/**
	 * 根据goods删除参数
	 * @param goods
	 * @return
	 */
	public boolean delete(Long goods) {
		return Db.deleteById("goods_promotion", "goods", goods);
	}
	
}
