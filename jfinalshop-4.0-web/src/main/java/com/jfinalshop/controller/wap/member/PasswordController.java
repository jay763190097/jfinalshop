package com.jfinalshop.controller.wap.member;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Setting;
import com.jfinalshop.controller.wap.BaseController;
import com.jfinalshop.interceptor.WapMemberInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 修改密码 - 会员中心
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/member/password")
@Before(WapMemberInterceptor.class)
public class PasswordController extends BaseController {

	@Inject
	private MemberService memberService;
	
	
	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("title" , "修改密码 - 会员中心");
		render("/wap/member/password/edit.ftl");
	}
	
	/**
	 * 更新
	 */
	public void update() {
		String oldpassword = getPara("oldpassword");
		String newpassword = getPara("newpassword");
		String newpassword1 = getPara("newpassword1");
		
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isEmpty(newpassword) || StringUtils.isEmpty(oldpassword)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "新旧密码不能为空!");
			renderJson(map);
			return;
		}
		if (!StringUtils.equals(newpassword, newpassword1)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "两次输入密码不相同!");
			renderJson(map);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		if (newpassword.length() < setting.getPasswordMinLength() || newpassword.length() > setting.getPasswordMaxLength()) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "密码长度必须在" +setting.getPasswordMinLength() +"到" + setting.getPasswordMaxLength() + "之间!");
			renderJson(map);
			return;
		}
		Member member = memberService.getCurrent();
		if (!StringUtils.equals(DigestUtils.md5Hex(oldpassword), member.getPassword())) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "旧密码错误!");
			renderJson(map);
			return;
		}
		member.setPassword(DigestUtils.md5Hex(newpassword));
		memberService.update(member);
		map.put(STATUS, SUCCESS);
		map.put("referer", "/wap/member.jhtml");
		renderJson(map);
	}
}
