package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Setting;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 会员中心 - 密码
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/password")
@Before(MemberInterceptor.class)
public class PasswordController extends BaseController {
	
	@Inject
	private MemberService memberService;

	/**
	 * 验证当前密码
	 */
	public void checkCurrentPassword() {
		String currentPassword = getPara("currentPassword");
		if (StringUtils.isEmpty(currentPassword)) {
			renderJson(false);
			return;
		}
		Member member = memberService.getCurrent();
		renderJson(StringUtils.equals(DigestUtils.md5Hex(currentPassword), member.getPassword()));
	}

	/**
	 * 编辑
	 */
	public void edit() {
		render("/shop/${theme}/member/password/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String currentPassword = getPara("currentPassword");
		String password = getPara("password");
		
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(currentPassword)) {
			redirect(ERROR_VIEW);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if (!StringUtils.equals(DigestUtils.md5Hex(currentPassword), member.getPassword())) {
			redirect(ERROR_VIEW);
			return;
		}
		member.setPassword(DigestUtils.md5Hex(password));
		memberService.update(member);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/shop/${theme}/member/password/edit.jhtml");
	}

}