package com.jfinalshop.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.util.SqlUtils;

/**
 * Dao - 文章分类
 * 
 * 
 */
public class ArticleCategoryDao extends OrderEntity<ArticleCategory> {
	
	/**
	 * 构造方法
	 */
	public ArticleCategoryDao() {
		super(ArticleCategory.class);
	}

	/**
	 * 查找顶级文章分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级文章分类
	 */
	public List<ArticleCategory> findRoots(Integer count) {
		String sql = "SELECT * FROM article_category WHERE parent_id IS NULL ORDER BY orders ASC ";
		if (count != null) {
			sql += "LIMIT 0, " + count;
		}
		return modelManager.find(sql);
	}

	/**
	 * 查找上级文章分类
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级文章分类
	 */
	public List<ArticleCategory> findParents(ArticleCategory articleCategory, boolean recursive, Integer count) {
		if (articleCategory == null || articleCategory.getParent() == null) {
			return Collections.emptyList();
		}
		String sql = "";
		if (recursive) {
			List<Long> ids = Arrays.asList(articleCategory.getParentIds());
			sql = "SELECT * FROM article_category WHERE id IN " + SqlUtils.getSQLIn(ids) + " ORDER BY grade ASC ";
		} else {
			sql = "SELECT * FROM article_category WHERE id = " + articleCategory.getParentId();
		}
		if (count != null) {
			sql += " LIMIT 0, " + count;
		}
		return modelManager.find(sql);
	}

	/**
	 * 查找下级文章分类
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级文章分类
	 */
	public List<ArticleCategory> findChildren(ArticleCategory articleCategory, boolean recursive, Integer count) {
		String sql = "";
		if (recursive) {
			if (articleCategory != null) {
				sql = "SELECT * FROM article_category WHERE tree_path LIKE '%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%' ORDER BY grade ASC, orders ASC ";
			} else {
				sql = "SELECT * FROM article_category ORDER BY grade ASC, orders ASC ";
			}
			if (count != null) {
				sql += "LIMIT 0, " + count;
			}
			List<ArticleCategory> result = modelManager.find(sql);
			sort(result);
			return result;
		} else {
			sql = "SELECT * FROM article_category WHERE parent_id = ? ORDER by orders ASC ";
			if (count != null) {
				sql += "LIMIT 0, " + count;
			}
			return modelManager.find(sql, articleCategory.getParentId());
		}
	}

	/**
	 * 排序文章分类
	 * 
	 * @param articleCategories
	 *            文章分类
	 */
	private void sort(List<ArticleCategory> articleCategories) {
		if (CollectionUtils.isEmpty(articleCategories)) {
			return;
		}
		final Map<Long, Integer> orderMap = new HashMap<Long, Integer>();
		for (ArticleCategory articleCategory : articleCategories) {
			orderMap.put(articleCategory.getId(), articleCategory.getOrders());
		}
		Collections.sort(articleCategories, new Comparator<ArticleCategory>() {
			@Override
			public int compare(ArticleCategory articleCategory1, ArticleCategory articleCategory2) {
				Long[] ids1 = (Long[]) ArrayUtils.add(articleCategory1.getParentIds(), articleCategory1.getId());
				Long[] ids2 = (Long[]) ArrayUtils.add(articleCategory2.getParentIds(), articleCategory2.getId());
				Iterator<Long> iterator1 = Arrays.asList(ids1).iterator();
				Iterator<Long> iterator2 = Arrays.asList(ids2).iterator();
				CompareToBuilder compareToBuilder = new CompareToBuilder();
				while (iterator1.hasNext() && iterator2.hasNext()) {
					Long id1 = iterator1.next();
					Long id2 = iterator2.next();
					Integer order1 = orderMap.get(id1);
					Integer order2 = orderMap.get(id2);
					compareToBuilder.append(order1, order2).append(id1, id2);
					if (!iterator1.hasNext() || !iterator2.hasNext()) {
						compareToBuilder.append(articleCategory1.getGrade(), articleCategory2.getGrade());
					}
				}
				return compareToBuilder.toComparison();
			}
		});
	}
}