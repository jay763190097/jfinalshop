package com.jfinalshop.controller.shop;

import com.jfinal.kit.PropKit;
import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 商品分类
 * 
 * 
 */
@ControllerBind(controllerKey = "/product_category")
@Before(ThemeInterceptor.class)
public class ProductCategoryController extends BaseController {

	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 首页
	 */
	public void index() {
	    String channel = PropKit.get("channelcode");
		System.out.println(PropKit.get("channelcode"));
		//setAttr("rootProductCategories", productCategoryService.findRoots());
	    setAttr("rootProductCategories", productCategoryService.findShopRoots(channel));
		render("/shop/${theme}/product_category/index.ftl");
	}

}