package com.jfinalshop.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Filter;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Goods;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.SearchService;

/**
 * Controller - 索引
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/index")
public class IndexController extends BaseController {

	@Inject
	private ArticleService articleService;
	@Inject
	private GoodsService goodsService;
	@Inject
	private SearchService searchService;

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

	/**
	 * 生成索引
	 */
	public void generate() {
		setAttr("generateTypes", GenerateType.values());
		render("/admin/index/generate.ftl");
	}

	/**
	 * 生成索引
	 */
	public void generateSubmit() {
		//String channel = getPara("channel","IOS");
		//String channel1 = "'"+channel +"'";
		String generateTypeName = getPara("generateType");
		GenerateType generateType = StrKit.notBlank(generateTypeName) ? GenerateType.valueOf(generateTypeName) : null;
		Boolean isPurge = getParaToBoolean("isPurge");
		Integer first = getParaToInt("first");
		Integer count = getParaToInt("count");
		
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
			//filters.add(Filter.eq("channel", channel1));
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
		renderJson(data);
	}

}