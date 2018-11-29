package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseArticleCategory;
import com.jfinalshop.util.ObjectUtils;

/**
 * Model - 文章分类
 * 
 * 
 */
public class ArticleCategory extends BaseArticleCategory<ArticleCategory> {
	private static final long serialVersionUID = 6645880730768361539L;
	public static final ArticleCategory dao = new ArticleCategory();
	
	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/** 路径前缀 */
	private static final String PATH_PREFIX = "/article/list";
	
	/** 路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";

	/** 上级分类 */
	private ArticleCategory parent;
	
	/** 下级分类 */
	private List<ArticleCategory> childrens = new ArrayList<ArticleCategory>();

	/** 文章 */
	private List<Article> articles = new ArrayList<Article>();
	
	/**
	 * 获取上级分类
	 * 
	 * @return 上级分类
	 */
	public ArticleCategory getParent() {
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
	public void setParent(ArticleCategory parent) {
		this.parent = parent;
	}
	
	/**
	 * 获取下级分类
	 * 
	 * @return 下级分类
	 */
	public List<ArticleCategory> getChildren() {
		if (CollectionUtils.isEmpty(childrens)) {
			String sql ="SELECT * FROM article_category WHERE `parent_id` = ?";
			childrens = ArticleCategory.dao.find(sql, getId());
		}
		return childrens;
	}

	/**
	 * 设置下级分类
	 * 
	 * @param children
	 *            下级分类
	 */
	public void setChildren(List<ArticleCategory> children) {
		this.childrens = children;
	}

	/**
	 * 获取文章
	 * 
	 * @return 文章
	 */
	public List<Article> getArticles() {
		String sql = "SELECT * FROM article WHERE `article_category_id` = ?";
		if (CollectionUtils.isEmpty(articles)) {
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
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return getId() != null ? PATH_PREFIX + "/" + getId() + PATH_SUFFIX : null;
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
	public List<ArticleCategory> getParents() {
		List<ArticleCategory> parents = new ArrayList<ArticleCategory>();
		recursiveParents(parents, this);
		return parents;
	}

	/**
	 * 递归上级分类
	 * 
	 * @param parents
	 *            上级分类
	 * @param articleCategory
	 *            文章分类
	 */
	private void recursiveParents(List<ArticleCategory> parents, ArticleCategory articleCategory) {
		if (articleCategory == null) {
			return;
		}
		ArticleCategory parent = findById(articleCategory.getParent());
		if (parent != null) {
			parents.add(0, parent);
			recursiveParents(parents, parent);
		}
	}
}
