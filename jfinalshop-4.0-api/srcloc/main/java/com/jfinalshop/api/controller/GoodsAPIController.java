package com.jfinalshop.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Filter;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.entity.ParameterValue;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.model.Goods;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Specification;
import com.jfinalshop.service.GoodsService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.SpecificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 货品
 * 
 */
@ControllerBind(controllerKey = "/api/goods")
public class GoodsAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(GoodsAPIController.class);

	@Inject
	private GoodsService goodsService;
	@Inject
	private ProductService productService;
	@Inject
	private SpecificationService specificationService;
	
	private static final Integer count = 6;
	
	/**
	 * 详情展示
	 */
	public void detail() {
		Long id = getParaToLong("id");
		Product product = productService.find(id);
		
		if(product == null) {
			renderArgumentError("商品没有找到!");
			return;
		}
		Goods goods = product.getGoods();
		
		//获取图片介绍
		String introduction = goods.getIntroduction();
		logger.info("库里介绍"+introduction);
		String[] str = introduction.split("/");
    	String res = str[1];
    	String res1 = str[2];
    	String res2 = str[3];
    	String fina = "/"+res +"/"+res1+"/"+res2;
    	String fina1 = fina.substring(0,fina.length()-1);
		// 增加点击数
		goodsService.addHits(goods, 1L);
       
		ProductCategory productCategory = goods.getProductCategory();
		Goods pGoods = new Goods();
		pGoods.setId(goods.getDefaultProduct().getId());
		pGoods.setName(goods.getName());
		pGoods.setPrice(new BigDecimal(currency(goods.getPrice(), false, false)));
		pGoods.setUnit(goods.getUnit());
		pGoods.setWeight(goods.getWeight());
		pGoods.setIntroduction(fina1);
		pGoods.setAttributeValue0(goods.getAttributeValue0());
		pGoods.setAttributeValue1(goods.getAttributeValue1());
		pGoods.setAttributeValue2(goods.getAttributeValue2());
		pGoods.setAttributeValue3(goods.getAttributeValue3());
		pGoods.setAttributeValue4(goods.getAttributeValue4());
		pGoods.setAttributeValue5(goods.getAttributeValue5());
		pGoods.setAttributeValue6(goods.getAttributeValue6());
		pGoods.setAttributeValue7(goods.getAttributeValue7());
		pGoods.setAttributeValue8(goods.getAttributeValue8());
		pGoods.setAttributeValue9(goods.getAttributeValue9());
		pGoods.setAttributeValue10(goods.getAttributeValue10());
		pGoods.setAttributeValue11(goods.getAttributeValue11());
		pGoods.setAttributeValue12(goods.getAttributeValue13());
		pGoods.setAttributeValue13(goods.getAttributeValue13());
		pGoods.setAttributeValue14(goods.getAttributeValue14());
		pGoods.setAttributeValue15(goods.getAttributeValue15());
		pGoods.setAttributeValue16(goods.getAttributeValue16());
		pGoods.setAttributeValue17(goods.getAttributeValue17());
		pGoods.setAttributeValue18(goods.getAttributeValue18());
		pGoods.setAttributeValue19(goods.getAttributeValue19());
		pGoods.setCaption(goods.getCaption());
		pGoods.setHits(goods.getHits());
		pGoods.setMonthSales(goods.getMonthSales());
		pGoods.setParameterValues(goods.getParameterValues());
		pGoods.setSales(goods.getSales());
		pGoods.setScore(goods.getScore());
		pGoods.setScoreCount(goods.getScoreCount());
		pGoods.setSeoDescription(goods.getSeoDescription());
		pGoods.setKeyword(goods.getKeyword());
		pGoods.setSeoTitle(goods.getSeoTitle());
		pGoods.setSn(goods.getSn());
		pGoods.setSpecificationItemConverter(goods.getSpecificationItemsConverter());
		pGoods.setTotalScore(goods.getTotalScore());
		pGoods.setType(goods.getType());
		pGoods.setWeekHits(goods.getWeekHits());
		pGoods.setWeekSales(goods.getWeekSales());
		pGoods.setSpecificationItems(goods.getSpecificationItems());
		pGoods.setProductCategoryId(goods.getProductCategoryId());
		pGoods.setImage(null);
		
		String brand = goods.getBrand() != null ? goods.getBrand().getName() : "";
		Set<Promotion> promotions = goods.getValidPromotions() != null ? goods.getValidPromotions() : null;
		
		List<ProductImage> productImages = goods.getProductImagesConverter();
		if (CollectionUtils.isNotEmpty(productImages)) {
			pGoods.setImage(goods.getProductImagesConverter().get(0).getLarge());
		}
		//商品介绍图片、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、
		if (CollectionUtils.isNotEmpty(productImages)) {
			pGoods.setImage(goods.getProductImagesConverter().get(0).getLarge());
		}
		
		goods.clear();
		goods._setAttrs(pGoods);
		goods.put("brand", brand);
		goods.put("availableStock", product.getAvailableStock());
		goods.put("promotions", promotions);
		
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("product_category_id", productCategory.getId()));
		filters.add(Filter.ne("id", goods.getId()));
		List<Goods> recommends = goodsService.findList(0, count, filters, null);
		
		// 猜你喜欢，如果商品相关分类不够6个就增加热销商品，去掉本身。
		if (recommends.size() < count) {
			List<Filter> pFilters = new ArrayList<Filter>();
			pFilters.add(Filter.ne("id", goods.getId()));
			List<Goods> goodsList = goodsService.findList(null, null, null, null, null, null, null, null, true, true, null, null, null, null, Goods.OrderType.salesDesc, count, pFilters, null, false);
			List<Goods> pRecommends = new ArrayList<Goods>();
			if (CollectionUtils.isNotEmpty(goodsList)) {
				for (int i = 0; i < count - recommends.size(); i++) {
					pRecommends.add(goodsList.get(i));
				}
			}
			recommends.addAll(pRecommends);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("goods", goods);
		map.put("recommends", convertGoods(recommends));
		renderJson(new DatumResponse(map));
	}
	
	/**
	 * 规格选择
	 */
	
	public void specificationsChoose(){
		Long categoryId = getParaToLong("categoryId");
		String sImage = getPara("sImage");
		Long goodsId = getParaToLong("goodsId");
		//List<Product> product = productService.findSpecifications(goodsId);
		//Product as = productService.findByGoodsId(goodsId);
		//List<Product> product = productService.findSpecifications(goodsId);
		Specification s = specificationService.find(categoryId);
		List<String> pRecommends = new ArrayList<String>();
		//List<ParameterValue> parameterValues = new ArrayList<ParameterValue>();
		Specification specification = new Specification();
		Goods goods = new Goods();
		specification.setName(s.getName());
		//String jsonString = s.getOptions().replace("[", "{");
		//String jsonString1 = jsonString.replace("]", "}");
		//logger.info("格式检查"+jsonString1);
		/*JSONObject json = new JSONObject();
		json=JSONObject.parseObject(jsonString);*/
		/*JSONArray parameterValueArrays = JSONArray.parseArray(s.getOptions());
		if (CollectionUtils.isNotEmpty(parameterValueArrays)) {
			for(int i = 0; i < parameterValueArrays.size(); i++) {
				parameterValues.add(JSONObject.parseObject(parameterValueArrays.getString(i), ParameterValue.class));
			}
		}*/
		//specification.setOptions(s.getOptions().replace("\"",""));
		specification.setOptions(s.getOptions());
		goods.setImage(sImage);
		goods.setId(goodsId);;
		/*if (CollectionUtils.isNotEmpty(product)) {
			for (int i = 0; i < product.size(); i++) {
				product.get(i).getSpecifications();
				product.get(i).getSpecificationValueIds();
				product.get(i).getSpecificationValuesConverter();
				pRecommends.addAll(product.get(i).getSpecifications());
				//pRecommends.add(product.get(i).getSpecificationValueIds());
				//pRecommends.add(product.get(i).getSpecificationValues());
			}
		}*/
		/*product1.setSpecificationValuesConverter(product.getSpecificationValuesConverter());
		product1.setId(product.getId());*/
		//String options = specification.getOptions();
		//logger.info(options);
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("products", product);
		/*map.put("sImage", sImage);
		map.put("pRecommends", pRecommends);*/
		map.put("specification", specification);
		map.put("goods", goods);
		//map.put("goodsId", goodsId);
		renderJson(new DatumResponse(map));
	}
	
	/**
	 * 查看库存
	 */
	public void checkStock(){
		Long productId = getParaToLong("productId");
		Product product = productService.find(productId);
		
		if(product == null) {
			renderArgumentError("商品没有找到!");
			return;
		}
		product.setStock(product.getStock());;
		product.setId(product.getId());
		renderJson(new DatumResponse(product));
	}
	
}
