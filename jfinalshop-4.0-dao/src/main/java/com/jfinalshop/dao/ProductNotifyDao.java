package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductNotify;

/**
 * Dao - 到货通知
 * 
 * 
 */
public class ProductNotifyDao extends BaseDao<ProductNotify> {
	
	/**
	 * 构造方法
	 */
	public ProductNotifyDao() {
		super(ProductNotify.class);
	}
	
	
	/**
	 * 判断到货通知是否存在
	 * 
	 * @param product
	 *            商品
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 到货通知是否存在
	 */
	public boolean exists(Product product, String email) {
		if (product == null || StringUtils.isEmpty(email)) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM product_notify WHERE product_id = ? AND email = LOWER(?) AND has_sent = FALSE";
		Long count = Db.queryLong(sql, product.getId(), email);
		return count > 0;
	}

	/**
	 * 查找到货通知分页
	 * 
	 * @param member
	 *            会员
	 * @param isMarketable
	 *            是否上架
	 * @param isOutOfStock
	 *            商品是否缺货
	 * @param hasSent
	 *            是否已发送.
	 * @param pageable
	 *            分页信息
	 * @return 到货通知分页
	 */
	public Page<ProductNotify> findPage(Member member, Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent, Pageable pageable) {
		String sqlExceptSelect = "FROM product_notify n LEFT JOIN product p ON n.product_id = p.id LEFT JOIN goods g ON p.goods_id = g.id WHERE 1 = 1 ";
		if (member != null) {
			sqlExceptSelect += " AND n.member_id = " + member.getId();
		}
		if (isMarketable != null) {
			sqlExceptSelect += " AND g.is_marketable = " + isMarketable;
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sqlExceptSelect += " AND(p.stock IS NOT NULL AND p.stock <= p.allocated_stock) ";
			} else {
				sqlExceptSelect += " AND(p.stock IS NULL OR p.stock > p.allocated_stock) ";
			}
		}
		if (hasSent != null) {
			sqlExceptSelect += " AND n.has_sent = " + hasSent ;
		}
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order("n.create_date", Order.Direction.desc));
		pageable.setOrders(orders);
		return super.findPage(sqlExceptSelect, pageable);
	}

	/**
	 * 查找到货通知数量
	 * 
	 * @param member
	 *            会员
	 * @param isMarketable
	 *            是否上架
	 * @param isOutOfStock
	 *            商品是否缺货
	 * @param hasSent
	 *            是否已发送.
	 * @return 到货通知数量
	 */
	public Long count(Member member, Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent) {
		String sqlExceptSelect = "FROM product_notify productNotify LEFT JOIN product p ON productNotify.product_id = p.id LEFT JOIN goods g ON p.goods_id = g.id WHERE 1 = 1 ";
		if (member != null) {
			sqlExceptSelect += " AND productNotify.member_id = " + member.getId();
		}
		if (isMarketable != null) {
			sqlExceptSelect += " AND g.is_marketable = " + isMarketable;
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sqlExceptSelect += "AND(p.stock IS NOT NULL AND p.stock <= p.allocated_stock) ";
			} else {
				sqlExceptSelect += "AND(p.stock IS NULL OR p.stock > p.allocated_stock) ";
			}
		}
		if (hasSent != null) {
			sqlExceptSelect += "AND productNotify.has_sent = " + hasSent + " ";
		}
		return super.count(sqlExceptSelect);
	}

}