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
	private final static Integer HOT_NUM = 10;
	/** 分类下商品数量 */
	private final static Integer PRODUCTCATEGORY_COUNT_NUM = 5;
	/** 分类层级 */
	private final static Integer GRADE  = 1;
  
	static{
		/*String generateTypeName = "goods";
		GenerateType generateType = StrKit.notBlank(generateTypeName) ? GenerateType.valueOf(generateTypeName) : null;
		Boolean isPurge = false;
		Integer first = 0;
		Integer count = 100;
		
		long startTime = System.currentTimeMillis();
		if (first == null || first < 0) {
			first = 0;
		}
		if (count == null || count <= 0) {
			count = 100;
		}
		int generateCount = 0;
		boolean isCompleted = true;
		List<Filter> filters = new ArrayList<Filter>();
		switch (generateType) {
		case article:
			if (first == 0 && isPurge != null && isPurge) {
				searchService.delArticleAll();
			}
			filters.add(Filter.eq("is_publication", true));
			List<Article> articleList = articleService.findList(first, count, null, null);
			generateCount = searchService.indexArticle(articleList);
			first += articleList.size();
			if (articleList.size() == count) {
				isCompleted = false;
			}
			break;
		case goods:
			if (first == 0 && isPurge != null && isPurge) {
				searchService.delGoodsAll();
			}
			filters.add(Filter.eq("is_marketable", true));
			filters.add(Filter.eq("is_list", true));
			List<Goods> productList = goodsService.findList(first, count, filters, null);
			generateCount = searchService.indexGoods(productList);
			first += productList.size();
			if (productList.size() == count) {
				isCompleted = false;
			}
			break;
		}
		long endTime = System.currentTimeMillis();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("first", first);
		data.put("generateCount", generateCount);
		data.put("generateTime", endTime - startTime);
		data.put("isCompleted", isCompleted);
		logger.info("加载索引数据"+data);*/
		System.out.print("静态代码块！-->");
		}
	/**
	 * 首页 - 轮播广告
	 */
	//@Before(CacheInterceptor.class) 
	public void ad() {
		AdPosition adPosition = adPositionService.find(1L);
		DataResponse dataResponse = new DataResponse(adPosition.getAds());
		renderJson(dataResponse);
	}
	
	
	/**
	 * 首页 - 分类
	 */
	public void productCategory() {
		DataResponse datumResponse = new DataResponse();
		String productCategoryJSONData = productCategoryService.createJSONData(GRADE, PRODUCTCATEGORY_COUNT_NUM);
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
		List<Goods> goodsList = goodsService.findList(null, null, null, null, null, null, null, null, true, true, null, null, null, null, Goods.OrderType.salesDesc, HOT_NUM, null, null, false);
		renderJson(new DataResponse(convertGoods(goodsList)));
	}

	public void index(){
		System.out.println("jsajsajklsaj");
		System.out.println("llllllllllllll");
	}
}
