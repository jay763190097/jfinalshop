package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseOrderItem;
import com.jfinalshop.util.JsonUtils;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 订单项
 * 
 * 
 */
public class OrderItem extends BaseOrderItem<OrderItem> {
	private static final long serialVersionUID = -3304468291946909927L;
	public static final OrderItem dao = new OrderItem();
	
	/** 商品 */
	private Product product;

	/** 订单 */
	private Order order;
	
	/** 规格 */
	private List<String> specifications = new ArrayList<String>();
	
	/**
	 * 获取商品类型
	 * 
	 * @return 商品类型
	 */
	public Goods.Type getTypeName() {
		return Goods.Type.values()[getType()];
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

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		if (ObjectUtils.isEmpty(order)) {
			order = Order.dao.findById(getOrderId());
		}
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param order
	 *            订单
	 */
	public void setOrder(Order order) {
		this.order = order;
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

	/**
	 * 获取商品总重量
	 * 
	 * @return 商品总重量
	 */
	public int getTotalWeight() {
		if (getWeight() != null && getQuantity() != null) {
			return getWeight() * getQuantity();
		} else {
			return 0;
		}
	}

	/**
	 * 获取小计
	 * 
	 * @return 小计
	 */
	public BigDecimal getSubtotal() {
		if (getPrice() != null && getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getQuantity()));
		} else {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 获取可发货数
	 * 
	 * @return 可发货数
	 */
	public int getShippableQuantity() {
		int shippableQuantity = getQuantity() - getShippedQuantity();
		return shippableQuantity >= 0 ? shippableQuantity : 0;
	}

	/**
	 * 获取可退货数
	 * 
	 * @return 可退货数
	 */
	public int getReturnableQuantity() {
		int returnableQuantity = getShippedQuantity() - getReturnedQuantity();
		return returnableQuantity >= 0 ? returnableQuantity : 0;
	}

	/**
	 * 删除
	 * @param orderId
	 * @return
	 */
	public boolean deleteByOrderId(Long orderId) {
		return Db.deleteById("order_item", "order_id", orderId);
	}
	
}
