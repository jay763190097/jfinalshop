package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseProductNotify;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 到货通知
 * 
 * 
 */
public class ProductNotify extends BaseProductNotify<ProductNotify> {
	private static final long serialVersionUID = -6220032110888351694L;
	public static final ProductNotify dao = new ProductNotify();
	
	/** 会员 */
	private Member member;

	/** 商品 */
	private Product product;
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		if (ObjectUtils.isEmpty(member)) {
			member = Member.dao.findById(getMemberId());
		}
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

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
}
