package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseProductCategoryBrand;

/**
 * Model - 商品分类品牌
 * 
 * 
 */
public class ProductCategoryBrand extends BaseProductCategoryBrand<ProductCategoryBrand> {
	private static final long serialVersionUID = -4065001154372937087L;
	public static final ProductCategoryBrand dao = new ProductCategoryBrand();
	
	/**
	 * 根据productCategoryId删除参数
	 * @param productCategoryId
	 * @return
	 */
	public boolean delete(Long id) {
		return Db.deleteById("product_category_brand", "product_categories", id);
	}
}
