package com.jfinalshop.api.controller;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.entity.GoodsVO;
import com.jfinalshop.service.SearchService;
import com.ld.zxw.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 搜索
 *
 */
@ControllerBind(controllerKey = "/api/search")
@Before(AccessInterceptor.class)
public class SearchAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(SearchAPIController.class);
	/**
	 * 搜索
	 */
	public void index() {
		String keyword = getPara("keyword");
		//Integer pageNumber = getParaToInt("pageNumber", 1);
		//Integer pageSize = getParaToInt("pageSize", 20);
		Pageable pageable = new Pageable();
		SearchService searchService = new SearchService();
		Page<GoodsVO> goods = searchService.search(keyword, null, null, null, pageable);
		//convertGoods(goods.getList());
		renderJson(new DataResponse(goods.getList()));
	}
	
	/**
	 * 获取热门搜索关键词
	 */
	public void hotSearch() {
		String[] hotSearches = setting.getHotSearches();
		renderJson(new DatumResponse(hotSearches));
	}
	
}
