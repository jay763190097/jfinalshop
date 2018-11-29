package com.jfinalshop.api.controller.member;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.CouponCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会员中心 - 优惠码
 * 
 * 
 */
@ControllerBind(controllerKey = "/api/member/couponCode")
@Before(TokenInterceptor.class)
public class CouponCodeAPIController extends BaseAPIController {

	private static Logger logger = LoggerFactory.getLogger(CouponCodeAPIController.class);

	@Inject
	private CouponCodeService couponCodeService;
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Integer pageSize = getParaToInt("pageSize", 20);
		Boolean isUsed = getParaToBoolean("isUsed", null);
		Member member = getMember();
		Pageable pageable = new Pageable(pageNumber, pageSize);
		Page<CouponCode> pages = couponCodeService.findPageApi(member, pageable, isUsed);
		renderJson(new DatumResponse(pages));
	}

	/**
	 * 查出用户可用的优惠券
	 */
	public void couponCode() {
		Member member = getMember();
		List<CouponCode> couponCodes = couponCodeService.findCouponCodes(member, false, false);
		renderJson(new DataResponse(couponCodes));
	}
	
	
}
