package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PropKit;
import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.RequestContextHolder;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Tag;
import com.jfinalshop.service.AttributeService;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.PromotionService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.service.SearchService;
import com.jfinalshop.service.TagService;

/**
 * Controller - 货品
 * 
 */
@ControllerBind(controllerKey = "/goods")
@Before(ThemeInterceptor.class)
public class GoodsController extends BaseController {
	@Inject
	private ConsultationService consultationService;
	@Inject
	private MemberService memberService;
	@Inject
	private ReviewService reviewService;

	/** 最大对比货品数 */
	public static final Integer MAX_COMPARE_GOODS_COUNT = 4;

	/** 最大浏览记录货品数 */
	public static final Integer MAX_HISTORY_GOODS_COUNT = 10;

	@Inject
	private GoodsService goodsService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private BrandService brandService;
	@Inject
	private PromotionService promotionService;
	@Inject
	private TagService tagService;
	@Inject
	private AttributeService attributeService;
	@Inject
	private SearchService searchService;

	/**
	 * 对比栏
	 */
	public void compareBar() {
		Long[] goodsIds = getParaValuesToLong("ids");
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (ArrayUtils.isEmpty(goodsIds) || goodsIds.length > MAX_COMPARE_GOODS_COUNT) {
			renderJson(data);
			return;
		}

		List<Goods> goodsList = goodsService.findList(goodsIds);
		for (Goods goods : goodsList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", goods.getId());
			item.put("name", goods.getName());
			item.put("price", goods.getPrice());
			item.put("marketPrice", goods.getMarketPrice());
			item.put("thumbnail", goods.getThumbnail());
			item.put("url", goods.getUrl());
			data.add(item);
		}
		renderJson(data);
	}

	/**
	 * 添加对比
	 */
	public void addCompare() {
		Long goodsId = getParaToLong("goodsId");
		Map<String, Object> data = new HashMap<String, Object>();
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}

