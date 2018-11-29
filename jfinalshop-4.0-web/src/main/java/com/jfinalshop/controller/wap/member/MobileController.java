package com.jfinalshop.controller.wap.member;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Setting;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.SmsService;
import com.jfinalshop.util.SMSUtils;

/**
 * Controller 修改手机 - 会员中心
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/member/mobile")
@Before(WapMemberInterceptor.class)
public class MobileController extends BaseController{

	@Inject
	private SmsService smsService;
	@Inject
	private MemberService memberService;
	
	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("title" , "修改手机 - 会员中心");
		render("/wap/member/mobile/edit.ftl");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		String mobile = getPara("mobile");
		String vcode = getPara("vcode");
		Map<String, String> map = new HashMap<String, String>();
		if(smsService.smsExists(mobile, vcode, Setting.SmsType.resetMobile)) {
			Member member = memberService.getCurrent();
			member.setMobile(mobile);
			memberService.update(member);
			map.put(STATUS, SUCCESS);
			map.put("referer", "/wap/member/profile/edit.jhtml");
		} else {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "验证码错误!");
		}
		renderJson(map);
	}
	
	/**
	 * 发送短信
	 */
	public void send() {
		String mobile = getPara("mobile");
		String vcode = SMSUtils.randomSMSCode(4);
		smsService.saveOrUpdate(mobile, vcode, Setting.SmsType.resetMobile);
		smsService.send(mobile, vcode);
		renderJson();
	}
	
}
