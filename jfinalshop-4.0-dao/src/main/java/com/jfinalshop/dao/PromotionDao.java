package com.jfinalshop.dao;

import java.util.List;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.util.DateUtils;

/**
 * Dao - 促销
 * 
 * 
 */
public class PromotionDao extends OrderEntity<Promotion> {
	
	/**
	 * 构造方法
	 */
	public PromotionDao() {
		super(Promotion.class);
	}
	
	/**
	 * 查找促销
	 * 
	 * @param memberRank
	 *            会员等级
	 * @param productCategory
	 *            商品分类
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 促销
	 */
	public List<Promotion> findList(MemberRank memberRank, ProductCategory productCategory, Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM promotion WHERE 1 = 1 ";
		if (memberRank != null) {
			sql = "SELECT p.* FROM promotion p left JOIN promotion_member_rank r ON p.id = r.promotions WHERE r.member_ranks = " + memberRank.getId();
		}
		if (productCategory != null) {
			sql = "SELECT p.* FROM promotion p left JOIN product_category_promotion c ON p.id = c.promotions WHERE c.product_categories = " + productCategory.getId();
		}
		if (hasBegun != null) {
			if (hasBegun) {
				sql += " AND (begin_date IS NULL OR begin_date <= '" + DateUtils.getNowTime()+ "')";
			} else {
				sql += " AND (begin_date IS NOT NULL OR begin_date > '" + DateUtils.getNowTime()+ "')";
			}
		}
		if (hasEnded != null) {
			if (hasEnded) {
				sql += " AND (end_date IS NULL OR end_date <= '" + DateUtils.getNowTime()+ "')";
			} else {
				sql += " AND (end_date IS NULL OR end_date > '" + DateUtils.getNowTime()+ "')";
			}
		}
		return super.findList(sql, null, count, filters, orders);
	}

}