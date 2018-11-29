package com.jfinalshop.controller.wap;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.WapInterceptor;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 商品分类
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/product_category")
@Before(WapInterceptor.class)
public class ProductCategoryController extends BaseController {
	
	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 首页
	 */
	public void index() {
		setAttr("title" , "全部分类");
		setAttr("rootProductCategories", productCategoryService.findRoots());
		render("/wap/product_category/index.ftl");
	}
}
