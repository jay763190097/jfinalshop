package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ReviewService;

/**
 * Controller - 会员中心 - 评论
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/review")
@Before(MemberInterceptor.class)
public class ReviewController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberService memberService;
	@Inject
	private ReviewService reviewService;

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", reviewService.findPage(member, null, null, null, pageable));
		setAttr("member", member);
		render("/shop/${theme}/member/review/list.ftl");
	}

}