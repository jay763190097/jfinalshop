package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PointLogService;

/**
 * Controller - 会员中心 - 我的积分
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/point_log")
@Before(MemberInterceptor.class)
public class PointLogController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberService memberService;
	@Inject
	private PointLogService pointLogService;

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", pointLogService.findPage(member, pageable));
		setAttr("member", member);
		render("/shop/${theme}/member/point_log/list.ftl");
	}

}