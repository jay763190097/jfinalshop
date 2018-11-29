package com.jfinalshop.controller.wap.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 会员中心 - 个人资料
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/member/profile")
@Before(WapMemberInterceptor.class)
public class ProfileController extends BaseController {

	@Inject
	private MemberService memberService;
	
	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("title" , "安全中心 - 会员中心");
		setAttr("member" , memberService.getCurrent());
		render("/wap/member/profile/edit.ftl");
	}
	
	
}
