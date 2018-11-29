package com.jfinalshop.controller.wap;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jfinalshop.Setting;
import com.jfinalshop.entity.AjaxResult;
import com.jfinalshop.interceptor.WapInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentLogService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 支付
 * 
 * 
 */
@ControllerBind(controllerKey = "/wap/payment")
@Before(WapInterceptor.class)
public class PaymentController extends BaseController {

	@Inject
	private OrderService orderService;
	@Inject
	private MemberService memberService;
	@Inject
	private PluginService pluginService;
	@Inject
	private PaymentLogService paymentLogService;
	private Res resZh = I18n.use();
	private AjaxResult ajax = new AjaxResult();
	
	public static final String RETURN_CODE = "return_code";
	public static final String RETURN_MSG = "return_msg";
	public static final String RESULT_CODE = "result_code";
	
	
	/**
	 * 第一步获取的code参数
	 * 
	 */
	public void getWeixinCode() {
		String sn = getPara("sn");
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("weixinPaymentPlugin");
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		
		Setting setting = SystemUtils.getSetting();
		String notify_url = setting.getSiteUrl() + "/wap/payment/weixinCodeNotify.jhtml";
		String url = SnsAccessTokenApi.getAuthorizeURL(pluginConfig.getAttribute("appid"), notify_url, sn, true);
		
		try {
			getResponse().sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderNull();
	}
	
	/**
	 * 支付订单 - 支付页面
	 * 同时也接收微信CODE
	 * 
	 */
	public void weixinCodeNotify() {
		String code = getPara("code"); // 微信CODE
		String sn = getPara("state"); // 订单号
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("weixinPaymentPlugin");
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		
		// 通过code获取access_token
		SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(pluginConfig.getAttribute("appid"), pluginConfig.getAttribute("appSecret"), code);
		String openId = snsAccessToken.getOpenid();
		Member member = memberService.getCurrent();
		if (member != null && StrKit.notBlank(openId)) {
			member.setOpenId(openId);
			member.update();
		}
		redirect("/wap/order/payment.jhtml?sn=" + sn);
	}
	
	
	/**
	 * 插件提交
	 */ 
	public void submit() {
		String typeName = getPara("type", "");
		PaymentLog.Type type = StrKit.notBlank(typeName) ? PaymentLog.Type.valueOf(typeName) : null;
		String paymentPluginId = getPara("paymentPluginId");
		
		String sn = getPara("sn");
		String amountStr = getPara("amount");
		BigDecimal amount = StrKit.notBlank(amountStr) ? new BigDecimal(amountStr) : null;
		
		if (type == null) {
			return;
		}
		Member member = memberService.getCurrent();
		if (member == null) {
			return;
		}
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			return;
		}
		
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		switch (type) {
			case recharge: {
				if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.precision() > 15 || amount.scale() > setting.getPriceScale()) {
					return;
				}
				PaymentLog paymentLog = new PaymentLog();
				paymentLog.setSn(null);
				paymentLog.setType(type.ordinal());
				paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
				paymentLog.setFee(paymentPlugin.calculateFee(amount));
				paymentLog.setAmount(paymentPlugin.calculateAmount(amount));
				paymentLog.setPaymentPluginId(paymentPluginId);
				paymentLog.setPaymentPluginName(paymentPlugin.getName());
				paymentLog.setMemberId(member.getId());
				paymentLog.setOrder(null);
				paymentLogService.save(paymentLog);
				parameterMap = paymentPlugin.getParameterMap(paymentLog.getSn(), resZh.format("shop.payment.rechargeDescription"), getRequest());
				break;
			}
			case payment: {
				Order order = orderService.findBySn(sn);
				if (order == null || !member.equals(order.getMember()) || orderService.isLocked(order, member, true)) {
					return;
				}
				if (order.getPaymentMethod() == null || !PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
					return;
				}
				if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
					return;
				}
				
				PaymentLog paymentLog = new PaymentLog();
				paymentLog.setSn(null);
				paymentLog.setType(type.ordinal());
				paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
				paymentLog.setFee(paymentPlugin.calculateFee(order.getAmount()));
				//paymentLog.setAmount(paymentPlugin.calculateAmount(order.getAmount()));
				paymentLog.setAmount(paymentPlugin.calculateAmount(order.getAmountPayable())); // 改成取【应付金额】
				paymentLog.setPaymentPluginId(paymentPluginId);
				paymentLog.setPaymentPluginName(paymentPlugin.getName());
				paymentLog.setOrderId(order.getId());
				paymentLog.setMember(null);
				paymentLogService.save(paymentLog);
				parameterMap = paymentPlugin.getParameterMap(paymentLog.getSn(), resZh.format("shop.payment.paymentDescription", order.getSn()), getRequest());
				break;
			}
		}
		Map<String, String> params = convertAttributes(parameterMap);
		params.put("openid", member.getOpenId());
		params.put("sign",  PaymentKit.createSign(params, pluginConfig.getAttribute("paternerKey"))); // 签名
		
		// 统一下单
		String xmlResult = PaymentApi.pushOrder(params);

		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		String returnCode = result.get(RETURN_CODE);
		String returnMsg = result.get(RETURN_MSG);
		if (StrKit.isBlank(returnCode) || !StringUtils.equals("SUCCESS", returnCode)) {
			ajax.addError(returnMsg);
			renderJson(ajax);
			return;
		}
		String resultCode = result.get(RESULT_CODE);
		if (StrKit.isBlank(resultCode) || !StringUtils.equals("SUCCESS", returnCode)) {
			ajax.addError(returnMsg);
			renderJson(ajax);
			return;
		}
		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		String prepay_id = result.get("prepay_id");

		Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appId", pluginConfig.getAttribute("appid"));
		packageParams.put("timeStamp", System.currentTimeMillis() / 1000 + "");
		packageParams.put("nonceStr", System.currentTimeMillis() + "");
		packageParams.put("package", "prepay_id=" + prepay_id);
		packageParams.put("signType", "MD5");
		String packageSign = PaymentKit.createSign(packageParams, pluginConfig.getAttribute("paternerKey"));
		packageParams.put("paySign", packageSign);

		String jsonStr = JsonUtils.toJson(packageParams);
		ajax.success(jsonStr);
		LogKit.info("返回ajax: " + ajax);
		renderJson(ajax);
	}
	
	/**
	 * 插件通知
	 * 支付结果通用通知文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
	 */
	public void paymentNotify() {
		String xmlMsg = HttpKit.readData(getRequest());
		LogKit.info("支付通知=" + xmlMsg);
		
		Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
		String total_fee = params.get("total_fee"); // 总金额
		String transaction_id = params.get("transaction_id"); // 微信支付订单号
		String out_trade_no = params.get("out_trade_no"); // 商户订单号
		
		// 以下是附加参数
		String attach = params.get("attach");
		
		// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态 避免已经成功、关闭、退款的订单被再次更新
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(attach);
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		if (paymentPlugin != null && PaymentKit.verifyNotify(params, pluginConfig.getAttribute("paternerKey"))) {
			PaymentLog paymentLog = paymentLogService.findBySn(out_trade_no);
			if (paymentLog != null) {
				LogKit.info("微信支付订单号:" + transaction_id + ",总金额:" + total_fee + "分");
				// 执行更新订单
				paymentLogService.handle(paymentLog);
				
				//发送通知等
				Map<String, String> xml = new HashMap<String, String>();
				xml.put(RETURN_CODE, "SUCCESS");
				xml.put(RETURN_MSG, "OK");
				renderText(PaymentKit.toXml(xml));
				return;
			}
		}
		renderText("");
	}
	
	/**
	 * 支付成功
	 * 
	 */
	public void success() {
		String sn = getPara("sn");
		Boolean success = getParaToBoolean("success", false);
		Order order = orderService.findBySn(sn);
		setAttr("order" , order);
		setAttr("title" , success ? "订单支付成功" : "订单支付失败");
		render("/wap/order/success.ftl");
	}
	
	/**
	 * 充值支付结果
	 * 
	 */
	public void result() {
		Boolean success = getParaToBoolean("success", false);
		setAttr("title" , success ? "充值成功" : "充值失败");
		render("/wap/order/result.ftl");
	}
	
	/**
	 * Object convert String
	 * 
	 * @param parameterMap
	 *            参数
	 * @return 签名
	 */
	private Map<String, String> convertAttributes(Map<String, Object> parameterMap) {
		Map<String, String> parameterNewMap = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
			if (entry.getValue() instanceof String) {
				parameterNewMap.put(entry.getKey(), (String) entry.getValue());
			}
		}
		return parameterNewMap;
	}
	
}