		data.put("message", SUCCESS_MESSAGE);
		data.put("id", goods.getId());
		data.put("name", goods.getName());
		data.put("price", goods.getPrice());
		data.put("marketPrice", goods.getMarketPrice());
		data.put("thumbnail", goods.getThumbnail());
		data.put("url", goods.getUrl());
		renderJson(data);
	}

	/**
	 * 浏览记录
	 */
	public void history() {
		Long[] goodsIds = getParaValuesToLong("goodsIds");
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (ArrayUtils.isEmpty(goodsIds) || goodsIds.length > MAX_HISTORY_GOODS_COUNT) {
			renderJson(data);
			return;
		}

		List<Goods> goodsList = goodsService.findList(goodsIds);
		for (Goods goods : goodsList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("name", goods.getName());
			item.put("price", goods.getPrice());
			item.put("thumbnail", goods.getThumbnail());
			item.put("url", goods.getUrl());
			data.add(item);
		}
		renderJson(data);
	}

	/**
	 * 列表
	 */
	public void list() {
		System.out.println("进来了");
		Long productCategoryId = getParaToLong(0);
		if (productCategoryId == null) {
			brand_list();
		} else {
			goods_list();
		}
	}
	
	/**
	 * 货品列表
	 */
	private void goods_list() {
		Long productCategoryId = getParaToLong(0);
		String channel = getPara("channel", PropKit.get("channelcode"));
		String typeName = getPara("type");
		Goods.Type type = StrKit.notBlank(typeName) ? Goods.Type.valueOf(typeName) : null;
		
		String orderTypeName = getPara("orderType");
		Goods.OrderType orderType = StrKit.notBlank(orderTypeName) ? Goods.OrderType.valueOf(orderTypeName) : null;
		
		Long brandId = getParaToLong("brandId");
		Long promotionId = getParaToLong("promotionId");
		Long tagId = getParaToLong("tagId");
		
		String startPriceStr = getPara("startPrice");
		BigDecimal startPrice = null;
		if (StrKit.notBlank(startPriceStr)) {
			startPrice = new BigDecimal(startPriceStr);
		}
		
		String endPriceStr = getPara("endPrice");
		BigDecimal endPrice = null;
		if (StrKit.notBlank(endPriceStr)) {
			endPrice = new BigDecimal(endPriceStr);
		}
		
		Integer pageNumber = getParaToInt("pageNumber");
		Integer pageSize = getParaToInt("pageSize");
		
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			throw new ResourceNotFoundException();
		}

		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		Tag tag = tagService.find(tagId);
		Map<Attribute, String> attributeValueMap = new HashMap<Attribute, String>();
		List<Attribute> attributes = productCategory.getAttributes();
		if (CollectionUtils.isNotEmpty(attributes)) {
			for (Attribute attribute : attributes) {
				String value = getPara("attribute_" + attribute.getId());
				String attributeValue = attributeService.toAttributeValue(attribute, value);
				if (attributeValue != null) {
					attributeValueMap.put(attribute, attributeValue);
				}
			}
		}

		Pageable pageable = new Pageable(pageNumber, pageSize);
		setAttr("orderTypes", Goods.OrderType.values());
		setAttr("productCategory", productCategory);
		setAttr("type", type);
		setAttr("brand", brand);
		setAttr("promotion", promotion);
		setAttr("tag", tag);
		setAttr("attributeValueMap", attributeValueMap);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("pageNumber", pageNumber);
		setAttr("pageSize", pageSize);
		setAttr("page", goodsService.findPage(type,channel, productCategory, brand, promotion, tag, attributeValueMap, startPrice, endPrice, true, true, null, null, null, null, orderType, pageable));
		render("/shop/${theme}/goods/list.ftl");
	}

	/**
	 * 列表
	 */
	private void brand_list() {
		String typeName = getPara("type");
		String channel = getPara("channel",PropKit.get("channelcode"));
		Goods.Type type = StrKit.notBlank(typeName) ? Goods.Type.valueOf(typeName) : null;
		String orderTypeName = getPara("orderType");
		Goods.OrderType orderType = StrKit.notBlank(orderTypeName) ? Goods.OrderType.valueOf(orderTypeName) : null;
		Long brandId = getParaToLong("brandId");
		Long promotionId = getParaToLong("promotionId");
		Long tagId = getParaToLong("tagId");
		
		String startPriceStr = getPara("startPrice");
		BigDecimal startPrice = null;
		if (StrKit.notBlank(startPriceStr)) {
			startPrice = new BigDecimal(startPriceStr);
		}
		
		String endPriceStr = getPara("endPrice");
		BigDecimal endPrice = null;
		if (StrKit.notBlank(endPriceStr)) {
			endPrice = new BigDecimal(endPriceStr);
		}
		
		Integer pageNumber = getParaToInt("pageNumber");
		Integer pageSize = getParaToInt("pageSize");
		
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		Tag tag = tagService.find(tagId);

		Pageable pageable = new Pageable(pageNumber, pageSize);
		setAttr("orderTypes", Goods.OrderType.values());
		setAttr("type", type);
		setAttr("brand", brand);
		setAttr("promotion", promotion);
		setAttr("tag", tag);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("pageNumber", pageNumber);
		setAttr("pageSize", pageSize);
		setAttr("page", goodsService.findPage(type,channel, null, brand, promotion, tag, null, startPrice, endPrice, true, true, null, null, null, null, orderType, pageable));
		render("/shop/${theme}/goods/list.ftl");
	}
	
	/**
	 * 详情
	 */
	public void detail() {
		//Long id = getParaToLong("id");
		//Goods goods = goodsService.find(id);
		Pageable pageable = new Pageable(1, 20);
		Boolean favorite = false;
		
		/*if (goods == null) {
			redirect("/shop/index.jhtml");
			return;
		}*/
		
		RequestContextHolder.setRequestAttributes(getRequest());
		/*if (goods.getFavoriteMembers().contains(memberService.getCurrent())) {
			favorite = true;
		}*/
		//Page<Consultation> consultationPages = consultationService.findPage(null, goods, true, pageable);
		//Page<Review> reviewPages = reviewService.findPage(null, goods, null, null, pageable);
		//setAttr("goods", goods);
		setAttr("favorite", favorite);
		//setAttr("consultationPages", consultationPages);
		//setAttr("reviewPages", reviewPages);
		//setAttr("title" , goods.getName());
		render("/shop/${theme}/goods/detail.ftl");
	}
	

	/**
	 * 搜索
	 */
	public void search() {
		String keyword = getPara("keyword");
		String channel = getPara("channel",PropKit.get("channelcode"));
		System.out.println(keyword);
		//System.out.println(channel);
		String startPriceStr = getPara("startPrice");
		BigDecimal startPrice = null;
		if (StrKit.notBlank(startPriceStr)) {
			startPrice = new BigDecimal(startPriceStr);
		}
		String endPriceStr = getPara("endPrice");
		BigDecimal endPrice = null;
		if (StrKit.notBlank(endPriceStr)) {
			endPrice = new BigDecimal(endPriceStr);
		}
		String orderTypeName = getPara("orderType");
		Goods.OrderType orderType = StrKit.notBlank(orderTypeName) ? Goods.OrderType.valueOf(orderTypeName) : null;
		
		if (StringUtils.isEmpty(keyword)) {
			redirect(ERROR_VIEW);
			return;
		}
		Pageable pageable = getBean(Pageable.class);
		setAttr("orderTypes", Goods.OrderType.values());
		setAttr("goodsKeyword", keyword);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("pageable", pageable);
		//setAttr("page", searchService.search(keyword, channel, startPrice, endPrice, orderType, pageable));
		setAttr("page", searchService.search(keyword,channel,startPrice, endPrice, orderType, pageable));
		render("/shop/${theme}/goods/search.ftl");
	}

	/**
	 * 对比
	 */
	public void compare() {
		Long[] goodsIds = getParaValuesToLong("goodsIds");
		if (ArrayUtils.isEmpty(goodsIds) || goodsIds.length > MAX_COMPARE_GOODS_COUNT) {
			redirect(ERROR_VIEW);
			return;
		}

		List<Goods> goodsList = goodsService.findList(goodsIds);
		if (CollectionUtils.isEmpty(goodsList)) {
			redirect(ERROR_VIEW);
			return;
		}

		setAttr("goodsList", goodsList);
		render("/shop/${theme}/goods/compare.ftl");
	}

	/**
	 * 点击数
	 */
	public void hits() {
		Long goodsId = getParaToLong(0);
		if (goodsId == null) {
			renderJson(0L);
		}

		renderJson(goodsService.viewHits(goodsId));
	}

}