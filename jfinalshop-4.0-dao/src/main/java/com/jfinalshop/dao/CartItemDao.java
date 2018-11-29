package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.CartItem;

/**
 * Dao - 购物车项
 * 
 * 
 */
public class CartItemDao extends BaseDao<CartItem> {
	
	/**
	 * 构造方法
	 */
	public CartItemDao() {
		super(CartItem.class);
	}
	
	/**
	 * 根据购物车Id删除
	 * @param cartId
	 * @return
	 */
	public boolean delete(Long cartId) {
		return Db.deleteById("cart_item", "cart_id", cartId);
	}
}