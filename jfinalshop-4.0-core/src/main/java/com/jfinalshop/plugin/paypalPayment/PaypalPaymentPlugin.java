package com.jfinalshop.plugin.paypalPayment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.util.WebUtils;

/**
 * Plugin - Paypal
 * 
 * 
 */
public class PaypalPaymentPlugin extends PaymentPlugin {

	/**
	 * 货币
	 */
	public enum Currency {

		/** 美元 */
		USD,

		/** 澳大利亚元 */
		AUD,

		/** 加拿大元 */
		CAD,

		/** 捷克克郎 */
		CZK,

		/** 丹麦克朗 */
		DKK,

		/** 欧元 */
		EUR,

		/** 港元 */
		HKD,

		/** 匈牙利福林 */
		HUF,

		/** 新西兰元 */
		NZD,

		/** 挪威克朗 */
		NOK,

		/** 波兰兹罗提 */
		PLN,

		/** 英镑 */
		GBP,

		/** 新加坡元 */
		SGD,

		/** 瑞典克朗 */
		SEK,

		/** 瑞士法郎 */
		CHF,

		/** 日元 */
		JPY
	}

	@Override
	public String getName() {
		return "Paypal";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "JFinalShop";
	}

	@Override
	public String getSiteUrl() {
		return "http://www.jfinalshop.com";
	}

	@Override
	public String getInstallUrl() {
		return "paypal_payment/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "paypal_payment/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "paypal_payment/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
		return "https://www.paypal.com/cgi-bin/webscr";
	}

	@Override
	public PaymentPlugin.RequestMethod getRequestMethod() {
		return PaymentPlugin.RequestMethod.post;
	}

	@Override
	public String getRequestCharset() {
		return "UTF-8";
	}

	@Override
	public Map<String, Object> getParameterMap(String sn, String description, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		PaymentLog paymentLog = getPaymentLog(sn);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("cmd", "_xclick");
		parameterMap.put("business", pluginConfig.getAttribute("partner"));
		parameterMap.put("item_name", StringUtils.abbreviate(description.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 100));
		parameterMap.put("amount", paymentLog.getAmount().setScale(2).toString());
		parameterMap.put("currency_code", pluginConfig.getAttribute("currency"));
		parameterMap.put("return", getNotifyUrl(PaymentPlugin.NotifyMethod.sync));
		parameterMap.put("notify_url", getNotifyUrl(PaymentPlugin.NotifyMethod.async));
		parameterMap.put("invoice", sn);
		parameterMap.put("charset", "UTF-8");
		parameterMap.put("no_shipping", "1");
		parameterMap.put("no_note", "0");
		parameterMap.put("rm", "2");
		parameterMap.put("custom", "shopxx");
		return parameterMap;
	}

	@Override
	public JSONObject getParameterMaps(String sn, String description, HttpServletRequest request) {
		return null;
	}

	@Override
	public String getResult(String sn, String description, HttpServletRequest request) throws Exception {
		return null;
	}

	@Override
	public boolean verifyNotify(PaymentPlugin.NotifyMethod notifyMethod, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		PaymentLog paymentLog = getPaymentLog(request.getParameter("invoice"));
		if (paymentLog != null && pluginConfig.getAttribute("partner").equals(request.getParameter("receiver_email")) && pluginConfig.getAttribute("currency").equals(request.getParameter("mc_currency")) && "Completed".equals(request.getParameter("payment_status"))
				&& paymentLog.getAmount().compareTo(new BigDecimal(request.getParameter("mc_gross"))) == 0) {
			Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
			parameterMap.put("cmd", "_notify-validate");
			parameterMap.putAll(request.getParameterMap());
			if ("VERIFIED".equals(WebUtils.post("https://www.paypal.com/cgi-bin/webscr", parameterMap))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getSn(HttpServletRequest request) {
		return request.getParameter("invoice");
	}

	@Override
	public String getNotifyMessage(PaymentPlugin.NotifyMethod notifyMethod, HttpServletRequest request) {
		return null;
	}

}