package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseSeo;

/**
 * Model - SEO设置
 * 
 * 
 */
public class Seo extends BaseSeo<Seo> {
	private static final long serialVersionUID = 8405086780619131548L;
	public static final Seo dao = new Seo();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 首页 */
		index,

		/** 文章列表 */
		articleList,

		/** 文章搜索 */
		articleSearch,

		/** 文章内容 */
		articleContent,

		/** 商品列表 */
		goodsList,

		/** 商品搜索 */
		goodsSearch,

		/** 商品内容 */
		goodsContent,

		/** 品牌列表 */
		brandList,

		/** 品牌内容 */
		brandContent
	}
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
	}
	
	/**
	 * 设置页面关键词
	 * 
	 * @param keywords
	 *            页面关键词
	 */
	public void setKeywords(String keywords) {
		if (keywords != null) {
			keywords = keywords.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.setKeywords(keywords);
	}
}
