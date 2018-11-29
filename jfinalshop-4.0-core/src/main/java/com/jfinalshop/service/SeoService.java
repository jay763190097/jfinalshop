package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.SeoDao;
import com.jfinalshop.model.Seo;

/**
 * Service - SEO设置
 * 
 * 
 */
@Singleton
public class SeoService extends BaseService<Seo> {

	/**
	 * 构造方法
	 */
	public SeoService() {
		super(Seo.class);
	}
	
	@Inject
	private SeoDao seoDao;
	
	/**
	 * 查找SEO设置
	 * 
	 * @param type
	 *            类型
	 * @return SEO设置
	 */
	public Seo find(Seo.Type type) {
		return seoDao.find(type);
	}

	/**
	 * 查找SEO设置
	 * 
	 * @param type
	 *            类型
	 * @param useCache
	 *            是否使用缓存
	 * @return SEO设置
	 */
	public Seo find(Seo.Type type, boolean useCache) {
		return seoDao.find(type);
	}

	public Seo save(Seo seo) {
		return super.save(seo);
	}

	public Seo update(Seo seo) {
		return super.update(seo);
	}

//	public Seo update(Seo seo, String... ignoreProperties) {
//		return super.update(seo, ignoreProperties);
//	}

	public void delete(Long id) {
		super.delete(id);
	}

	public void delete(Long... ids) {
		super.delete(ids);
	}

	public void delete(Seo seo) {
		super.delete(seo);
	}
}