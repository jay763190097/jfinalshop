package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.model.Tag;
import com.jfinalshop.util.DateUtils;


/**
 * Dao - 文章
 * 
 * 
 */
public class ArticleDao extends BaseDao<Article> {
	
	/**
	 * 构造方法
	 */
	public ArticleDao() {
		super(Article.class);
	}
	
	/**
	 * 查找文章
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param tag
	 *            标签
	 * @param isPublication
	 *            是否发布
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 文章
	 */
	public List<Article> findList(ArticleCategory articleCategory, Tag tag, Boolean isPublication, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM article a WHERE 1 = 1 ";
		if (articleCategory != null) {
			sql += " AND EXISTS (SELECT 1 FROM article_category c WHERE a.article_category_id = c.id AND (c.`tree_path` LIKE '%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%' OR a.article_category_id = "+ articleCategory.getId() +")) ";
		}
		if (tag != null) {
			sql = "SELECT a.* FROM article_tag t  LEFT JOIN article a ON a.id = t.articles WHERE t.`tags` = " + tag.getId() + " ";
		}
		if (isPublication != null) {
			sql += "AND is_publication = " + isPublication + " ";
		}
		if (CollectionUtils.isEmpty(orders)) {
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("create_date", Order.Direction.desc));
		}
		return super.findList(sql, null, count, filters, orders);
	}

	/**
	 * 查找文章
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param isPublication
	 *            是否发布
	 * @param generateMethod
	 *            静态生成方式
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 文章
	 */
	public List<Article> findList(ArticleCategory articleCategory, Boolean isPublication, Article.GenerateMethod generateMethod, Date beginDate, Date endDate, Integer first, Integer count) {
		String sql = "SELECT * FROM article a WHERE 1 = 1 ";
		if (articleCategory != null) {
			sql = "SELECT a.* FROM article a LEFT JOIN article_category c ON a.article_category_id = c.id WHERE (c.tree_path like '%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%' OR  a.article_category_id = " + articleCategory.getId() + ") ";
		}
		if (isPublication != null) {
			sql += "AND is_publication = " + isPublication + " ";
		}
		if (generateMethod != null) {
			sql += "AND generate_method = " + generateMethod + " ";
		}
		if (beginDate != null) {
			sql += "AND a.create_date >= '" + DateUtils.formatDateTime(beginDate) + "' ";
		}
		if (endDate != null) {
			sql += "AND a.create_date <= '" + DateUtils.formatDateTime(endDate) + "' ";
		}
		return super.findList(sql, first, count, null, null);
	}

	/**
	 * 查找文章分页
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param tag
	 *            标签
	 * @param isPublication
	 *            是否发布
	 * @param pageable
	 *            分页信息
	 * @return 文章分页
	 */
	public Page<Article> findPage(ArticleCategory articleCategory, Tag tag, Boolean isPublication, Pageable pageable) {
		String sqlExceptSelect = "FROM article a WHERE 1 = 1 ";
		if (articleCategory != null) {
			sqlExceptSelect = "FROM article a LEFT JOIN article_category c ON a.article_category_id = c.id WHERE (c.tree_path like '%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%' OR a.article_category_id = " + articleCategory.getId() + ") ";
		}
		if (tag != null) {
			sqlExceptSelect = "FROM article_tag t  LEFT JOIN article a ON a.id = t.articles WHERE t.`tags` = " + tag.getId() + " ";
		}
		if (isPublication != null) {
			sqlExceptSelect += "AND is_publication = " + isPublication + " ";
		}
		if (pageable == null || ((StringUtils.isEmpty(pageable.getOrderProperty()) || pageable.getOrderDirection() == null) && CollectionUtils.isEmpty(pageable.getOrders()))) {
			List<Order> orders = new ArrayList<Order>();
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("a.create_date", Order.Direction.desc));
			pageable.setOrders(orders);
		}
		return super.findPage(sqlExceptSelect, pageable);
	}

}