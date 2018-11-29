package com.jfinalshop.api.controller.member;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jfinalshop.CommonAttributes;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.entity.AjaxResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * H5支付
 * 
 * 
 */
@ControllerBind(controllerKey = "/api/payment")
public class PaymentH5APIController extends ApiController {
	private static Logger logger = LoggerFactory.getLogger(PaymentH5APIController.class);

	@Inject
	private OrderService orderService;
	@Inject
	private MemberService memberService;
	@Inject
	private PluginService pluginService;
	@Inject
	private PaymentLogService paymentLogService;
	
	public static final String WEIXIN_PAYMENT_PLUGIN = "weixinPaymentPlugin";
	
	Prop prop = PropKit.use(CommonAttributes.JFINALSHOP_PROPERTIES_PATH);
	private Boolean isTestURL = prop.getBoolean("accessToken.isTestURL", false);
	
	private Res res = I18n.use();
	private AjaxResult ajax = new AjaxResult();
	
	@Override
	public ApiConfig getApiConfig() {
		ApiConfig apiConfig = new ApiConfig();
		// 配置微信 API 相关常量
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(WEIXIN_PAYMENT_PLUGIN);
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		apiConfig.setToken(pluginConfig.getAttribute("mch_id"));
		apiConfig.setAppId(pluginConfig.getAttribute("appid"));
		apiConfig.setAppSecret(pluginConfig.getAttribute("appSecret"));
		apiConfig.setEncryptMessage(false);
		return apiConfig;
	}

	/**
	 * 插件提交
	 */ 
	public void submit() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String typeName = getPara("type", "");
		PaymentLog.Type type = StrKit.notBlank(typeName) ? PaymentLog.Type.valueOf(typeName) : null;
		String paymentPluginId = getPara("paymentPluginId");
		
		String sn = getPara("sn");
		String amountStr = getPara("amount");
		BigDecimal amount = StrKit.notBlank(amountStr) ? new BigDecimal(amountStr) : null;
		
		if (type == null) {
			renderArgumentError("支付类型不能为空! ");
			return;
		}
		
		Member member = null;
		String token = getPara("token");
        if (StrKit.notBlank(token)) {
        	member = TokenManager.getMe().validate(token);
        	if (member != null && StrKit.isBlank(member.getOpenId())) {
    			member = memberService.find(member.getId());
    		}
        }
		
