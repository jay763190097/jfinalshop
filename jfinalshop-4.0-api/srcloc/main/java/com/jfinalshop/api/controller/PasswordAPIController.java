package com.jfinalshop.api.controller;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.api.sms.json.JSONHttpClient;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.SmsService;
import com.jfinalshop.util.SMSUtils;
import com.jfinalshop.util.SystemUtils;

import net.hasor.core.Inject;
/**
 * 
 * 修改密码
 *
 */
@ControllerBind(controllerKey = "/api/password")
@Before(AccessInterceptor.class)
public class PasswordAPIController extends BaseAPIController {
	private static Logger logger = LoggerFactory.getLogger(PasswordAPIController.class);

	@Inject
	private MemberService memberServices;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private SmsService smsService; 
	public void find() {
		
	}
	/**
	 * 检查用户名是否未注册
	 */
	public void checkUsername() {
		String username = getPara("username");
		if (StringUtils.isEmpty(username)) {
			renderJson(new DatumResponse(false));
			return;
		}
		renderJson(new DatumResponse(memberServices.usernameExists(username)));
	}
	/**
	 * 检查手机号码是否已发送注册短讯，验证码是否正确
	 */
	public void checkSMS() {
		String code = getPara("smsCode");
		String smscode = (String) getSession().getAttribute("smscode");
		if (StringUtils.isEmpty(smscode)) {
			renderJson(new DatumResponse(false));
			return;
		}
		renderJson(new DatumResponse(code.equals(smscode)));
	}
	
	/**
	 * 获取验证码
	 * @throws URIException 
	 */
	public void sendChangePs() {
		String account = "dh29521";// 用户名（必填）
		String password = "D5Rr1o~2";// 密码（必填,明文）
		String phone = getPara("username"); // 手机号码（必填,多条以英文逗号隔开）
		String ID = phone + "code";
		String smscode = (String) getSession().getAttribute(ID);
		logger.info("会员的手机号码为：" + phone);
		String sign = "【青岛中谷】"; // 短信签名（必填）
		String subcode = ""; // 子号码（可选）
		//String msgid = UUID.randomUUID().toString().replace("-", ""); // 短信id，查询短信状态报告时需要，（可选）
		String sendtime = ""; // 定时发送时间（可选）
		String smsCode = SMSUtils.randomSMSCode(6);
		String content = "提现验证码:"+smsCode + "，有效期10分钟。";// 短信内容（必填）
		JSONHttpClient jsonHttpClient;
		if (smscode!=null) {
	           getSession().removeAttribute(ID);
	        }
		try {
			jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
			jsonHttpClient.setRetryCount(1);
			String sendhRes = jsonHttpClient.sendSms(account, password, phone, content, sign, subcode);
			JSONObject json = JSONObject.parseObject(sendhRes);
			logger.info("提交单条普通短信响应：" + sendhRes);
			String end = json.getString("desc");
			System.out.println("响应的结果是："+end);
			if("提交成功".equals(end)){
				//将验证码存入session当中
				//setSessionAttr(ID, smsCode);
				getSession().setAttribute(ID, smsCode);
				//setAttr("success", "YES");
				 //TimerTask实现5分钟后从session中删除checkCode
	            final Timer timer=new Timer();
	            timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	            getSession().removeAttribute(ID);
	            System.out.println("smscode删除成功");
	            timer.cancel();
	                }
	            },10*60*1000);
	            renderJson(new BaseResponse("发送短信成功"));
			}else{
			    //setAttr("success", "NO");
			    renderArgumentError("服务器异常");
			}
		} catch (URIException e) {

			logger.error("应用异常", e);
		}
	}
	/**
	 * 找回密码提交
	 */
	public void findSubmit() {
		String code = getPara("smsCode");
		String username = getPara("username");
		String ID = username + "code";
		/*String email = getPara("email");*/
		String rePassword = getPara("rePassword");
		String smsCode = (String) getSession().getAttribute(ID);
		/*if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Message.error("shop.captcha.invalid"));
			return;
		}*/
		if (smsCode==null) {
    		renderArgumentError("请先获取短信验证码");
            return;
        }
		if (!SMSUtils.isMobileNo(username)) {
            renderArgumentError("请检查手机号是否正确！");
            return;
        }
		
		if (!SMSUtils.IsPassword(rePassword)) {
            renderArgumentError("请检查密码是否正确！");
            return;
        }
        
		if (!code.equals(smsCode)) {
			renderArgumentError("验证码输入错误！");
			return;
		}
		
		if (!memberServices.usernameExists(username)) {
			renderArgumentError("用户名手机号未注册！");
			return;
		}

		Member member = memberServices.findByUsername(username);
		member.setPassword(DigestUtils.md5Hex(rePassword));
		member.setSafeKeyExpire(null);
		member.setSafeKeyValue(null);
		memberServices.update(member);
		renderJson(new BaseResponse(Code.SUCCESS, "修改成功"));
	}
}
