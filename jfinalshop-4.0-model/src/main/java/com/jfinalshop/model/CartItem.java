package com.jfinalshop.model;

import java.math.BigDecimal;

import com.jfinalshop.Setting;
import com.jfinalshop.model.base.BaseCartItem;
import com.jfinalshop.util.ObjectUtils;
import com.jfinalshop.util.SystemUtils;

/**
 * Model - 购物车项
 * 
 * 
 */
public class CartItem extends BaseCartItem<CartItem> {
	private static final long serialVersionUID = -5006709902370682355L;
	public static final CartItem dao = new CartItem();
	
	/** 最大数量 */
	public static final Integer MAX_QUANTITY = 10000;
	
	/** 商品 */
	private Product product;

	/** 购物车 */
	private Cart cart;
	
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public Product getProduct() {
		if (ObjectUtils.isEmpty(product)) {
			product = Product.dao.findById(getProductId());
		}
		return product;
	}

	/**
	 * 设置商品
	 * 
	 * @param product
	 *            商品
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * 获取购物车
	 * 
	 * @return 购物车
	 */
	public Cart getCart() {
		if (ObjectUtils.isEmpty(cart)) {
			cart = Cart.dao.findById(getCartId());
		}
		return cart;
	}

	/**
	 * 设置购物车
	 * 
	 * @param cart
	 *            购物车
	 */
	public void setCart(Cart cart) {
		this.cart = cart;
	}

	/**
	 * 获取商品重量
	 * 
	 * @return 商品重量
	 */
	public int getWeight() {
		if (getProduct() != null && getProduct().getWeight() != null && getQuantity() != null) {
			return getProduct().getWeight() * getQuantity();
		} else {
			return 0;
		}
	}

	/**
	 * 获取赠送积分
	 * 
	 * @return 赠送积分
	 */
	public long getRewardPoint() {
		if (getProduct() != null && getProduct().getRewardPoint() != null && getQuantity() != null) {
			return getProduct().getRewardPoint() * getQuantity();
		} else {
			return 0L;
		}
	}

	/**
	 * 获取兑换积分
	 * 
	 * @return 兑换积分
	 */
	public long getExchangePoint() {
		if (getProduct() != null && getProduct().getExchangePoint() != null && getQuantity() != null) {
			return getProduct().getExchangePoint() * getQuantity();
		} else {
			return 0L;
		}
	}

	/**
	 * 获取价格
	 * 
	 * @return 价格
	 */
	public BigDecimal getPrice() {
		if (getProduct() != null && getProduct().getPrice() != null) {
			Setting setting = SystemUtils.getSetting();
			if (getCart() != null && getCart().getMember() != null && getCart().getMember().getMemberRank() != null) {
				MemberRank memberRank = getCart().getMember().getMemberRank();
				if (memberRank.getScale() != null) {
					return setting.setScale(getProduct().getPrice().multiply(new BigDecimal(String.valueOf(memberRank.getScale()))));
				}
			}
			return setting.setScale(getProduct().getPrice());
		} else {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 获取小计
	 * 
	 * @return 小计
	 */
	public BigDecimal getSubtotal() {
		if (getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getQuantity()));
		} else {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 获取是否上架
	 * 
	 * @return 是否上架
	 */
	public boolean getIsMarketable() {
		return getProduct() != null && getProduct().getIsMarketable();
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public boolean getIsDelivery() {
		return getProduct() != null && getProduct().getIsDelivery();
	}

	/**
	 * 获取是否库存不足
	 * 
	 * @return 是否库存不足
	 */
	public boolean getIsLowStock() {
		return getQuantity() != null && getProduct() != null && getQuantity() > getProduct().getAvailableStock();
	}

	/**
	 * 增加商品数量
	 * 
	 * @param quantity
	 *            数量
	 */
	public void add(int quantity) {
		if (quantity < 1) {
			return;
		}
		if (getQuantity() != null) {
			setQuantity(getQuantity() + quantity);
		} else {
			setQuantity(quantity);
		}
	}
}
