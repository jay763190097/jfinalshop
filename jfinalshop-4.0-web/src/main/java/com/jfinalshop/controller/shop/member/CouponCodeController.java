package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.CouponService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 会员中心 - 优惠码
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/coupon_code")
@Before(MemberInterceptor.class)
public class CouponCodeController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberService memberService;
	@Inject
	private CouponService couponService;
	@Inject
	private CouponCodeService couponCodeService;

	/**
	 * 兑换
	 */
	public void exchange() {
		Integer pageNumber = getParaToInt("pageNumber");
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", couponService.findPage(true, true, false, pageable));
		render("/shop/${theme}/member/coupon_code/exchange.ftl");
	}

	/**
	 * 兑换
	 */
	public void exchangeSubmit() {
		Long id = getParaToLong("id");
		Coupon coupon = couponService.find(id);
		if (coupon == null || !coupon.getIsEnabled() || !coupon.getIsExchange() || coupon.hasExpired()) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Member member = memberService.getCurrent();
		if (member.getPoint() < coupon.getPoint()) {
			renderJson(Message.warn("shop.member.couponCode.point"));
			return;
		}
		couponCodeService.exchange(coupon, member, null);
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", couponCodeService.findPage(member, pageable, null));
		setAttr("member", member);
		render("/shop/${theme}/member/coupon_code/list.ftl");
	}

}