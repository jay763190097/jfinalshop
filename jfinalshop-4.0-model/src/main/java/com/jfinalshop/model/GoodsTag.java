package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseGoodsTag;

/**
 * Model - 货品标签
 * 
 * 
 */
public class GoodsTag extends BaseGoodsTag<GoodsTag> {
	private static final long serialVersionUID = -7218948201172701315L;
	public static final GoodsTag dao = new GoodsTag();
	
	/**
	 * 根据goods删除参数
	 * @param goods
	 * @return
	 */
	public boolean delete(Long goods) {
		return Db.deleteById("goods_tag", "goods", goods);
	}
}
