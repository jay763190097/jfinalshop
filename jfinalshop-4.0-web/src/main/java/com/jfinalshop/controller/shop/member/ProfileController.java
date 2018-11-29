package com.jfinalshop.controller.shop.member;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Setting;
import com.jfinalshop.controller.shop.BaseController;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 会员中心 - 个人资料
 * 
 * 
 */
@ControllerBind(controllerKey = "/member/profile")
@Before(MemberInterceptor.class)
public class ProfileController extends BaseController {

	@Inject
	private MemberService memberService;
	@Inject
	private MemberAttributeService memberAttributeService;

	/**
	 * 检查E-mail是否唯一
	 */
	public void checkEmail() {
		String email = getPara("email");
		if (StringUtils.isEmpty(email)) {
			renderJson(false);
			return;
		}
		Member member = memberService.getCurrent();
		renderJson(memberService.emailUnique(member.getEmail(), email));
	}

	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("genders", Member.Gender.values());
		render("/shop/${theme}/member/profile/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String email = getPara("email");
		String nickname = getPara("nickname");

		Setting setting = SystemUtils.getSetting();
		Member member = memberService.getCurrent();
		if (!setting.getIsDuplicateEmail() && !memberService.emailUnique(member.getEmail(), email)) {
			redirect(ERROR_VIEW);
			return;
		}
		member.setEmail(email);
		member.setNickname(nickname);
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = getRequest().getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				redirect(ERROR_VIEW);
				return;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		memberService.update(member);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/shop/${theme}/member/profile/edit.jhtml");
	}

}