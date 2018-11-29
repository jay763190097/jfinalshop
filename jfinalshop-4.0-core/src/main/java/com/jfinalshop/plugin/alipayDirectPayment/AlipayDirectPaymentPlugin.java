package com.jfinalshop.plugin.alipayDirectPayment;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.util.WebUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.Setting;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.util.SystemUtils;

/**
 * Plugin - 支付宝(即时交易)
 * 
 * 
 */
public class AlipayDirectPaymentPlugin extends PaymentPlugin {

	@Override
	public String getName() {
		return "支付宝(即时交易)";
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
		return "alipay_direct_payment/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "alipay_direct_payment/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "alipay_direct_payment/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
		//return "https://mapi.alipay.com/gateway.do";
				return "https://mapi.alipay.com/gateway.do";
	}

	@Override
	public PaymentPlugin.RequestMethod getRequestMethod() {
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
		parameterMap.put("service", "create_direct_pay_by_user"); // 接口名称
		parameterMap.put("partner", pluginConfig.getAttribute("partner")); // 签约的支付宝账号对应的支付宝唯一用户号,以2088开头的16位纯数字组成。
		parameterMap.put("_input_charset", "utf-8"); // 商户网站使用的编码格式，如UTF-8、GBK、GB2312等。
		parameterMap.put("sign_type", "MD5"); // DSA、RSA、MD5三个值可选，必须大写。
		parameterMap.put("return_url", getNotifyUrl(PaymentPlugin.NotifyMethod.sync)); // 支付宝处理完请求后，当前页面自动跳转到商户网站里指定页面的http路径。
		parameterMap.put("notify_url", getNotifyUrl(PaymentPlugin.NotifyMethod.async)); // 支付宝服务器主动通知商户网站里指定的页面http路径。
		parameterMap.put("out_trade_no", sn); // 支付宝合作商户网站唯一订单号。
		parameterMap.put("subject", StringUtils.abbreviate(description.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 60)); // 商品的标题/交易标题/订单标题/订单关键字等。该参数最长为128个汉字。
		parameterMap.put("body", StringUtils.abbreviate(description.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 600)); // 商品描述
		parameterMap.put("payment_type", "1"); // 只支持取值为1（商品购买）。
		parameterMap.put("seller_id", pluginConfig.getAttribute("partner")); // 卖家支付宝用户号
		parameterMap.put("total_fee", paymentLog.getAmount().setScale(2).toString()); // 该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01，100000000.00]，精确到小数点后两位。
		parameterMap.put("show_url", setting.getSiteUrl()); // 商品展示网址
		parameterMap.put("paymethod", "directPay");// 取值范围：creditPay（信用支付）directPay（余额支付）如果不设置，默认识别为余额支付。必须注意区分大小写。
		parameterMap.put("extend_param", "isv^1860648a1");
		parameterMap.put("exter_invoke_ip", request.getLocalAddr()); // 客户端IP
		parameterMap.put("extra_common_param", "qwerty"); //公用回传参数
		parameterMap.put("sign", generateSign(parameterMap)); // 签名
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
		
		//Map<String, String[]> map = transCodeMap(request.getParameterMap());
		//LogKit.info(">>>" + generateSign(map));
		//LogKit.info(">>>" + request.getParameter("sign"));
		if (paymentLog != null 
				/*&& generateSign(request.getParameterMap()).equals(request.getParameter("sign"))*/ 
				&& pluginConfig.getAttribute("partner").equals(request.getParameter("seller_id"))
				&& ("TRADE_SUCCESS".equals(request.getParameter("trade_status")) 
			    || "TRADE_FINISHED".equals(request.getParameter("trade_status"))) 
			    && paymentLog.getAmount().compareTo(new BigDecimal(request.getParameter("total_fee"))) == 0) {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("service", "notify_verify");
			parameterMap.put("partner", pluginConfig.getAttribute("partner"));
			parameterMap.put("notify_id", request.getParameter("notify_id"));
			if ("true".equals(WebUtils.post("https://mapi.alipay.com/gateway.do", parameterMap))) {
				return true;
			}
			return true;
		}
		return false;
	}

	public Map<String, String[]> transCodeMap(Map<String, String[]> map) {
		Map<String, String[]> newmap = new HashMap<String, String[]>();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			String name = entry.getKey();
			String values[] = entry.getValue();

			if (values == null) {
				newmap.put(name, new String[] {});
				continue;
			}
			String newvalues[] = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				try {
					value = new String(value.getBytes("iso8859-1"), "utf-8");
					newvalues[i] = value; // 解决乱码后封装到Map中
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			newmap.put(name, newvalues);
		}
		return newmap;
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