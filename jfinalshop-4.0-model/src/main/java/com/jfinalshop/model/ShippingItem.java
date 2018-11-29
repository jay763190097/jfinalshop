package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseShippingItem;
import com.jfinalshop.util.JsonUtils;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 发货项
 * 
 * 
 */
public class ShippingItem extends BaseShippingItem<ShippingItem> {
	private static final long serialVersionUID = -2478725586166536631L;
	public static final ShippingItem dao = new ShippingItem();
	
	/** 商品 */
	private Product product;

	/** 发货单 */
	private Shipping shipping;
	
	/** 规格 */
	private List<String> specifications = new ArrayList<String>();
	
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
	 * 获取发货单
	 * 
	 * @return 发货单
	 */
	public Shipping getShipping() {
		if (ObjectUtils.isEmpty(product)) {
			shipping = Shipping.dao.findById(getShippingId());
		}
		return shipping;
	}

	/**
	 * 设置发货单
	 * 
	 * @param shipping
	 *            发货单
	 */
	public void setShipping(Shipping shipping) {
		this.shipping = shipping;
	}
	
	/**
	 * 获取规格
	 * 
	 * @return 规格
	 */
	public List<String> getSpecificationConverter() {
		if (CollectionUtils.isEmpty(specifications)) {
			specifications = JsonUtils.convertJsonStrToList(getSpecifications());
		}
		return specifications;
	}

	/**
	 * 设置规格
	 * 
	 * @param specifications
	 *            规格
	 */
	public void setSpecificationConverter(List<String> specifications) {
		this.specifications = specifications;
	}


}
