package com.jfinalshop.dao;

import java.util.List;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.ProductCategory;

/**
 * Dao - 品牌
 * 
 * 
 */
public class BrandDao extends OrderEntity<Brand> {
	
	/**
	 * 构造方法
	 */
	public BrandDao() {
		super(Brand.class);
	}

	/**
	 * 查找品牌
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 品牌
	 */
	public List<Brand> findList(ProductCategory productCategory, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM brand WHERE 1 = 1 ";
		if (productCategory != null) {
			sql = "SELECT b.* FROM product_category_brand pcb INNER JOIN brand b ON pcb.`brands`= b.`id` WHERE pcb.`product_categories` = " + productCategory.getId() + " ";
		}
		return super.findList(sql, null, count, filters, orders);
	}

}