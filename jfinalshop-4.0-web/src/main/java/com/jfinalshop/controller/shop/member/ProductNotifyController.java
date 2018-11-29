package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductNotifyService;

/**
 * Controller - 会员中心 - 到货通知
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/product_notify")
@Before(MemberInterceptor.class)
public class ProductNotifyController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private ProductNotifyService productNotifyService;
	@Inject
	private MemberService memberService;

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", productNotifyService.findPage(member, null, null, null, pageable));
		setAttr("member", member);
		render("/shop/${theme}/member/product_notify/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		ProductNotify productNotify = productNotifyService.find(id);
		if (productNotify == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Member member = memberService.getCurrent();
		if (!member.getProductNotifies().contains(productNotify)) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		productNotifyService.delete(productNotify);
		renderJson(SUCCESS_MESSAGE);
	}

}