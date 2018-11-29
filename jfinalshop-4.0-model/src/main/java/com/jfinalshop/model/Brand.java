package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseBrand;

/**
 * Model - 品牌
 * 
 * 
 */
public class Brand extends BaseBrand<Brand> {
	private static final long serialVersionUID = 4587688878405294634L;
	public static final Brand dao = new Brand();
	
	/** 路径前缀 */
	private static final String PATH_PREFIX = "/brand/content";

	/** 路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		image
	}
	
	/** 货品 */
	private List<Goods> goods = new ArrayList<Goods>();

	/** 商品分类 */
	private List<ProductCategory> productCategories = new ArrayList<ProductCategory>();
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
	}
	
	/**
	 * 获取货品
	 * 
	 * @return 货品
	 */
	public List<Goods> getGoods() {
		if (CollectionUtils.isEmpty(goods)) {
			String sql = "SELECT * FROM `goods` WHERE `brand_id` = ? ";
			goods = Goods.dao.find(sql, getId());
		}
		return goods;
	}

	/**
	 * 设置货品
	 * 
	 * @param goods
	 *            货品
	 */
	public void setGoods(List<Goods> goods) {
		this.goods = goods;
	}

	/**
	 * 获取商品分类
	 * 
	 * @return 商品分类
	 */
	public List<ProductCategory> getProductCategories() {
		if (CollectionUtils.isEmpty(productCategories)) {
			String sql ="SELECT pc.* FROM `product_category_brand` pcb LEFT JOIN `product_category` pc ON pcb.`product_categories` = pc.`id` WHERE pcb.`brands` = ?";
			productCategories = ProductCategory.dao.find(sql, getId());
		}
		return productCategories;
	}

	/**
	 * 设置商品分类
	 * 
	 * @param productCategories
	 *            商品分类
	 */
	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}
	
	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return getId() != null ? PATH_PREFIX + "/" + getId() + PATH_SUFFIX : null;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Goods> goodsList = getGoods();
		if (goodsList != null) {
			for (Goods goods : goodsList) {
				goods.setBrand(null);
			}
		}
		List<ProductCategory> productCategories = getProductCategories();
		if (productCategories != null) {
			for (ProductCategory productCategory : productCategories) {
				productCategory.getBrands().remove(this);
			}
		}
	}
	
}
