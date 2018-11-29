package com.jfinalshop.controller.wap.member;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.DepositLogService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 会员中心 - 预存款
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/member/deposit")
@Before(WapMemberInterceptor.class)
public class DepositController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberService memberService;
	@Inject
	private DepositLogService depositLogService;
	@Inject
	private PluginService pluginService;
	
	/**
	 * 充值
	 */
	public void recharge() {
		List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
		if (!paymentPlugins.isEmpty()) {
			setAttr("defaultPaymentPlugin", paymentPlugins.get(0));
			setAttr("paymentPlugins", paymentPlugins);
		}
		setAttr("title" , "余额充值 - 会员中心");
		render("/wap/member/deposit/recharge.ftl");
	}
	
	/**
	 * 记录
	 */
	public void log() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pages", depositLogService.findPage(member, pageable));
		setAttr("title" , "会员中心 - 预存款");
		render("/wap/member/deposit/log.ftl");
	}
	
	
}
