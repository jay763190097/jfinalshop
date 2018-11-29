package com.jfinalshop.controller.admin;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.AuthenticationFailedException;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.Setting.AccountLockType;
import com.jfinalshop.Setting.CaptchaType;
import com.jfinalshop.Setting.ConsultationAuthority;
import com.jfinalshop.Setting.ReviewAuthority;
import com.jfinalshop.Setting.RoundType;
import com.jfinalshop.Setting.StockAllocationTime;
import com.jfinalshop.Setting.WatermarkPosition;
import com.jfinalshop.service.CacheService;
import com.jfinalshop.service.FileService;
import com.jfinalshop.service.MailService;
import com.jfinalshop.service.SmsService;
import com.jfinalshop.service.StaticService;
import com.jfinalshop.util.ObjectUtils;
import com.jfinalshop.util.SystemUtils;
import com.sun.mail.smtp.SMTPSenderFailedException;

/**
 * Controller - 系统设置
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/setting")
public class SettingController extends BaseController {

	@Inject
	private FileService fileService;
	@Inject
	private MailService mailService;
	@Inject
	private SmsService smsService;
	@Inject
	private CacheService cacheService;
	@Inject
	private StaticService staticService;

	/**
	 * SMTP测试
	 */
	public void testSmtp() {
		String smtpHost = getPara("smtpHost");
		Integer smtpPort = getParaToInt("smtpPort");
		String smtpUsername = getPara("smtpUsername");
		String smtpPassword = getPara("smtpPassword");
		Boolean smtpSSLEnabled = getParaToBoolean("smtpSSLEnabled");
		String smtpFromMail = getPara("smtpFromMail");
		String toMail = getPara("toMail");
		if (StringUtils.isEmpty(toMail)) {
			renderJson(ERROR_MESSAGE);
			return;
		}

		Setting setting = SystemUtils.getSetting();
		try {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("smtpHost", smtpHost);
			properties.put("smtpPort", smtpPort);
			properties.put("smtpUsername", smtpUsername);
			properties.put("smtpSSLEnabled", smtpSSLEnabled);
			properties.put("smtpFromMail", smtpFromMail);
			
			mailService.sendTestSmtpMail(smtpHost, smtpPort, smtpUsername, StringUtils.isNotEmpty(smtpPassword) ? smtpPassword : setting.getSmtpPassword(), smtpSSLEnabled, smtpFromMail, toMail);
		} catch (Exception e) {
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			if (rootCause != null) {
				if (rootCause instanceof UnknownHostException) {
					renderJson(Message.error("admin.setting.testSmtpUnknownHost"));
					return;
				} else if (rootCause instanceof ConnectException || rootCause instanceof SocketTimeoutException) {
					renderJson(Message.error("admin.setting.testSmtpConnectFailed"));
					return;
				} else if (rootCause instanceof AuthenticationFailedException) {
					renderJson(Message.error("admin.setting.testSmtpAuthenticationFailed"));
					return;
				} else if (rootCause instanceof SMTPSenderFailedException) {
					renderJson(Message.error("admin.setting.testSmtpSenderFailed"));
					return;
				}
			}
			renderJson(Message.error("admin.setting.testSmtpFailed"));
			return;
		}
		renderJson(Message.success("admin.setting.testSmtpSuccess"));
	}

	/**
	 * 短信余额查询
	 */
	public void smsBalance() {
		long balance = smsService.getBalance();
		if (balance < 0) {
			renderJson(Message.warn("admin.setting.smsInvalid"));
			return;
		}
		renderJson(Message.success("admin.setting.smsBalanceResult", balance));
	}

	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("setting", SystemUtils.getSetting());
		setAttr("locales", Setting.Locale.values());
		setAttr("watermarkPositions", Setting.WatermarkPosition.values());
		setAttr("roundTypes", Setting.RoundType.values());
		setAttr("captchaTypes", Setting.CaptchaType.values());
		setAttr("accountLockTypes", Setting.AccountLockType.values());
		setAttr("stockAllocationTimes", Setting.StockAllocationTime.values());
		setAttr("reviewAuthorities", Setting.ReviewAuthority.values());
		setAttr("consultationAuthorities", Setting.ConsultationAuthority.values());
		render("/admin/setting/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		UploadFile watermarkImageFile = getFile("watermarkImageFile");
		Setting setting = getBean(Setting.class);
		setting.setIsSiteEnabled(getParaToBoolean("isSiteEnabled", false));
		setting.setIsShowMarketPrice(getParaToBoolean("isShowMarketPrice", false));
		setting.setIsRegisterEnabled(getParaToBoolean("isRegisterEnabled", false));
		setting.setIsDuplicateEmail(getParaToBoolean("isDuplicateEmail", false));
		setting.setIsEmailLogin(getParaToBoolean("isEmailLogin", false));
		setting.setIsReviewEnabled(getParaToBoolean("isReviewEnabled", false));
		setting.setIsReviewCheck(getParaToBoolean("isReviewCheck", false));
		setting.setIsConsultationEnabled(getParaToBoolean("isConsultationEnabled", false));
		setting.setIsConsultationCheck(getParaToBoolean("isConsultationCheck", false));
		setting.setIsInvoiceEnabled(getParaToBoolean("isInvoiceEnabled", false));
		setting.setIsTaxPriceEnabled(getParaToBoolean("isTaxPriceEnabled", false));
		setting.setIsDevelopmentEnabled(getParaToBoolean("isDevelopmentEnabled", false));
		
		String watermarkPositionName = getPara("watermarkPosition");
		WatermarkPosition watermarkPosition = StrKit.notBlank(watermarkPositionName) ? WatermarkPosition.valueOf(watermarkPositionName) : null;
		setting.setWatermarkPosition(watermarkPosition);
		
		String priceRoundTypeName = getPara("priceRoundType", null);
		RoundType priceRoundType = StrKit.notBlank(priceRoundTypeName) ? RoundType.valueOf(priceRoundTypeName) : null;
		setting.setPriceRoundType(priceRoundType);
		
		String reviewAuthorityName = getPara("reviewAuthority", null);
		ReviewAuthority reviewAuthority = StrKit.notBlank(reviewAuthorityName) ? ReviewAuthority.valueOf(reviewAuthorityName) : null;
		setting.setReviewAuthority(reviewAuthority);
		
		String consultationAuthorityName = getPara("consultationAuthority", null);
		ConsultationAuthority consultationAuthority = StrKit.notBlank(consultationAuthorityName) ? ConsultationAuthority.valueOf(consultationAuthorityName) : null;
		setting.setConsultationAuthority(consultationAuthority);
		
		String stockAllocationTimeName = getPara("stockAllocationTime", null);
		StockAllocationTime stockAllocationTime = StrKit.notBlank(stockAllocationTimeName) ? StockAllocationTime.valueOf(stockAllocationTimeName) : null;
		setting.setStockAllocationTime(stockAllocationTime);
		
		// 验证码类型
		String[] captchaTypeNames = getParaValues("captchaTypes");
		if (!ObjectUtils.isEmpty(captchaTypeNames)) {
			int length = captchaTypeNames.length;
			CaptchaType [] captchaTypes = new CaptchaType [length];
			for (int i = 0; i < length; i++) {  
				captchaTypes[i] = CaptchaType.valueOf(captchaTypeNames[i]); 
			}  
			setting.setCaptchaTypes(captchaTypes);
		}
		
		// 账号锁定类型
		String [] accountLockTypeNames = getParaValues("accountLockTypes");
		if (!ObjectUtils.isEmpty(accountLockTypeNames)) {
			int length = accountLockTypeNames.length;
			AccountLockType [] accountLockTypes = new AccountLockType [length];
			for (int i = 0; i < length; i++) {  
				accountLockTypes[i] = AccountLockType.valueOf(accountLockTypeNames[i]); 
			}  
			setting.setAccountLockTypes(accountLockTypes);
		}
				
		Setting srcSetting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getSmtpPassword())) {
			setting.setSmtpPassword(srcSetting.getSmtpPassword());
		}
		if (watermarkImageFile != null && watermarkImageFile.getFile().length() <= 0) {
			if (!fileService.isValid(FileType.image, watermarkImageFile)) {
				addFlashMessage(Message.error("admin.upload.invalid"));
				redirect("edit.jhtml");
			}
			String watermarkImage = fileService.uploadLocal(FileType.image, watermarkImageFile);
			setting.setWatermarkImage(watermarkImage);
		} else {
			setting.setWatermarkImage(srcSetting.getWatermarkImage());
		}
		if (StringUtils.isEmpty(setting.getSmsSn()) || StringUtils.isEmpty(setting.getSmsKey())) {
			setting.setSmsSn(null);
			setting.setSmsKey(null);
		}
		setting.setIsCnzzEnabled(srcSetting.getIsCnzzEnabled());
		setting.setCnzzSiteId(srcSetting.getCnzzSiteId());
		setting.setCnzzPassword(srcSetting.getCnzzPassword());
		setting.setTheme(srcSetting.getTheme());
		SystemUtils.setSetting(setting);
		cacheService.clear();
		staticService.generateIndex();
		staticService.generateOther();

		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/setting/edit.jhtml");
	}

}