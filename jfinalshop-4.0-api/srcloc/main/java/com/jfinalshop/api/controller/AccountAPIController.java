package com.jfinalshop.api.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.api.sms.json.JSONHttpClient;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.common.bean.LoginResponse;
import com.jfinalshop.api.common.bean.Require;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.RSAService;
import com.jfinalshop.service.SmsService;
import com.jfinalshop.util.SMSUtils;
import com.jfinalshop.util.SystemUtils;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * 
 * 帐户帐号
 *
 */
@ControllerBind(controllerKey = "/api/account")
@Before(AccessInterceptor.class)
public class AccountAPIController extends BaseAPIController {
	
	@Inject
	private MemberService memberService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private SmsService smsService;
	@Inject
	private RSAService rsaService;
	private static final Logger LOG = Logger.getLogger(AccountAPIController.class);
	/** 默认头像 */
	private static final String AVATAR = "/upload/image/default_head.jpg";
	
	/**
	 * 检查用户名是否被禁用或已存在
	 */
	public void checkUsername() {
		String username = getPara("username");
		System.out.println("手机"+username);
		if (StringUtils.isEmpty(username)) {
			renderJson(new DatumResponse(false));
			return;
		}
		renderJson(new DatumResponse(!memberService.usernameDisabled(username) && !memberService.usernameExists(username)));
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
	 * 发送会员短信
	 * 
	 */
    public void sendSMS() {
		String account = "dh29521";// 用户名（必填）
		String password = "D5Rr1o~2";// 密码（必填,明文）
		String username = getPara("username"); // 手机号码（必填,多条以英文逗号隔开）
		//String psw = username.substring(username.length()-4, username.length());
		String ID = username + "code";
		String smscode = (String) getSession().getAttribute(ID);
		LOG.info("会员的手机号码为：" + username);;
		String sign = "【青岛中谷】"; // 短信签名（必填）
		String subcode = ""; // 子号码（可选）
		//String msgid = UUID.randomUUID().toString().replace("-", ""); // 短信id，查询短信状态报告时需要，（可选）
		String sendtime = ""; // 定时发送时间（可选）
		//String yzm = username+"code";
		String smsCode = SMSUtils.randomSMSCode(6);
		String phone = username;
		String content = "提现验证码:"+smsCode + "，有效期10分钟。";// 短信内容（必填）
        if (StringUtils.isEmpty(username)) {
            renderArgumentError("手机不能为空！");
            return;
        }
        if (smscode!=null) {
           getSession().removeAttribute(ID);
           LOG.info("验证码立刻删除"+ID);
        }
        
       /* if (memberService.usernameExists(username)) {
			renderArgumentError("此手机已存在");
			return;
		}*/
        JSONHttpClient jsonHttpClient;
        //getSession().removeAttribute("yzm");
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
				//setSessionAttr(ID, smsCode);
				getSession().setAttribute(ID, smsCode);
				String vfy = (String) getSession().getAttribute(ID);
				LOG.info("存入的session中的是"+vfy);
				 //TimerTask实现5分钟后从session中删除checkCode
	            final Timer timer=new Timer();
	            timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	            System.out.println("定时将"+ID+"删除成功");
	            timer.cancel();
	                }
	            },10*60*1000);
	            renderJson(new BaseResponse("发送短信成功"));
			}else{
			    //setAttr("success", "NO");
			    renderArgumentError("服务器异常");
			}
		} catch (URIException e) {

			LOG.error("应用异常", e);
		}
    }
    
    /**
	 * 注册提交
	 */
	/*public void register(){
		String username = getPara("username");
        String smsCode = getPara("smsCode");
        String password = getPara("password", "asDF~!HJK_+98");
        
        //校验必填项参数
		if(!notNull(Require.me().put(username, "登录名不能为空！").put(smsCode, "验证码不能为空！"))){
			return;
		}
		
		//检查手机号码有效性
        if (!SMSUtils.isMobileNo(username)) {
            renderArgumentError("请检查手机号是否正确！");
            return;
        }
        
		if (!smsService.smsExists(username, smsCode, Setting.SmsType.memberRegister)) {
			renderArgumentError("验证码输入错误");
			return;
		}
		
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			renderArgumentError("用户名被禁用或已被注册");
			return;
		}
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsRegisterEnabled()) {
			renderJson("会员注册功能已关闭");
			return;
		}
		
		if (username.length() < setting.getUsernameMinLength() || username.length() > setting.getUsernameMaxLength()) {
			renderArgumentError("用户名长度在" + setting.getUsernameMinLength() + "-" + setting.getUsernameMaxLength());
			return;
		}
		
		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
			renderArgumentError("密码长度在" + setting.getPasswordMinLength() + "-" + setting.getPasswordMaxLength());
			return;
		}
		
		Member member = new Member();
		member.setUsername(StringUtils.lowerCase(username));
		member.setPassword(DigestUtils.md5Hex(password));
		member.setNickname(null);
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setIsEnabled(true);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setLockedDate(null);
		member.setRegisterIp(getRequest().getRemoteAddr());
		member.setLoginIp(getRequest().getRemoteAddr());
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
		smsService.delete(username, smsCode);
		LoginResponse response = new LoginResponse();
		response.setToken(TokenManager.getMe().generateToken(member));
		setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, TokenManager.getMe().validate(response.getToken()));
		renderJson(new BaseResponse("注册成功！"));
	}*/
	
    /**
	 * 注册提交 快捷注册
	 */
  /*  public void register(){
		String username = getPara("username");
        String code = getPara("smsCode");
        String smscode = (String) getSession().getAttribute("smscode");
        String password = getPara("password", "asDF~!HJK_+98");
        
        //校验必填项参数
		if(!notNull(Require.me().put(username, "手机不能为空！").put(code, "验证码不能为空！"))){
			return;
		}
		
		//检查手机号码有效性
        if (!SMSUtils.isMobileNo(username)) {
            renderArgumentError("请检查手机号是否正确！");
            return;
        }
        
		if (!code.equals(smscode)) {
			renderArgumentError("验证码输入错误");
			return;
		}
		
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			renderArgumentError("用户名被禁用或已被注册");
			return;
		}
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsRegisterEnabled()) {
			renderJson("会员注册功能已关闭");
			return;
		}
		
		if (username.length() < setting.getUsernameMinLength() || username.length() > setting.getUsernameMaxLength()) {
			renderArgumentError("用户名长度在" + setting.getUsernameMinLength() + "-" + setting.getUsernameMaxLength());
			return;
		}
		
		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
			renderArgumentError("密码长度在" + setting.getPasswordMinLength() + "-" + setting.getPasswordMaxLength());
			return;
		}
		
		Member member = new Member();
		member.setUsername(StringUtils.lowerCase(username));
		member.setPassword(DigestUtils.md5Hex(password));
		member.setNickname(null);
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setIsEnabled(true);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setLockedDate(null);
		member.setRegisterIp(getRequest().getRemoteAddr());
		member.setLoginIp(getRequest().getRemoteAddr());
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
		//smsService.delete(username, code);
		LoginResponse response = new LoginResponse();
		response.setToken(TokenManager.getMe().generateToken(member));
		setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, TokenManager.getMe().validate(response.getToken()));
		renderJson(new BaseResponse("注册成功"));
	}*/
	
	/**
	 * 发送会员登录短信
	 */
  /*  public void sendLoginSms() {
    	String smscode = (String) getSession().getAttribute("smscode");
    	String account = "dh29521";// 用户名（必填）
		String password = "D5Rr1o~2";// 密码（必填,明文）
		String username = getPara("username"); // 手机号码（必填,多条以英文逗号隔开）
		LOG.info("会员的手机号码为：" + username);;
		String sign = "【青岛中谷】"; // 短信签名（必填）
		String subcode = ""; // 子号码（可选）
		//String msgid = UUID.randomUUID().toString().replace("-", ""); // 短信id，查询短信状态报告时需要，（可选）
		String sendtime = ""; // 定时发送时间（可选）
		String smsCode = SMSUtils.randomSMSCode(6);
		String phone = username;
		String content = "提现验证码:"+smsCode + "，有效期10分钟。";// 短信内容（必填）
		//检查手机的有效性
		if (!SMSUtils.isMobileNo(username)) {
	        renderArgumentError("请检查手机号是否正确！");
	        return;
	    }
        if (StringUtils.isEmpty(username)) {
            renderArgumentError("手机不能为空！");
            return;
        }
        
        if (!memberService.usernameExists(username)) {
			renderArgumentError("手机号未注册");
			return;
		}
        if (smscode!=null) {
            getSession().removeAttribute("smscode");
         }
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
				setSessionAttr("smscode", smsCode);
				//setAttr("success", "YES");
				 //TimerTask实现5分钟后从session中删除checkCode
	            final Timer timer=new Timer();
	            timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	            getSession().removeAttribute("smscode");
	            System.out.println("smscode删除成功");
	            timer.cancel();
	                }
	            },10*60*1000);
	            renderJson(new BaseResponse("短信发送成功"));
			}else{
			    //setAttr("success", "NO");
			    renderArgumentError("服务器异常");
			}
		} catch (URIException e) {

			LOG.error("应用异常", e);
		}
	}*/
    
	/*public void sendLoginSms() {
		String username = getPara("username");
		String smsCode = SMSUtils.randomSMSCode(4);
		
		if (!notNull(Require.me().put(username, "手机不能为空！").put(smsCode, "验证码不能为空！"))) {
            return;
        }
		
		if (memberService.usernameDisabled(username)) {
			renderArgumentError("此账号已被禁用");
			return;
		} 
		
		if (!memberService.usernameExists(username)) {
			renderArgumentError("此账号不存在");
			return;
		}
		
		// 保存
		smsService.saveOrUpdate(username, smsCode, Setting.SmsType.memberLogin);
		JSONObject result = smsService.sendSmsVerifyCode(username, smsCode);
		if (result != null && !result.getBoolean("success")) {
			renderArgumentError("短信太忙了[" + result.getString("sub_msg") + "]");
			return;
		} 
		renderJson(new BaseResponse("短信发送成功"));
	}*/
	
	/**
	 * 登录提交  手机号
	 *     
	 */
    public void login() {
    	//HttpServletRequest request = getRequest();
		//HttpServletResponse response = getResponse();
    	String username = getPara("username");
    	//Key
    	String ID = username + "code";
    	String code = getPara("smsCode");
    	//默认密码
    	String psw = username.substring(username.length()-4, username.length());
    	String password = psw;
    	String smsCode = (String) getSession().getAttribute(ID);
    	LOG.info("session当中的"+smsCode);
    	//String smsCode = "1234";
    	//String realPassword = rsaService.decryptParameter("password", request);
    	//rsaService.removePrivateKey(request);
    	if (smsCode==null) {
    		renderArgumentError("请先获取短信验证码");
            return;
        }
    	 //校验必填项参数
   		if(!notNull(Require.me().put(username, "手机不能为空！").put(code, "验证码不能为空！"))){
   			return;
   		}
   		
   		//检查手机号码有效性
           if (!SMSUtils.isMobileNo(username)) {
               renderArgumentError("请检查手机号是否正确！");
               return;
           }
         //检查验证码有效性 
   		if (!code.equals(smsCode)) {
   			renderArgumentError("验证码输入错误");
   			return;
   		}
    	//判定是否注册的业务处理 如果已注册直接登录 else 先注册后登录
    	Boolean b = memberService.usernameExists(username);
    	if(b){
        Member member = memberService.findByUsername(username);
        if (member == null) {
        	if (memberService.usernameDisabled(username) || !memberService.usernameExists(username)) {
    			renderArgumentError("用户名被禁用或未注册");
    			return;
    		}
    		
    		Setting setting = SystemUtils.getSetting();
    		/*if (!setting.getIsRegisterEnabled()) {
    			renderJson("会员注册功能已关闭");
    			return;
    		}*/
    		
    		/*if (username.length() < setting.getUsernameMinLength() || username.length() > setting.getUsernameMaxLength()) {
    			renderArgumentError("用户名长度在" + setting.getUsernameMinLength() + "-" + setting.getUsernameMaxLength());
    			return;
    		}*/
    		
    		/*if (!code.equals(smsCode)) {
    			renderArgumentError("验证码输入错误");
    			return;
    		}*/
/*    		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
    			renderArgumentError("密码长度在" + setting.getPasswordMinLength() + "-" + setting.getPasswordMaxLength());
    			return;
    		}*/
    		
    		member = new Member();
    		member.setUsername(StringUtils.lowerCase(username));
    		member.setPassword(DigestUtils.md5Hex(psw));
    		member.setNickname(StringUtils.lowerCase(username));
    		member.setPoint(0L);
    		member.setBalance(BigDecimal.ZERO);
    		member.setAmount(BigDecimal.ZERO);
    		member.setIsEnabled(true);
    		member.setIsLocked(false);
    		member.setLoginFailureCount(0);
    		member.setLockedDate(null);
    		member.setRegisterIp(getRequest().getRemoteAddr());
    		member.setLoginIp(getRequest().getRemoteAddr());
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
    		member.setAvatar(AVATAR);
    		memberService.save(member);

    		if (setting.getRegisterPoint() > 0) {
    			memberService.addPoint(member, setting.getRegisterPoint(), PointLog.Type.reward, null, null);
    		}
		}
		if (!member.getIsEnabled()) {
			renderArgumentError("此账号已被禁用");
			return;
		}
		
		Setting setting = SystemUtils.getSetting();
		if (member.getIsLocked()) {
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
				int loginFailureLockTime = setting.getAccountLockTime();
				if (loginFailureLockTime == 0) {
					renderArgumentError("此账号已被锁定");
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
					renderArgumentError("此账号已被锁定");
					return;
				}
			} else {
				member.setLoginFailureCount(0);
				member.setIsLocked(false);
				member.setLockedDate(null);
				memberService.update(member);
			}
		}
		
/*		if (!DigestUtils.md5Hex(password).equals(member.getPassword())) {
			
		}*/
		//验证码输入错误的账户锁定
		if (!code.equals(smsCode)) {
			int loginFailureCount = member.getLoginFailureCount() + 1;
			if (loginFailureCount >= setting.getAccountLockCount()) {
				member.setIsLocked(true);
				member.setLockedDate(DateUtil.date());
			}
			member.setLoginFailureCount(loginFailureCount);
			memberService.update(member);
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
				renderArgumentError("验证码错误，若连续" + setting.getAccountLockCount() + "次验证码错误账号将被锁定");
				return;
			} else {
				renderArgumentError("用户名或验证码错误");
				return;
			}
		}
		member.setLoginIp(getRequest().getRemoteAddr());
		member.setLoginDate(new Date());
		member.setLoginFailureCount(0);
		memberService.update(member);
		
        LoginResponse Lresponse = new LoginResponse();
        Member pMember = new Member();
        pMember.setId(member.getId());
        pMember.setUsername(member.getUsername());
        pMember.setAvatar(member.getAvatar());
        pMember.setNickname(member.getNickname());
        pMember.setOpenId(member.getOpenId() == null ? "" : member.getOpenId());
        Lresponse.setMessage("登录成功");
        Lresponse.setInfo(pMember);
        Lresponse.setImageUrl(setting.getImageUrl());
        Lresponse.setToken(TokenManager.getMe().generateToken(member));
        if(StrKit.notBlank(Lresponse.getToken())) {
        	setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, TokenManager.getMe().validate(Lresponse.getToken()));
        }
       /* LOG.info("登录成功否！");
        System.out.println("终于成功了");*/
		renderJson(Lresponse);
       }else{
   		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
   			renderArgumentError("用户名被禁用或已被注册");
   			return;
   		}
   		
   		Setting setting = SystemUtils.getSetting();
   		if (!setting.getIsRegisterEnabled()) {
   			renderJson("会员注册功能已关闭");
   			return;
   		}
   		
   		if (username.length() < setting.getUsernameMinLength() || username.length() > setting.getUsernameMaxLength()) {
   			renderArgumentError("用户名长度在" + setting.getUsernameMinLength() + "-" + setting.getUsernameMaxLength());
   			return;
   		}
   		
   		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
   			renderArgumentError("密码长度在" + setting.getPasswordMinLength() + "-" + setting.getPasswordMaxLength());
   			return;
   		}
   		
   		Member member = new Member();
   		member.setUsername(StringUtils.lowerCase(username));
   		member.setPassword(DigestUtils.md5Hex(psw));
   		member.setNickname(null);
   		member.setPoint(0L);
   		member.setBalance(BigDecimal.ZERO);
   		member.setAmount(BigDecimal.ZERO);
   		member.setIsEnabled(true);
   		member.setIsLocked(false);
   		member.setLoginFailureCount(0);
   		member.setLockedDate(null);
   		member.setRegisterIp(getRequest().getRemoteAddr());
   		member.setLoginIp(getRequest().getRemoteAddr());
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
   		//smsService.delete(username, code);
   		LoginResponse response = new LoginResponse();
   		response.setToken(TokenManager.getMe().generateToken(member));
   		setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, TokenManager.getMe().validate(response.getToken()));
   		//renderJson(new BaseResponse("注册成功，请及时修改密码"));
   		
   	   //注册直接登录
   	  Member member1 = memberService.findByUsername(username);
      if (member1 == null) {
      	if (memberService.usernameDisabled(username) || !memberService.usernameExists(username)) {
  			renderArgumentError("用户名被禁用或未注册");
  			return;
  		}
  		
  		//Setting setting = SystemUtils.getSetting();
  		if (!setting.getIsRegisterEnabled()) {
  			renderJson("会员注册功能已关闭");
  			return;
  		}
  		
  		if (username.length() < setting.getUsernameMinLength() || username.length() > setting.getUsernameMaxLength()) {
  			renderArgumentError("用户名长度在" + setting.getUsernameMinLength() + "-" + setting.getUsernameMaxLength());
  			return;
  		}
  		
  		if (!code.equals(smsCode)) {
  			renderArgumentError("验证码输入错误");
  			return;
  		}
    /*		if (psw.length() < setting.getPasswordMinLength() || psw.length() > setting.getPasswordMaxLength()) {
  			renderArgumentError("密码长度在" + setting.getPasswordMinLength() + "-" + setting.getPasswordMaxLength());
  			return;
  		}*/
  		
      	member1 = new Member();
      	member1.setUsername(StringUtils.lowerCase(username));
  		//member.setPassword(DigestUtils.md5Hex(password));
      	member1.setNickname(StringUtils.lowerCase(username));
      	member1.setPoint(0L);
  		member1.setBalance(BigDecimal.ZERO);
  		member1.setAmount(BigDecimal.ZERO);
  		member1.setIsEnabled(true);
  		member1.setIsLocked(false);
  		member1.setLoginFailureCount(0);
  		member1.setLockedDate(null);
  		member1.setRegisterIp(getRequest().getRemoteAddr());
  		member1.setLoginIp(getRequest().getRemoteAddr());
  		member1.setLoginDate(new Date());
  		member1.setLoginPluginId(null);
  		member1.setOpenId(null);
  		member1.setLockKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
  		member1.setSafeKey(null);
  		member1.setMemberRankId(memberRankService.findDefault().getId());
  		member1.setCart(null);
  		member1.setOrders(null);
  		member1.setPaymentLogs(null);
  		member1.setDepositLogs(null);
  		member1.setCouponCodes(null);
  		member1.setReceivers(null);
  		member1.setReviews(null);
  		member1.setConsultations(null);
  		member1.setFavoriteGoods(null);
  		member1.setProductNotifies(null);
  		member1.setInMessages(null);
  		member1.setOutMessages(null);
  		member1.setPointLogs(null);
  		member1.setAvatar(AVATAR);
  		memberService.save(member1);

  		if (setting.getRegisterPoint() > 0) {
  			memberService.addPoint(member1, setting.getRegisterPoint(), PointLog.Type.reward, null, null);
  		}
		}
		if (!member1.getIsEnabled()) {
			renderArgumentError("此账号已被禁用");
			return;
		}
		
		//Setting setting = SystemUtils.getSetting();
		if (member1.getIsLocked()) {
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
				int loginFailureLockTime = setting.getAccountLockTime();
				if (loginFailureLockTime == 0) {
					renderArgumentError("此账号已被锁定");
					return;
				}
				Date lockedDate = member1.getLockedDate();
				Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
				if (new Date().after(unlockDate)) {
					member1.setLoginFailureCount(0);
					member1.setIsLocked(false);
					member1.setLockedDate(null);
					memberService.update(member1);
				} else {
					renderArgumentError("此账号已被锁定");
					return;
				}
			} else {
				member1.setLoginFailureCount(0);
				member1.setIsLocked(false);
				member1.setLockedDate(null);
				memberService.update(member1);
			}
		}
		
		if (!DigestUtils.md5Hex(password).equals(member.getPassword())) {
			
		}
		//验证码输入错误的账户锁定
		if (!code.equals(smsCode)) {
			int loginFailureCount = member1.getLoginFailureCount() + 1;
			if (loginFailureCount >= setting.getAccountLockCount()) {
				member1.setIsLocked(true);
				member1.setLockedDate(DateUtil.date());
			}
			member1.setLoginFailureCount(loginFailureCount);
			memberService.update(member1);
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
				renderArgumentError("验证码错误，若连续" + setting.getAccountLockCount() + "次验证码错误账号将被锁定");
				return;
			} else {
				renderArgumentError("用户名或验证码错误");
				return;
			}
		}
		  member1.setLoginIp(getRequest().getRemoteAddr());
		  member1.setLoginDate(new Date());
		  member1.setLoginFailureCount(0);
		  memberService.update(member1);
			
	      LoginResponse Lresponse = new LoginResponse();
	      Member pMember = new Member();
	      pMember.setId(member1.getId());
	      pMember.setUsername(member1.getUsername());
	      pMember.setAvatar(member1.getAvatar());
	      pMember.setNickname(member1.getNickname());
	      pMember.setOpenId(member1.getOpenId() == null ? "" : member1.getOpenId());
	      Lresponse.setMessage("注册完毕，登录成功");
	      Lresponse.setInfo(pMember);
	      Lresponse.setImageUrl(setting.getImageUrl());
	      Lresponse.setToken(TokenManager.getMe().generateToken(member1));
	      if(StrKit.notBlank(Lresponse.getToken())) {
	      	setSessionAttr(Member.PRINCIPAL_ATTRIBUTE_NAME, TokenManager.getMe().validate(Lresponse.getToken()));
	      }
	      LOG.info("登录成功否！");
	      System.out.println("终于成功了");
		  renderJson(Lresponse);
   		
       }
    }
}

