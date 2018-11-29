package com.jfinalshop.api.controller;

import java.util.List;

import net.hasor.core.Inject;

import com.aliyun.common.comm.ServiceClient.Request;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Tag;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.PromotionService;
import com.jfinalshop.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 分类
 *
 */
@ControllerBind(controllerKey = "/api/productCategory")
@Before(AccessInterceptor.class)
public class ProductCategoryAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(ProductCategoryAPIController.class);
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private GoodsService goodsService;
	@Inject
	private BrandService brandService;
	@Inject
	private PromotionService promotionService;
	@Inject
	private TagService tagService;
	
	/**
	 * 查询默认1级分类
	 */
	public void findGrade() {
		String channel = getPara("channel","IOS");
		Integer grade = getParaToInt("grade", 1);
		List<ProductCategory> productCategorys = productCategoryService.findGrade(grade, true, null,channel);
		renderJson(new DatumResponse(productCategorys));
	}
	
	/**
	 * 查找下级商品分类
	 */
	public void findChildren() {
		Long id = getParaToLong("id");
		Integer count = getParaToInt("count", null);
		String channel = getPara("channel","IOS");
		ProductCategory productCategory = productCategoryService.find(id);
		if (productCategory == null) {
			renderArgumentError("商品分类不能为空!");
			return;
		}
		List<ProductCategory> productCategorys = productCategoryService.findChildrenAss(productCategory, false, count,channel);
		renderJson(new DataResponse(productCategorys));
	}
	
	/**
	 * 按分类查找商品分页
	 */
	public void findGoods() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		// 分类
		Long id = getParaToLong("id");
		ProductCategory productCategory = productCategoryService.find(id);
		if (productCategory == null) {
			renderArgumentError("商品分类不能为空!");
			return;
		}
		//渠道标识
		String channel = getPara("channel","IOS");
		// 类型
		String typeName = getPara("type");
		Goods.Type type = StrKit.notBlank(typeName) ? Goods.Type.valueOf(typeName) : null;
		// 排序类型
		String orderTypeName = getPara("orderType");
		Goods.OrderType orderType = StrKit.notBlank(orderTypeName) ? Goods.OrderType.valueOf(orderTypeName) : null;
		// 品牌
		Long brandId = getParaToLong("brandId");
		Brand brand = brandService.find(brandId);
		// 促销
		Long promotionId = getParaToLong("promotionId");
		Promotion promotion = promotionService.find(promotionId);
		// 标签
		Long tagId = getParaToLong("tagId");
		Tag tag = tagService.find(tagId);
		Pageable pageable = new Pageable(pageNumber, pageSize);
		
		Page<Goods> goods = goodsService.findPage(type,channel, productCategory, brand, promotion, tag, null, null, null, true, true, null, null, null, null, orderType, pageable);
		convertGoods(goods.getList());
		DatumResponse datumResponse = new DatumResponse();
		datumResponse.setDatum(goods);
		renderJson(datumResponse);
	}
	
	
}
