package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseProductCategoryPromotion;

/**
 * Model - 商品分类促销
 * 
 * 
 */
public class ProductCategoryPromotion extends BaseProductCategoryPromotion<ProductCategoryPromotion> {
	private static final long serialVersionUID = -6143574415574609749L;
	public static final ProductCategoryPromotion dao = new ProductCategoryPromotion();
	
	/**
	 * 根据productCategoryId删除参数
	 * @param productCategoryId
	 * @return
	 */
	public boolean delete(Long id) {
		return Db.deleteById("product_category_promotion", "product_categories", id);
	}
}
