package com.jfinalshop.service;


import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.render.FreeMarkerRender;

import freemarker.template.Configuration;

/**
 * Service - 缓存
 * 
 * 
 */
@Singleton
public class CacheService {

	private CacheManager cacheManager = CacheKit.getCacheManager();
	private Configuration freeMarkerConfigurer = FreeMarkerRender.getConfiguration();
	@Inject
	private ConfigService configService;
	
	/**
	 * 获取缓存存储路径
	 * 
	 * @return 缓存存储路径
	 */
	public String getDiskStorePath() {
		return cacheManager.getConfiguration().getDiskStoreConfiguration().getPath();
	}

	/**
	 * 获取缓存数
	 * 
	 * @return 缓存数
	 */
	public int getCacheSize() {
		int cacheSize = 0;
		String[] cacheNames = cacheManager.getCacheNames();
		if (cacheNames != null) {
			for (String cacheName : cacheNames) {
				Ehcache cache = cacheManager.getEhcache(cacheName);
				if (cache != null) {
					cacheSize += cache.getSize();
				}
			}
		}
		return cacheSize;
	}


	/**
	 * 清除缓存
	 */
	public void clear() {
		String[] cacheNames = new String[] {"setting", "logConfig", "templateConfig", "pluginConfig", "messageConfig", "area", "seo", "adPosition", "memberAttribute", "navigation", "tag", "friendLink", "brand", "attribute", "article", "articleCategory", "goods", "productCategory", "review", "consultation","promotion", "shipping", "authorization"};
		freeMarkerConfigurer.clearTemplateCache();
		for (String cacheName : cacheNames) {
			CacheKit.removeAll(cacheName);
		}
		configService.init();
	}

}