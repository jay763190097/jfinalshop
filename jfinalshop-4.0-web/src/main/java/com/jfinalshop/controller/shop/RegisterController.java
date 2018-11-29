package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.kit.PropKit;
import com.jfinalshop.shiro.session.SMSManager;
import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.api.sms.json.JSONHttpClient;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Principal;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.RSAService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.session.RedisManager;
import com.jfinalshop.util.SMSUtils;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.util.WebUtils;

/**
 * Controller - 会员注册
 * 
 * 
 */
@ControllerBind(controllerKey = "/register")
@Before(ThemeInterceptor.class)
public class RegisterController extends BaseController {

	@Inject
	private RSAService rsaService;
	@Inject
	private MemberService memberService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private MemberAttributeService memberAttributeService;
	@Inject
	private CartService cartService;
	private static final Logger LOG = Logger.getLogger(RegisterController.class);
	/** redis管理 */
	private RedisManager redisManager = new RedisManager();

	private SMSManager smsManager = new SMSManager();
	/**
	 * 检查用户名是否被禁用或已存在
	 */
	public void checkUsername() {
		String username = getPara("username");
		if (StringUtils.isEmpty(username)) {
			renderJson(false);
			return;
		}
		renderJson(!memberService.usernameDisabled(username) && !memberService.usernameExists(username));
	}

	/**
	 * 检查E-mail是否存在
	 */
	public void checkEmail() {
		String email = getPara("email");
		if (StringUtils.isEmpty(email)) {
			renderJson(false);
			return;
		}
		renderJson(!memberService.emailExists(email));
	}
	/**
	 * 检查手机号码是否存在
	 */
	public void checkPhone() {
		String phone = getPara("mobile");
		getSession().setAttribute("flag",phone);
		if (StringUtils.isEmpty(phone)){
			renderJson(false);
			return;
		}
		renderJson(!memberService.phoneExists(phone));
	}
	/**
	 * 检查手机号码是否已发送注册短讯，验证码是否正确
	 */

	public void checkSMS() {
		String code = getPara("code");
		String username = (String) getSession().getAttribute("flag");
//		String ID = username+"code";
//		String smscode = smsManager.validate(ID,code) ;
//		getSession().removeAttribute("flag");
//		if (smscode=="0") {
//			renderJson(false);
//			return;
//		}
//		renderJson(code.equals(smscode));
		renderJson(true);
	}
//	/**
//	 * 获取验证码
//	 */
	public void getCode() {
		String account = "dh29521";// 用户名（必填）
		String password = "D5Rr1o~2";// 密码（必填,明文）
		String phone = getPara("mobile"); // 手机号码（必填,多条以英文逗号隔开）
		LOG.info("会员的手机号码为：" + phone);
		String ID = phone + "code";
		//String smscode = (String) getSession().getAttribute(ID);
		//获取redis当中的验证码
		String smsCode = redisManager.get(ID);
		//String smsCode = SMSManager.getMe().generateSMS(username);
		LOG.info("会员的手机号码为：" + phone);
		LOG.info("当前短信验证码真的有吗"+smsCode);
		String sign = "【青岛中谷】"; // 短信签名（必填）
		String subcode = ""; // 子号码（可选）
		//String msgid = UUID.randomUUID().toString().replace("-", ""); // 短信id，查询短信状态报告时需要，（可选）
		String sendtime = ""; // 定时发送时间（可选）
		String SMSCODE = SMSUtils.randomSMSCode(6);
		String content = "提现验证码:"+SMSCODE + "，有效期10分钟。";// 短信内容（必填）
		JSONHttpClient jsonHttpClient;
		try {
			jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
			jsonHttpClient.setRetryCount(1);
			String sendhRes = jsonHttpClient.sendSms(account, password, phone, content, sign, subcode);
			JSONObject json = JSONObject.parseObject(sendhRes);
			LOG.info("提交单条普通短信响应：" + sendhRes);
			String end = json.getString("desc");
			System.out.println("响应的结果是："+end);
			if("提交成功".equals(end)){
				//将验证码存入session当中
				String generate = SMSManager.getMe().generateSMS(phone,SMSCODE);
				LOG.info("最新添加的短信验证码"+generate);
				setAttr("success", "YES");
				/* //TimerTask实现5分钟后从session中删除checkCode
	            final Timer timer=new Timer();
	            timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	            getSession().removeAttribute("smscode");
	            System.out.println("smscode删除成功");
	            timer.cancel();
	                }
	            },10*60*1000);*/
			}else{
			    setAttr("success", "NO");
			}
		} catch (URIException e) {

			LOG.error("应用异常", e);
		}
		renderJson();
	}
	/**
	 * 注册页面                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
	 */
	public void index() {
		setAttr("genders", Member.Gender.values());
		/*setAttr("captchaId", UUID.randomUUID().toString());*/
		render("/shop/${theme}/register/index.ftl");
	}

	/**
	 * 注册提交
	 */
	public void submit() {
		/*String captcha = getPara("captcha");*/
		String username = getPara("username");
		String email = getPara("email");
		String code = getPara("code");
		String phone = getPara("mobile");
		String ID = phone + "code";
		String smsCode = redisManager.get(ID);
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		
		/*if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}*/
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsRegisterEnabled()) {
			renderJson(Message.error("shop.register.disabled"));
			return;
		}
		String password = rsaService.decryptParameter("enPassword", request);
		System.out.println("密码是"+password);
		rsaService.removePrivateKey(request);

		if (username.length() < setting.getUsernameMinLength() || username.length() > setting.getUsernameMaxLength()) {
			renderJson(Message.error("shop.common.invalid"));
			return;
		}
		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
			renderJson(Message.error("shop.common.invalid"));
			return;
		}
		if (!code.equals(smsCode)) {
			renderJson(Message.error("shop.register.codeResError"));
			return;
		}
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			renderJson(Message.error("shop.register.disabledExist"));
			return;
		}
		/*if (memberService.phoneExists(phone)) {
			renderJson(Message.error("shop.register.repetition"));
			return;
		}*/
		if (!setting.getIsDuplicateEmail() && memberService.emailExists(email)) {
			renderJson(Message.error("shop.register.emailExist"));
			return;
		}

		Member member = new Member();
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				renderJson(Message.error("shop.common.invalid"));
				return;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		member.setUsername(StringUtils.lowerCase(username));
		member.setPassword(DigestUtils.md5Hex(password));
		member.setEmail(StringUtils.lowerCase(email));
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
		memberService.save(member);
        
		if (setting.getRegisterPoint() > 0) {
			memberService.addPoint(member, setting.getRegisterPoint(), PointLog.Type.reward, null, null);
		}
		String channel= PropKit.get("channelcode");
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
	/*	WebUtils.addCookie(request, response, Member.SMSCODE_COOKIE_NAME, code);
		if (StringUtils.isNotEmpty(code)) {
			WebUtils.addCookie(request, response, Member.SMSCODE_COOKIE_NAME, code);
		}*/
		renderJson(Message.success("shop.register.success"));
	}

}