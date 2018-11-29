package com.jfinalshop.controller.wap;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Principal;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.WapInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.SmsService;
import com.jfinalshop.util.SMSUtils;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.util.WebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Controller - 会员注册
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/register")
@Before(WapInterceptor.class)
public class RegisterController extends BaseController {
	private static Log log = LogFactory.getLog(RegisterController.class);
	@Inject
	private MemberService memberService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private SmsService smsService;
	
	private static final String AVATAR = "/statics/img/default_head.png";
	/**
	 * 检查用户名是否被禁用或已存在
	 */
	public void checkUsername() {
		String username = getPara("param");
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isEmpty(username)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "手机号不为空!");
			renderJson(map);
			return;
		}
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "手机号已存在或禁用中!");
			renderJson(map);
		} else {
			map.put(STATUS, SUCCESS);
			map.put(MESSAGE, "允许使用!");
			renderJson(map);
		}
	}
	
	/**
	 * 发送短信
	 */
	public void send() {
		String mobile = getPara("mobile");
		String vcode = SMSUtils.randomSMSCode(4);
		
		Map<String, String> map = new HashMap<String, String>();
		if (StrKit.isBlank(mobile)) {
			map.put(MESSAGE, "手机号不能为空!");
			renderJson(map);
			return;
		}
		// 保存
		smsService.saveOrUpdate(mobile, vcode, Setting.SmsType.memberRegister);
		log.info("保存短信成功");
		JSONObject result = smsService.sendSmsVerifyCode(mobile, vcode);
		if (result != null && !result.getBoolean("success")) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "短信太忙了[" + result.getString("sub_msg") + "]");
			renderJson(map);
			return;
		} 
		map.put(STATUS, SUCCESS);
		map.put(MESSAGE, "发送成功!");
		renderJson(map);
	}
	
	/**
	 * 注册页面
	 */
	public void index() {
		setAttr("title" , "会员注册");
		render("/wap/register/index.ftl");
	}

	/**
	 * 注册提交
	 */
	public void submit() {
		String mobile = getPara("mobile");
		String password = getPara("password", "asDF~!HJK_+98");
		String pwdconfirm = getPara("pwdconfirm", "asDF~!HJK_+98");
		String vcode = getPara("vcode");
		String url_forward = getPara("url_forward");
		
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		
		Map<String, String> map = new HashMap<String, String>();
		
		if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(vcode)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "手机号或验证码不为空!");
			renderJson(map);
			return;
		}
		
		if (!smsService.smsExists(mobile, vcode, Setting.SmsType.memberRegister)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "手机号验证码错误!");
			renderJson(map);
			return;
		}
		
		Res resZh = I18n.use();
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsRegisterEnabled()) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.register.disabledExist"));
			renderJson(map);
			return;
		}
		
		if (password == null || pwdconfirm == null || (! password.equals(pwdconfirm))) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, "两次密码不一致!");
			renderJson(map);
			return;
		} 

		if (mobile.length() < setting.getUsernameMinLength() || mobile.length() > setting.getUsernameMaxLength()) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.common.invalid"));
			renderJson(map);
			return;
		}
		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.common.invalid"));
			renderJson(map);
			return;
		}
		if (memberService.usernameDisabled(mobile) || memberService.usernameExists(mobile)) {
			map.put(STATUS, ERROR);
			map.put(MESSAGE, resZh.format("shop.register.disabledExist"));
			renderJson(map);
			return;
		}

		Member member = new Member();
		member.removeAttributeValue();
		member.setUsername(StringUtils.lowerCase(mobile));
		member.setPassword(DigestUtils.md5Hex(password));
		member.setNickname(null);
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setIsEnabled(true);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setLockedDate(null);
		member.setRegisterIp(request.getRemoteAddr());
		member.setLoginIp(request.getRemoteAddr());
		member.setLoginDate(new Date());
		member.setLoginPluginId(null);
		member.setOpenId(null);
		member.setLockKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		member.setSafeKey(null);
		member.setMemberRankId(memberRankService.findDefault() != null ? memberRankService.findDefault().getId() : null);
		member.setCart(null);
		member.setOrders(null);
		member.setPaymentLogs(null);
		member.setDepositLogs(null);
		member.setCouponCodes(null);
		member.setReceivers(null);
		member.setReviews(null);
		member.setConsultations(null);
		member.setFavoriteGoods(null);
		member.setProductNotifies(null);
		member.setInMessages(null);
		member.setOutMessages(null);
		member.setPointLogs(null);
		member.setAvatar(AVATAR);
		member.setNickname(StringUtils.lowerCase(mobile));
		String openId = (String) request.getSession().getAttribute(Member.OPEN_ID);
		if (StrKit.notBlank(openId)) {
			member.setOpenId(openId);
		}
		memberService.save(member);

		if (setting.getRegisterPoint() > 0) {
			memberService.addPoint(member, setting.getRegisterPoint(), PointLog.Type.reward, null, null);
		}

		setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
		WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
		if (StringUtils.isNotEmpty(member.getNickname())) {
			WebUtils.addCookie(request, response, Member.NICKNAME_COOKIE_NAME, member.getNickname());
		}

		map.put(STATUS, SUCCESS);
		map.put(MESSAGE, resZh.format("shop.register.success"));
		map.put("referer", url_forward);
		renderJson(map);
	}
	
}
