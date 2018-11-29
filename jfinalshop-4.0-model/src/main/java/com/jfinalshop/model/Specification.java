package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseSpecification;
import com.jfinalshop.util.JsonUtils;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 规格
 * 
 * 
 */
public class Specification extends BaseSpecification<Specification> {
	private static final long serialVersionUID = -6565170866461711421L;
	public static final Specification dao = new Specification();
	
	/** 绑定分类 */
	private ProductCategory productCategory;
	
	/** 可选项 */
	private List<String> options = new ArrayList<String>();
	
	/**
	 * 获取绑定分类
	 * 
	 * @return 绑定分类
	 */
	public ProductCategory getProductCategory() {
		if (ObjectUtils.isEmpty(productCategory)) {
			productCategory = ProductCategory.dao.findById(getProductCategoryId());
		}
		return productCategory;
	}

	/**
	 * 设置绑定分类
	 * 
	 * @param productCategory
	 *            绑定分类
	 */
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * 获取可选项
	 * 
	 * @return 可选项
	 */
	public List<String> getOptionsConverter() {
		if (CollectionUtils.isEmpty(options)) {
			options = JsonUtils.convertJsonStrToList(getOptions());
		}
		return options;
	}

	/**
	 * 设置可选项
	 * 
	 * @param options
	 *            可选项
	 */
	public void setOptionsConverter(List<String> options) {
		this.options = options;
	}

	
}
