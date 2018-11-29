package com.jfinalshop.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PropKit;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.service.ProductCategoryService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 顶级商品分类列表
 * 
 * 
 */
public class ProductCategoryRootListDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "productCategories";

	private ProductCategoryService productCategoryService = appContext.getInstance(ProductCategoryService.class);

	/**
	 * 执行
	 * 
	 * @param env
	 *            环境变量
	 * @param params
	 *            参数
	 * @param loopVars
	 *            循环变量
	 * @param body
	 *            模板内容
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Integer count = getCount(params);
		boolean useCache = useCache(env, params);
		String channel= PropKit.get("channelcode");
//		List<ProductCategory> productCategories = productCategoryService.findRoots(count, useCache);
		List<ProductCategory> productCategories = productCategoryService.findShopRoot3(count, useCache,channel);
		setLocalVariable(VARIABLE_NAME, productCategories, env, body);
	}

}