		if (member == null || StrKit.isBlank(member.getOpenId())) {
			renderArgumentError("当前用户,openid不能为空! ");
			return;
		}
		
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			renderArgumentError("支付为空或未启用! ");
			return;
		}
		
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		switch (type) {
			case recharge: {
				if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.precision() > 15 || amount.scale() > setting.getPriceScale()) {
					renderArgumentError("充值金额异常! ");
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
				parameterMap = paymentPlugin.getParameterMap(paymentLog.getSn(), res.format("shop.payment.rechargeDescription"), getRequest());
				break;
			}
			case payment: {
				Order order = orderService.findBySn(sn);
				if (order == null || !member.getId().equals(order.getMemberId()) || orderService.isLocked(order, member, true)) {
					renderArgumentError("订单不能为空或订单不属于当前用户! ");
					return;
				}
				if (order.getPaymentMethod() == null || !PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
					renderArgumentError("所选方式不是在线支付! ");
					return;
				}
				if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
					renderArgumentError("应付金额小于等于0! ");
					return;
				}
				
				PaymentLog paymentLog = new PaymentLog();
				paymentLog.setSn(null);
				paymentLog.setType(type.ordinal());
				paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
				paymentLog.setFee(paymentPlugin.calculateFee(order.getAmount()));
				paymentLog.setAmount(paymentPlugin.calculateAmount(order.getAmountPayable())); // 改成取【应付金额】
				paymentLog.setPaymentPluginId(paymentPluginId);
				paymentLog.setPaymentPluginName(paymentPlugin.getName());
				paymentLog.setOrderId(order.getId());
				paymentLog.setMember(null);
				paymentLogService.save(paymentLog);
				parameterMap = paymentPlugin.getParameterMap(paymentLog.getSn(), res.format("shop.payment.paymentDescription", order.getSn()), getRequest());
				break;
			}
		}
		Map<String, String> params = convertAttributes(parameterMap);
		params.put("openid", member.getOpenId());
		params.put("sign",  PaymentKit.createSign(params, pluginConfig.getAttribute("paternerKey"))); // 签名
		
		// 统一下单
		
		String xmlResult = PaymentApi.pushOrder(params);
		
		LogKit.info("xml >>> " + xmlResult);

		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		String result_code = result.get("result_code");
		String err_code_des = result.get("err_code_des");
		if (StrKit.isBlank(result_code) || StringUtils.equals("FAIL", result_code)) {
			renderArgumentError(err_code_des);
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
		// 总金额
		String total_fee = params.get("total_fee"); 
		// 微信支付订单号
		String transaction_id = params.get("transaction_id"); 
		// 商户订单号
		String out_trade_no = params.get("out_trade_no"); 
		// 交易类型
		String trade_type = params.get("trade_type");
		// 支付完成时间，格式为yyyyMMddHHmmss
		String time_end = params.get("time_end");
		// 以下是附加参数
		String openId = params.get("openid");
		
		// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态 避免已经成功、关闭、退款的订单被再次更新
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(WEIXIN_PAYMENT_PLUGIN);
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		if (paymentPlugin != null && PaymentKit.verifyNotify(params, pluginConfig.getAttribute("paternerKey"))) {
			PaymentLog paymentLog = paymentLogService.findBySn(out_trade_no);
			if (paymentLog != null) {
				Member member = memberService.findByOpenId(openId);
				paymentLog.setMemberId(member.getId());
				paymentLog.setMemo(" total_fee = " + total_fee  + ",trade_type = " + trade_type  + ",transaction_id = " + transaction_id + ",time_end=" + time_end);
				paymentLog.put("transaction_id", transaction_id);
				
				// 执行更新订单
				paymentLogService.handle(paymentLog);
				
				// 发送通知等
				Map<String, String> xml = new HashMap<String, String>();
				xml.put("return_code", "SUCCESS");
				xml.put("return_msg", "OK");
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
	public void payResult() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String sn = getPara("sn");
		if (StrKit.isBlank(sn)) {
			renderArgumentError("订单编号不能为空!");
			return;
		}
		
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单编号没有找到!");
			return;
		}
		
		// 返回支付方式
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sn", order.getSn());
		map.put("status", order.getStatus());
		map.put("status_name", order.getStatusName());
		map.put("amountPaid", order.getAmountPaid());
		map.put("paymentMethodName", order.getPaymentMethodName());
		map.put("shippingMethodName", order.getShippingMethodName());
		renderJson(new DatumResponse(map));
	}
	
	 
	/**
	 * 获取微信token
	 * 
	 */
	public void getAccessToken() {
		Long id = getParaToLong("id");
		String urlForward = getPara("url_forward");
		
		String url;
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(WEIXIN_PAYMENT_PLUGIN);
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		Setting setting = SystemUtils.getSetting();
		String redirectUri = setting.getSiteUrl() + "/api/tokenApi/codeNotify.jhtml";
		String state = urlForward + "," + id;
		
		if(isTestURL) {
			url = "http://www.omengo.com/get-weixin-code.html?appid=wx7838f486e14b1a24&scope=snsapi_base&state=" + urlForward + ","+ id + "&redirect_uri=http%3A%2F%2Ftest.omengo.com%2Fapi%2FtokenApi%2FcodeNotify.jhtml";
		} else {
			url =  SnsAccessTokenApi.getAuthorizeURL(pluginConfig.getAttribute("appid"), redirectUri, state, true);
		}
		
		try {
			getResponse().sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderNull();
	}
	
	/**
     * 响应请求参数有误*
     * @param message 错误信息
     */
    public void renderArgumentError(String message) {
        renderJson(new BaseResponse(Code.ARGUMENT_ERROR, message));
    }
		
	/**
	 * Object convert String
	 * 
	 * @param parameterMap
	 *            参数
	 * @return 签名
	 */
	protected Map<String, String> convertAttributes(Map<String, Object> parameterMap) {
		Map<String, String> parameterNewMap = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
			if (entry.getValue() instanceof String) {
				parameterNewMap.put(entry.getKey(), (String) entry.getValue());
			}
		}
		return parameterNewMap;
	}
    
}
