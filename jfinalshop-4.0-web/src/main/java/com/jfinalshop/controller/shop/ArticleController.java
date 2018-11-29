package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.SearchService;

/**
 * Controller - 文章
 * 
 * 
 */
@ControllerBind(controllerKey = "/article")
@Before(ThemeInterceptor.class)
public class ArticleController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 20;

	@Inject
	private ArticleService articleService;
	@Inject
	private ArticleCategoryService articleCategoryService;
	@Inject
	private SearchService searchService;

	/**
	 * 列表
	 */
	public void list() {
		Long id = getParaToLong(0);
		Integer pageNumber = getParaToInt("pageNumber");
		ArticleCategory articleCategory = articleCategoryService.find(id);
		if (articleCategory == null) {
			throw new ResourceNotFoundException();
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("articleCategory", articleCategory);
		setAttr("page", articleService.findPage(articleCategory, null, true, pageable));
		render("/shop/${theme}/article/list.ftl");
	}

	/**
	 * 搜索
	 */
	public void search() {
		String keyword = getPara("keyword"); 
		
		Integer pageNumber = getParaToInt("pageNumber");
		if (StringUtils.isEmpty(keyword)) {
			redirect(ERROR_VIEW);
			return;
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("articleKeyword", keyword);
		setAttr("page", searchService.search(keyword, pageable));
		render("/shop/${theme}/article/search.ftl");
	}

	/**
	 * 点击数
	 */
	public void hits() {
		Long id = getParaToLong(0);
		renderJson(articleService.viewHits(id));
	}

}