package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.kit.PropKit;
import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Principal;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.plugin.LoginPlugin;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.RSAService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.util.WebUtils;

/**
 * Controller - 会员登录
 * 
 * 
 */
@ControllerBind(controllerKey = "/login")
@Before(ThemeInterceptor.class)
public class LoginController extends BaseController {

	@Inject
	private RSAService rsaService;
	@Inject
	private MemberService memberService;
	@Inject
	private CartService cartService;
	@Inject
	private PluginService pluginService;
	@Inject
	private MemberRankService memberRankService;

	/**
	 * 登录检测
	 */
	public void check() {
		renderJson(memberService.isAuthenticated());
	}

	/**
	 * 登录页面
	 */
	public void index() {
		String redirectUrl = getPara("redirectUrl");
		Setting setting = SystemUtils.getSetting();
		if (StringUtils.equalsIgnoreCase(redirectUrl, setting.getSiteUrl()) || StringUtils.startsWithIgnoreCase(redirectUrl, getRequest().getContextPath() + "/") || StringUtils.startsWithIgnoreCase(redirectUrl, setting.getSiteUrl() + "/")) {
			setAttr("redirectUrl", redirectUrl);
		}
		setAttr("captchaId", UUID.randomUUID().toString());
		setAttr("loginPlugins", pluginService.getLoginPlugins(true));
		render("/shop/${theme}/login/index.ftl");
	}

