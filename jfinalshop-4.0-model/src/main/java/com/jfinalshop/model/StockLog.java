package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseStockLog;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 库存记录
 * 
 * 
 */
public class StockLog extends BaseStockLog<StockLog> {
	private static final long serialVersionUID = -9125794153277317852L;
	public static final StockLog dao = new StockLog();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 入库 */
		stockIn,

		/** 出库 */
		stockOut
	}
	
	/** 商品 */
	private Product product;
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
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
	 * 设置操作员
	 * 
	 * @param operator
	 *            操作员
	 */
	public void setOperator(Admin operator) {
		setOperator(operator != null ? operator.getUsername() : null);
	}
}
