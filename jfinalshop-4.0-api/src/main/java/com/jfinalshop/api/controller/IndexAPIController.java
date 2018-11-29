package com.jfinalshop.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinalshop.Filter;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Goods;
import com.jfinalshop.service.AdPositionService;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.SearchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 首页
 *
 */
@ControllerBind(controllerKey = "/api/index")
@Before(AccessInterceptor.class)
public class IndexAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(IndexAPIController.class);
	@Inject
	private ArticleService articleService;
	@Inject
	private SearchService searchService;
	@Inject
	private AdPositionService adPositionService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private GoodsService goodsService;
	/**
	 * 生成类型
	 */
	public enum GenerateType {
		/**
		 * 文章
		 */
		article,

		/**
		 * 商品
		 */
		goods
	}
	
	/** 显示热销排行数量*/
	private final static Integer HOT_NUM = 9;
	/** 分类下商品数量 */
	private final static Integer PRODUCTCATEGORY_COUNT_NUM = 5;
	/** 分类层级 */
	private final static Integer GRADE  = 1;
  
	/**
	 * 首页 - 轮播广告
	 */
	//@Before(CacheInterceptor.class) 
	public void ad() {
		String channel = getPara("channel","IOS");
		AdPosition adPosition = adPositionService.find(1L);
		DataResponse dataResponse = new DataResponse(adPosition.getAds1(channel));
		renderJson(dataResponse);
	}
	
	
	/**
	 * 首页 - 分类
	 * 分类添加渠道
	 */
	public void productCategory() {
		String channel = getPara("channel","IOS");
		DataResponse datumResponse = new DataResponse();
		String productCategoryJSONData = productCategoryService.createJSONData(GRADE,PRODUCTCATEGORY_COUNT_NUM,channel);
		JSONArray jsonArray = null;
		if (StrKit.notBlank(productCategoryJSONData)) {
			jsonArray = JSONArray.parseArray(productCategoryJSONData);
		}
		datumResponse.setData(jsonArray);
		renderJson(datumResponse);
	}
	
	/**
	 * 首页 - 热销排行
	 */
	public void hotGoods() {
		String channel = getPara("channel","IOS");
		List<Goods> goodsList = goodsService.findHot(null, null, null, null, null, null, null, null, channel, true, true, null, null, null, null, Goods.OrderType.salesDesc, HOT_NUM, null, null, false);
		renderJson(new DataResponse(convertGoods(goodsList)));
	}

	public void index(){
		System.out.println("jsajsajklsaj");
		System.out.println("llllllllllllll");
	}
}
