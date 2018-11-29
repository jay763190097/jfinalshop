package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseProductCategory;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 商品分类
 * 
 * 
 */
public class ProductCategory extends BaseProductCategory<ProductCategory> {
	private static final long serialVersionUID = -2936605043952329686L;
	public static final ProductCategory dao = new ProductCategory();
	
	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/** 路径前缀 */
	private static final String PATH_PREFIX = "/goods/list";
	
	/** wap路径前缀 */
	private static final String WAP_PATH_PREFIX = "/wap/goods/list";
	
	/** 路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";

	/** 上级分类 */
	private ProductCategory parent;

	/** 下级分类 */
	private List<ProductCategory> children = new ArrayList<ProductCategory>();

	/** 货品 */
	private List<Goods> goods = new ArrayList<Goods>();

	/** 关联品牌 */
	private List<Brand> brands = new ArrayList<Brand>();

	/** 关联促销 */
	private List<Promotion> promotions = new ArrayList<Promotion>();

	/** 参数 */
	private List<Parameter> parameters = new ArrayList<Parameter>();

	/** 属性 */
	private List<Attribute> attributes = new ArrayList<Attribute>();

	/** 规格 */
	private List<Specification> specifications = new ArrayList<Specification>();

	/**
	 * 获取上级分类
	 * 
	 * @return 上级分类
	 */
	public ProductCategory getParent() {
		if (ObjectUtils.isEmpty(parent)) {
			parent = findById(getParentId());
		}
		return parent;
	}

	/**
	 * 设置上级分类
	 * 
	 * @param parent
	 *            上级分类
	 */
	public void setParent(ProductCategory parent) {
		this.parent = parent;
	}
	
	/**
	 * 获取下级分类
	 * 
	 * @return 下级分类
	 */
	public List<ProductCategory> getChildren() {
		if (CollectionUtils.isEmpty(children)) {
			String sql = "SELECT * FROM `product_category` WHERE parent_id = ? ORDER BY `orders` ASC";
			children = ProductCategory.dao.find(sql, getId());
		}
		return children;
	}

	/**
	 * 设置下级分类
	 * 
	 * @param children
	 *            下级分类
	 */
	public void setChildren(List<ProductCategory> children) {
		this.children = children;
	}

	/**
	 * 获取货品
	 * 
	 * @return 货品
	 */
	public List<Goods> getGoods() {
		if (CollectionUtils.isEmpty(goods)) {
			String sql = "SELECT * FROM `goods` WHERE product_category_id = ?";
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
	 * 获取关联品牌
	 * 
	 * @return 关联品牌
	 */
	public List<Brand> getBrands() {
		if (CollectionUtils.isEmpty(brands)) {
			String sql = "SELECT b.* FROM `product_category_brand` pcb LEFT JOIN `brand` b ON pcb.`brands` = b.`id` WHERE pcb.`product_categories` = ? ORDER BY `orders` ASC";
			brands = Brand.dao.find(sql, getId());
		}
		return brands;
	}

	/**
	 * 设置关联品牌
	 * 
	 * @param brands
	 *            关联品牌
	 */
	public void setBrands(List<Brand> brands) {
		this.brands = brands;
	}

	/**
	 * 获取关联促销
	 * 
	 * @return 关联促销
	 */
	public List<Promotion> getPromotions() {
		if (CollectionUtils.isEmpty(promotions)) {
			String sql = "SELECT p.* FROM `product_category_promotion` pcp LEFT JOIN `promotion` p ON pcp.`promotions` = p.`id` WHERE pcp.`product_categories` = ? ORDER BY `orders` ASC";
			promotions = Promotion.dao.find(sql, getId());
		}
		return promotions;
	}

	/**
	 * 设置关联促销
	 * 
	 * @param promotions
	 *            关联促销
	 */
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 获取参数
	 * 
	 * @return 参数
	 */
	public List<Parameter> getParameters() {
		if (CollectionUtils.isEmpty(promotions)) {
			String sql = "SELECT * FROM `parameter` WHERE product_category_id = ? ORDER BY `orders` ASC";
			parameters = Parameter.dao.find(sql, getId());
		}
		return parameters;
	}

	/**
	 * 设置参数
	 * 
	 * @param parameters
	 *            参数
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * 获取属性
	 * 
	 * @return 属性
	 */
	public List<Attribute> getAttributes() {
		if (CollectionUtils.isEmpty(attributes)) {
			String sql = "SELECT * FROM `attribute` WHERE product_category_id = ? ORDER BY orders ASC";
			attributes = Attribute.dao.find(sql, getId());
		}
		return attributes;
	}

	/**
	 * 设置属性
	 * 
	 * @param attributes
	 *            属性
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * 获取规格
	 * 
	 * @return 规格
	 */
	public List<Specification> getSpecifications() {
		if (CollectionUtils.isEmpty(specifications)) {
			String sql = "SELECT * FROM `specification` WHERE product_category_id = ? ORDER BY `orders` ASC";
			specifications = Specification.dao.find(sql, getId());
		}
		return specifications;
	}

	/**
	 * 设置规格
	 * 
	 * @param specifications
	 *            规格
	 */
	public void setSpecifications(List<Specification> specifications) {
		this.specifications = specifications;
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
	 * wap获取路径
	 * 
	 * @return 路径
	 */
	public String getWapPath() {
		return getId() != null ? WAP_PATH_PREFIX + "/" + getId() + PATH_SUFFIX : null;
	}

	/**
	 * 获取所有上级分类ID
	 * 
	 * @return 所有上级分类ID
	 */
	public Long[] getParentIds() {
		String[] parentIds = StringUtils.split(getTreePath(), TREE_PATH_SEPARATOR);
		Long[] result = new Long[parentIds.length];
		for (int i = 0; i < parentIds.length; i++) {
			result[i] = Long.valueOf(parentIds[i]);
		}
		return result;
	}

	/**
	 * 获取所有上级分类
	 * 
	 * @return 所有上级分类
	 */
	public List<ProductCategory> getParents() {
		List<ProductCategory> parents = new ArrayList<ProductCategory>();
		recursiveParents(parents, this);
		return parents;
	}

	/**
	 * 递归上级分类
	 * 
	 * @param parents
	 *            上级分类
	 * @param productCategory
	 *            商品分类
	 */
	private void recursiveParents(List<ProductCategory> parents, ProductCategory productCategory) {
		if (productCategory == null) {
			return;
		}
		ProductCategory parent = findById(productCategory.getParent());
		if (parent != null) {
			parents.add(0, parent);
			recursiveParents(parents, parent);
		}
	}

}
