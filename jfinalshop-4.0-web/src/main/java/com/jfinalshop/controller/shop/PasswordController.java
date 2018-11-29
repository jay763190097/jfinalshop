
package com.jfinalshop.controller.shop;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.api.sms.json.JSONHttpClient;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.sms.SMSManager;
import com.jfinalshop.entity.SafeKey;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MailService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.session.RedisManager;
import com.jfinalshop.util.SMSUtils;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 密码
 * 
 * 
 */
@ControllerBind(controllerKey = "/password")
@Before(ThemeInterceptor.class)
public class PasswordController extends BaseController {

	@Inject
	private MemberService memberService;
	@Inject
	private MailService mailService;
	private static final Logger LOG = Logger.getLogger(PasswordController.class);
	/** redis管理 */
	private RedisManager redisManager = new RedisManager();
	/**
	 * 找回密码
	 */
	public void find() {
		render("/shop/${theme}/password/find.ftl");
	}
	
	/**
	 * 检查用户名是否被禁用或已存在
	 *//*
	public void checkUsername() {
		String username = getPara("username");
		if (StringUtils.isEmpty(username)) {
			renderJson(false);
			return;
		}
		boolean a = memberService.usernameDisabled(username);
		boolean b = memberService.usernameExists(username);
		System.out.println(">>>>>>>>>"+a+"++++++++++"+b);
		renderJson(!memberService.usernameDisabled(username) && !memberService.usernameExists(username));
	}*/
	
	/**
	 * 检查手机号码是否已发送注册短讯，验证码是否正确
	 */
	public void checkSMS() {
		String code = getPara("code");
		String smscode = (String) getSession().getAttribute("smscode");
		if (StringUtils.isEmpty(smscode)) {
			renderJson(false);
			return;
		}
		renderJson(code.equals(smscode));
	}
	
	/**
	 * 获取验证码
	 * @throws URIException 
	 */
	public void getCode() {
		String account = "dh29521";// 用户名（必填）
		String password = "D5Rr1o~2";// 密码（必填,明文）
		String phone = getPara("username"); // 手机号码（必填,多条以英文逗号隔开）
		String ID = phone + "pass";
		//获取redis当中的验证码
		String smsCode = redisManager.get(ID);
		LOG.info("会员的存在验证码：" + smsCode);
		LOG.info("会员的手机号码为：" + phone);
		String sign = "【青岛中谷】"; // 短信签名（必填）
		String subcode = ""; // 子号码（可选）
		//String msgid = UUID.randomUUID().toString().replace("-", ""); // 短信id，查询短信状态报告时需要，（可选）
		String sendtime = ""; // 定时发送时间（可选）
		String SMSCODE = SMSUtils.randomSMSCode(6);
		String content = "验证码:"+SMSCODE + " ，有效期10分钟。";// 短信内容（必填）
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
			// TODO Auto-generated catch block
			LOG.error("应用异常", e);
		}
		renderJson();
	}
	/**
	 * 找回密码提交
	 */
	public void findSubmit() {
		String code = getPara("code");
		String username = getPara("username");
		/*String email = getPara("email");*/
		String newPassword = getPara("rePassword");
		String ID = username + "pass";
		String smsCode = redisManager.get(ID);
		//String smscode = (String) getSession().getAttribute("smscode");
		/*if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}*/
		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(code)) {
			renderJson(Message.error("shop.common.invalid"));
			return;
		}
		if (newPassword.length() < setting.getPasswordMinLength() || newPassword.length() > setting.getPasswordMaxLength()) {
			renderJson(Message.error("shop.password.invalidPassword"));
			return;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			renderJson(Message.error("shop.password.memberNotExist"));
			return;
		}
		/*if (StringUtils.isEmpty(member.getEmail())) {
			renderJson(Message.error("shop.password.emailEmpty"));
			return;
		}*/
		if (!code.equals(smsCode)) {
			renderJson(Message.error("shop.password.smsCodeError"));
			return;
		}
//		if (!StringUtils.equalsIgnoreCase(member.getEmail(), email)) {
//			renderJson(Message.error("shop.password.invalidEmail"));
//			return;
//		}
/*		Setting setting = SystemUtils.getSetting();
		SafeKey safeKey = new SafeKey();
		safeKey.setValue(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		safeKey.setExpire(setting.getSafeKeyExpiryTime() != 0 ? DateUtils.addMinutes(new Date(), setting.getSafeKeyExpiryTime()) : null);
		member.setSafeKeyExpire(safeKey.getExpire());
		member.setSafeKeyValue(safeKey.getValue());
		member.setSafeKey(safeKey);
		memberService.update(member);
		mailService.sendFindPasswordMail(member.getEmail(), member.getUsername(), safeKey);*/
		member.setPassword(DigestUtils.md5Hex(newPassword));
		member.setSafeKeyExpire(null);
		member.setSafeKeyValue(null);
		memberService.update(member);
		renderJson(Message.success("shop.password.resetSuccess"));
	}

	/**
	 * 重置密码
	 */
	public void reset() {
		String username = getPara("username");
		String key = getPara("key");
		Member member = memberService.findByUsername(username);
		if (member == null) {
			redirect(ERROR_VIEW);
			return;
		}
		SafeKey safeKey = new SafeKey();
		safeKey.setExpire(member.getSafeKeyExpire());
		safeKey.setValue(member.getSafeKeyValue());
		if (safeKey == null || safeKey.getValue() == null || !safeKey.getValue().equals(key)) {
			redirect(ERROR_VIEW);
			return;
		}
		if (safeKey.hasExpired()) {
			setAttr("errorMessage", Message.warn("shop.password.hasExpired"));
			redirect(ERROR_VIEW);
			return;
		}
		setAttr("member", member);
		setAttr("key", key);
		render("/shop/${theme}/password/reset.ftl");
	}

	/**
	 * 重置密码提交
	 */
	public void resetSubmit() {
		String captcha = getPara("captcha");
		String username = getPara("username");
		String newPassword = getPara("newPassword");
		String key = getPara("key");
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		if (newPassword.length() < setting.getPasswordMinLength() || newPassword.length() > setting.getPasswordMaxLength()) {
			renderJson(Message.warn("shop.password.invalidPassword"));
			return;
		}
		SafeKey safeKey = new SafeKey();
		safeKey.setExpire(member.getSafeKeyExpire());
		safeKey.setValue(member.getSafeKeyValue());
		if (safeKey == null || safeKey.getValue() == null || !safeKey.getValue().equals(key)) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		if (safeKey.hasExpired()) {
			renderJson(Message.error("shop.password.hasExpired"));
			return;
		}
		member.setPassword(DigestUtils.md5Hex(newPassword));
		member.setSafeKeyExpire(null);
		member.setSafeKeyValue(null);
		memberService.update(member);
		renderJson(Message.success("shop.password.resetSuccess"));
	}

}