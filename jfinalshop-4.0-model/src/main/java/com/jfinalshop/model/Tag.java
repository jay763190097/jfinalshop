package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseTag;

/**
 * Model - 标签
 * 
 * 
 */
public class Tag extends BaseTag<Tag> {
	private static final long serialVersionUID = 2987044000199029506L;
	public static final Tag dao = new Tag();
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 文章标签 */
		article,

		/** 商品标签 */
		goods
	}
	
	/** 文章 */
	private List<Article> articles = new ArrayList<Article>();

	/** 货品 */
	private List<Goods> goods = new ArrayList<Goods>();
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
	}
	
	/**
	 * 获取文章
	 * 
	 * @return 文章
	 */
	public List<Article> getArticles() {
		if (CollectionUtils.isEmpty(articles)) {
			String sql = "SELECT a.* FROM `article_tag` at LEFT JOIN `article` a ON at.`articles` = a.`id` WHERE at.`tags` = ?";
			articles = Article.dao.find(sql, getId());
		}
		return articles;
	}

	/**
	 * 设置文章
	 * 
	 * @param articles
	 *            文章
	 */
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	/**
	 * 获取货品
	 * 
	 * @return 货品
	 */
	public List<Goods> getGoods() {
		if (CollectionUtils.isEmpty(goods)) {
			String sql = "SELECT g.* FROM `goods_tag` gt LEFT JOIN `goods` g ON gt.`goods` = g.`id` WHERE gt.`tags` = ?";
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
	 * 删除前处理
	 */
	public void preRemove() {
		List<Article> articles = getArticles();
		if (articles != null) {
			for (Article article : articles) {
				article.getTags().remove(this);
			}
		}
		List<Goods> goodsList = getGoods();
		if (goodsList != null) {
			for (Goods goods : goodsList) {
				goods.getTags().remove(this);
			}
		}
	}
}
