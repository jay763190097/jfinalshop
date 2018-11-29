package com.jfinalshop.plugin.weixinPayment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.util.WebUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi.TradeType;
import com.jfinalshop.Setting;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.RandomUtils;
import com.jfinalshop.util.SystemUtils;

/**
 * Plugin - 微信支付
 * 
 * 
 */
public class WeixinPaymentPlugin extends PaymentPlugin {

	@Override
	public String getName() {
		return "微信支付";
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
		return "weixin_payment/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "weixin_payment/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "weixin_payment/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
		return "https://api.mch.weixin.qq.com/pay/unifiedorder";
	}

	@Override
	public RequestMethod getRequestMethod() {
		return PaymentPlugin.RequestMethod.get;
	}

	@Override
	public String getRequestCharset() {
		return "UTF-8";
	}

	@Override
	public Map<String, Object> getParameterMap(String sn, String description, HttpServletRequest request) {
		Setting setting = SystemUtils.getSetting();
		PluginConfig pluginConfig = getPluginConfig();
		PaymentLog paymentLog = getPaymentLog(sn);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("appid", pluginConfig.getAttribute("appid")); // 公众账号ID
		parameterMap.put("mch_id", pluginConfig.getAttribute("mch_id")); // 商户号
		parameterMap.put("nonce_str", RandomUtils.randomUpperWords(32)); // 随机字符串
		parameterMap.put("_input_charset", "utf-8");
		parameterMap.put("sign", generateSign(parameterMap));
		parameterMap.put("body", StringUtils.abbreviate(description.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 600)); // 商品描述
		parameterMap.put("out_trade_no", sn); // 商户订单号
		parameterMap.put("total_fee", paymentLog.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());// 订单总金额
		String ip = IpUtil.getIpAddr(request);
		if (StrKit.isBlank(ip) || ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "192.168.59.1";
		}
		parameterMap.put("spbill_create_ip", ip); // 终端IP
		parameterMap.put("notify_url", setting.getSiteUrl() + "/wap/payment/paymentNotify.jhtml"); // 通知地址
		parameterMap.put("trade_type", TradeType.JSAPI.name()); // 交易类型
		parameterMap.put("attach", getId()); // 插件id
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
		PaymentLog paymentLog = getPaymentLog(request.getParameter("out_trade_no"));
		if (paymentLog != null && generateSign(request.getParameterMap()).equals(request.getParameter("sign")) && ("TRADE_SUCCESS".equals(request.getParameter("trade_status")) || "TRADE_FINISHED".equals(request.getParameter("trade_status"))) && paymentLog.getAmount().compareTo(new BigDecimal(request.getParameter("total_fee"))) == 0) {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			if ("true".equals(WebUtils.post("https://api.mch.weixin.qq.com/pay/unifiedorder", parameterMap))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getSn(HttpServletRequest request) {
		return request.getParameter("out_trade_no");
	}

	@Override
	public String getNotifyMessage(PaymentPlugin.NotifyMethod notifyMethod, HttpServletRequest request) {
		if (PaymentPlugin.NotifyMethod.async.equals(notifyMethod)) {
			return "success";
		}
		return null;
	}

	/**
	 * 生成签名
	 *
	 * @param parameterMap
	 *            参数
	 * @return 签名
	 */
	private String generateSign(Map<String, ?> parameterMap) {
		PluginConfig pluginConfig = getPluginConfig();
		return DigestUtils.md5Hex(joinKeyValue(new TreeMap<String, Object>(parameterMap), null, pluginConfig.getAttribute("key"), "&", true, "sign_type", "sign"));
	}
}