	/**
	 * 登录提交
	 */
	public void submit() {
		String channel= PropKit.get("channelcode");
		String captcha = getPara("captcha"); 
		String username = getPara("username");
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		String password = rsaService.decryptParameter("enPassword", request);
		rsaService.removePrivateKey(request);

		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			renderJson(Message.error("shop.common.invalid"));
			return;
		}
		Member member;
		Setting setting = SystemUtils.getSetting();
		if (setting.getIsEmailLogin() && username.contains("@")) {
			List<Member> members = memberService.findListByEmail(username);
			if (members.isEmpty()) {
				member = null;
			} else if (members.size() == 1) {
				member = members.get(0);
			} else {
				renderJson(Message.error("shop.login.unsupportedAccount"));
				return;
			}
		} else {
			member = memberService.findByUsername(username);
		}
		if (member == null) {
			renderJson(Message.error("shop.login.unknownAccount"));
			return;
		}
		if (!member.getIsEnabled()) {
			renderJson(Message.error("shop.login.disabledAccount"));
			return;
		}
		if (member.getIsLocked()) {
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
				int loginFailureLockTime = setting.getAccountLockTime();
				if (loginFailureLockTime == 0) {
					renderJson(Message.error("shop.login.lockedAccount"));
					return;
				}
				Date lockedDate = member.getLockedDate();
				Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
				if (new Date().after(unlockDate)) {
					member.setLoginFailureCount(0);
					member.setIsLocked(false);
					member.setLockedDate(null);
					memberService.update(member);
				} else {
					renderJson(Message.error("shop.login.lockedAccount"));
					return;
				}
			} else {
				member.setLoginFailureCount(0);
				member.setIsLocked(false);
				member.setLockedDate(null);
				memberService.update(member);
			}
		}

		if (!DigestUtils.md5Hex(password).equals(member.getPassword())) {
			int loginFailureCount = member.getLoginFailureCount() + 1;
			if (loginFailureCount >= setting.getAccountLockCount()) {
				member.setIsLocked(true);
				member.setLockedDate(new Date());
			}
			member.setLoginFailureCount(loginFailureCount);
			memberService.update(member);
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
				renderJson(Message.error("shop.login.accountLockCount", setting.getAccountLockCount()));
				return;
			} else {
				renderJson(Message.error("shop.login.incorrectCredentials"));
				return;
			}
		}
		member.setLoginIp(request.getRemoteAddr());
		member.setLoginDate(new Date());
		member.setLoginFailureCount(0);
		memberService.update(member);

		Cart cart = cartService.getCurrent(channel);
		if (cart != null && cart.getMember() == null) {
			cartService.merge(member, cart,channel);
			WebUtils.removeCookie(request, response, Cart.KEY_COOKIE_NAME);
		}

		/*Map<String, Object> attributes = new HashMap<String, Object>();
		Enumeration<?> keys = session.getAttributeNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			attributes.put(key, session.getAttribute(key));
		}
		session.invalidate();
		session = request.getSession();
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			session.setAttribute(entry.getKey(), entry.getValue());
		}*/

		//session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), username));
		setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
		WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
		if (StringUtils.isNotEmpty(member.getNickname())) {
			WebUtils.addCookie(request, response, Member.NICKNAME_COOKIE_NAME, member.getNickname());
		}

		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 插件提交
	 */
	public void pluginSubmit() {
		String pluginId = getPara("pluginId");
		
		LoginPlugin loginPlugin = pluginService.getLoginPlugin(pluginId);
		if (loginPlugin == null || !loginPlugin.getIsEnabled()) {
			redirect(ERROR_VIEW);
			return;
		}
		setAttr("requestUrl", loginPlugin.getRequestUrl());
		setAttr("requestMethod", loginPlugin.getRequestMethod());
		setAttr("requestCharset", loginPlugin.getRequestCharset());
		setAttr("parameterMap", loginPlugin.getParameterMap(getRequest()));
		if (StringUtils.isNotEmpty(loginPlugin.getRequestCharset())) {
			getResponse().setContentType("text/html; charset=" + loginPlugin.getRequestCharset());
		}
		render("/shop/${theme}/login/plugin_submit.ftl");
	}

	/**
	 * 插件通知
	 */
	public void pluginNotify() {
		String channel= PropKit.get("channelcode");
		String pluginId = getPara("pluginId");
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		//HttpSession session = getSession();
		
		LoginPlugin loginPlugin = pluginService.getLoginPlugin(pluginId);
		if (loginPlugin != null && loginPlugin.getIsEnabled() && loginPlugin.verifyNotify(request)) {
			Setting setting = SystemUtils.getSetting();
			String openId = loginPlugin.getOpenId(request);
			if (StringUtils.isEmpty(openId)) {
				setAttr("errorMessage", message("shop.login.pluginError"));
				redirect(ERROR_VIEW);
				return;
			}
			Member member = memberService.find(pluginId, openId);
			if (member != null) {
				if (!member.getIsEnabled()) {
					setAttr("errorMessage", message("shop.login.disabledAccount"));
					redirect(ERROR_VIEW);
					return;
				}
				if (member.getIsLocked()) {
					if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
						int loginFailureLockTime = setting.getAccountLockTime();
						if (loginFailureLockTime == 0) {
							setAttr("errorMessage", message("shop.login.lockedAccount"));
							redirect(ERROR_VIEW);
							return;
						}
						Date lockedDate = member.getLockedDate();
						Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
						if (new Date().after(unlockDate)) {
							member.setLoginFailureCount(0);
							member.setIsLocked(false);
							member.setLockedDate(null);
							memberService.update(member);
						} else {
							setAttr("errorMessage", message("shop.login.lockedAccount"));
							redirect(ERROR_VIEW);
							return;
						}
					} else {
						member.setLoginFailureCount(0);
						member.setIsLocked(false);
						member.setLockedDate(null);
						memberService.update(member);
					}
				}
				member.setLoginIp(request.getRemoteAddr());
				member.setLoginDate(new Date());
				member.setLoginFailureCount(0);
				memberService.update(member);
			} else {
				if (!setting.getIsRegisterEnabled()) {
					setAttr("errorMessage", message("shop.login.registerDisabled"));
					redirect(ERROR_VIEW);
					return;
				}
				String email = loginPlugin.getEmail(request);
				String nickname = loginPlugin.getNickname(request);
				member = new Member();
				String username = openId;
				for (int i = 0; memberService.usernameExists(username); i++) {
					username = openId + i;
				}
				member.removeAttributeValue();
				member.setUsername(StringUtils.lowerCase(username));
				member.setEmail(StringUtils.lowerCase(email));
				member.setPassword(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
				member.setNickname(nickname);
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
				member.setLoginPluginId(pluginId);
				member.setOpenId(openId);
				member.setLockKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
				//member.setSafeKey(null);
				member.setMemberRankId(memberRankService.findDefault().getId());
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
				member.setUsername(StringUtils.lowerCase(member.getUsername()));
				member.setEmail(StringUtils.lowerCase(member.getEmail()));
				member.setLockKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
				memberService.save(member);

				if (setting.getRegisterPoint() > 0) {
					memberService.addPoint(member, setting.getRegisterPoint(), PointLog.Type.reward, null, null);
				}
			}
			Cart cart = cartService.getCurrent(channel);
			if (cart != null && cart.getMember() == null) {
				cartService.merge(member, cart,channel);
				WebUtils.removeCookie(request, response, Cart.KEY_COOKIE_NAME);
			}

			/*Map<String, Object> attributes = new HashMap<String, Object>();
			Enumeration<?> keys = session.getAttributeNames();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				attributes.put(key, session.getAttribute(key));
			}
			session.invalidate();
			session = request.getSession();
			for (Map.Entry<String, Object> entry : attributes.entrySet()) {
				session.setAttribute(entry.getKey(), entry.getValue());
			}*/

			//session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
			setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
			WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
			if (StringUtils.isNotEmpty(member.getNickname())) {
				WebUtils.addCookie(request, response, Member.NICKNAME_COOKIE_NAME, member.getNickname());
			}
		}
		redirect("/");
	}

}