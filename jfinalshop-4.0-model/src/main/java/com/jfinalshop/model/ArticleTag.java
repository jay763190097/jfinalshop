package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseArticleTag;

/**
 * Model - 文章标签
 * 
 * 
 * 
 */
public class ArticleTag extends BaseArticleTag<ArticleTag> {
	private static final long serialVersionUID = 7696694880068755539L;
	public static final ArticleTag dao = new ArticleTag();
	
	/**
	 * 根据文章删除
	 * @param articles
	 * @return
	 */
	public boolean deleteArticleTag(Long articles) {
		return Db.deleteById("article_tag", "articles", articles);
	}
}
