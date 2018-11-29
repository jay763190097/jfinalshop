package com.jfinalshop.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.ArticleCategoryDao;
import com.jfinalshop.dao.ArticleDao;
import com.jfinalshop.dao.TagDao;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.model.ArticleTag;
import com.jfinalshop.model.Tag;
import com.jfinalshop.util.Assert;

/**
 * Service - 文章
 * 
 * 
 */
@Singleton
public class ArticleService extends BaseService<Article> {

	/**
	 * 构造方法
	 */
	public ArticleService() {
		super(Article.class);
	}
	
	private CacheManager cacheManager = CacheKit.getCacheManager();
	@Inject
	private ArticleDao articleDao;
	@Inject
	private ArticleCategoryDao articleCategoryDao;
	@Inject
	private TagDao tagDao;
	@Inject
	private StaticService staticService;
	
	
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
		return articleDao.findList(articleCategory, tag, isPublication, count, filters, orders);
	}


	/**
	 * 查找文章
	 * 
	 * @param articleCategoryId
	 *            文章分类ID
	 * @param tagId
	 *            标签ID
	 * @param isPublication
	 *            是否发布
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 文章
	 */
	public List<Article> findList(Long articleCategoryId, Long tagId, Boolean isPublication, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		ArticleCategory articleCategory = articleCategoryDao.find(articleCategoryId);
		if (articleCategoryId != null && articleCategory == null) {
			return Collections.emptyList();
		}
		Tag tag = tagDao.find(tagId);
		if (tagId != null && tag == null) {
			return Collections.emptyList();
		}
		return articleDao.findList(articleCategory, tag, isPublication, count, filters, orders);
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
		return articleDao.findList(articleCategory, isPublication, generateMethod, beginDate, endDate, first, count);
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
		return articleDao.findPage(articleCategory, tag, isPublication, pageable);
	}

	/**
	 * 查看点击数
	 * 
	 * @param id
	 *            ID
	 * @return 点击数
	 */
	public long viewHits(Long id) {
		Assert.notNull(id);

		Ehcache cache = cacheManager.getEhcache(Article.HITS_CACHE_NAME);
		Element element = cache.get(id);
		Long hits;
		if (element != null) {
			hits = (Long) element.getObjectValue() + 1;
		} else {
			Article article = articleDao.find(id);
			if (article == null) {
				return 0L;
			}
			hits = article.getHits() + 1;
		}
		cache.put(new Element(id, hits));
		return hits;
	}

	/**
	 * 保存
	 * 
	 */
	public Article save(Article article) {
		Assert.notNull(article);

		article.setGenerateMethod(Article.GenerateMethod.eager.ordinal());
		super.save(article);
		
		List<Tag> tags = article.getTags();
		if (CollectionUtils.isNotEmpty(tags)) {
			for(Tag tag : tags) {
				ArticleTag articleTag = new ArticleTag();
				articleTag.setTags(tag.getId());
				articleTag.setArticles(article.getId());
				articleTag.save();
			}
		}
		return article;
	}

	/**
	 * 更新
	 * 
	 */
	public Article update(Article article) {
		Assert.notNull(article);

		article.setGenerateMethod(Article.GenerateMethod.eager.ordinal());
		super.update(article);
		
		List<Tag> tags = article.getTags();
		if (CollectionUtils.isNotEmpty(tags)) {
			ArticleTag.dao.deleteArticleTag(article.getId());
			for(Tag tag : tags) {
				ArticleTag articleTag = new ArticleTag();
				articleTag.setTags(tag.getId());
				articleTag.setArticles(article.getId());
				articleTag.save();
			}
		}
		return article;
	}

	public void delete(Long id) {
		super.delete(id);
	}

	public void delete(Long... ids) {
		super.delete(ids);
	}

	public void delete(Article article) {
		staticService.delete(article);
		ArticleTag.dao.deleteArticleTag(article.getId());
		super.delete(article);
	}
}