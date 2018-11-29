package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.service.PromotionService;

/**
 * Controller - 促销
 * 
 * 
 */
@ControllerBind(controllerKey = "/promotion")
@Before(ThemeInterceptor.class)
public class PromotionController extends BaseController {

	@Inject
	private PromotionService promotionService;

	/**
	 * 内容
	 */
	public void content() {
		Long id = getParaToLong(0);
		Promotion promotion = promotionService.find(id);
		if (promotion == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("promotion", promotion);
		render("/shop/${theme}/promotion/content.ftl");
	}

}