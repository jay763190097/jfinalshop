package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Brand;
import com.jfinalshop.service.BrandService;

/**
 * Controller - 品牌
 * 
 * 
 */
@ControllerBind(controllerKey = "/brand")
@Before(ThemeInterceptor.class)
public class BrandController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 40;

	@Inject
	private BrandService brandService;

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", brandService.findPage(pageable));
		render("/shop/${theme}/brand/list.ftl");
	}

	/**
	 * 内容
	 */
	public void content() {
		Long id = getParaToLong(0);
		Brand brand = brandService.find(id);
		if (brand == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("brand", brand);
		render("/shop/${theme}/brand/content.ftl");
	}

}