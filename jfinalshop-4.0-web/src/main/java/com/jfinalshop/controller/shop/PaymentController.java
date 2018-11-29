package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import net.hasor.core.Inject;
import org.apache.commons.lang3.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.ThemeInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.PaymentLog;
import com.jfinalshop.model.PaymentMethod;
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
@ControllerBind(controllerKey = "/payment")
@Before(ThemeInterceptor.class)
public class PaymentController extends BaseController {

	@Inject
	private OrderService orderService;
	@Inject
	private MemberService memberService;
	@Inject
	private PluginService pluginService;
	@Inject
	private PaymentLogService paymentLogService;

	/**
	 * 插件提交
	 */
	PaymentLog paymentLog=null;
	String message=null;

	public void pluginSubmit() throws Exception {
		String typeName = getPara("type", "");
 		PaymentLog.Type type = StrKit.notBlank(typeName) ? PaymentLog.Type.valueOf(typeName) : null;
		String paymentPluginId = getPara("paymentPluginId");
		String sn = getPara("sn");
		BigDecimal amount = new BigDecimal(getPara("amount", "0"));
		String paymentName = null;
		if (type == null) {
			redirect(ERROR_VIEW);
			return;
		}
		Member member = memberService.getCurrent();
		if (member == null) {
			redirect(ERROR_VIEW);
			return;
		}
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		paymentName = paymentPlugin.getPaymentName();
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			redirect(ERROR_VIEW);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		switch (type) {
			case recharge: {
				if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.precision() > 15 || amount.scale() > setting.getPriceScale()) {
					redirect(ERROR_VIEW);
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
				setAttr("parameterMap", paymentPlugin.getParameterMap(paymentLog.getSn(), message("shop.payment.rechargeDescription"), getRequest()));
				break;
			}
			case payment: {
				Order order = orderService.findBySn(sn);
				if (order == null || !member.equals(order.getMember()) || orderService.isLocked(order, member, true)) {
					redirect(ERROR_VIEW);
					return;
				}
				if (order.getPaymentMethod() == null || !PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
					redirect(ERROR_VIEW);
					return;
				}
				if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
					redirect(ERROR_VIEW);
					return;
				}
				PaymentLog paymentLog = new PaymentLog();
				paymentLog.setSn(null);
				paymentLog.setType(type.ordinal());
				paymentLog.setStatus(PaymentLog.Status.wait.ordinal());
				paymentLog.setFee(paymentPlugin.calculateFee(order.getAmount()));
				paymentLog.setAmount(paymentPlugin.calculateAmount(order.getAmount()));
				paymentLog.setPaymentPluginId(paymentPluginId);
				paymentLog.setPaymentPluginName(paymentPlugin.getName());
				paymentLog.setOrderId(order.getId());
				paymentLog.setMember(null);
				paymentLogService.save(paymentLog);
				if("国际银联".equals(paymentName)){
					JSONObject results = paymentPlugin.getParameterMaps(paymentLog.getSn(), message("shop.payment.paymentDescription"), getRequest());
					if (results==null) {
						redirect(ERROR_VIEW);
						return;
					}
				}
				if("快捷支付".equals(paymentName)){
					String results = paymentPlugin.getResult(paymentLog.getSn(), message("shop.payment.paymentDescription"), getRequest());
					if (results=="") {
						redirect("/member/order/list.jhtml");
						return;
					}else{
						renderHtml(results);
						return;
					}
				}
				if("网关支付".equals(paymentName)){
					String results = paymentPlugin.getResult(paymentLog.getSn(), message("shop.payment.paymentDescription"), getRequest());
					if (results==null) {
						redirect(ERROR_VIEW);
						return;
					}else{
						redirect(results);
						return;
					}
				}
				if("H5支付".equals(paymentName)){
					Map<String ,Object > results = paymentPlugin.getParameterMap(paymentLog.getSn(), message("shop.payment.paymentDescription"), getRequest());
					if (results==null) {
						redirect(ERROR_VIEW);
						return;
					}else if(results.get("message")!="0000"){
						redirect("/member/order/list.jhtml");
						return;
					}else {
						redirect(results.get("qrcode").toString());
					}
				}
				setAttr("parameterMap", paymentPlugin.getParameterMap(paymentLog.getSn(), message("shop.payment.paymentDescription", order.getSn()), getRequest()));
				break;
			}
		}
		setAttr("requestUrl", paymentPlugin.getRequestUrl());
		setAttr("requestMethod", paymentPlugin.getRequestMethod());
		setAttr("requestCharset", paymentPlugin.getRequestCharset());
		if (StringUtils.isNotEmpty(paymentPlugin.getRequestCharset())) {
			getResponse().setContentType("text/html; charset=" + paymentPlugin.getRequestCharset());
		}
		if("国际银联".equals(paymentName)){
			render("/shop/${theme}/payment/plugin_submits.ftl");
		}else {
			render("/shop/${theme}/payment/plugin_submit.ftl");
		}
	}
	/**
	 *
	 */
	//@RequestMapping("/plugin_notify/{pluginId}/{notifyMethod}")

	public void pluginNotify() throws Exception {
		String pluginId = getPara("pluginId");
		String notifyMethodName = getPara("notifyMethod");
		PaymentPlugin.NotifyMethod notifyMethod = notifyMethodName != null ? PaymentPlugin.NotifyMethod.valueOf(notifyMethodName) : null;
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(pluginId);
		if (paymentPlugin != null && paymentPlugin.verifyNotify(notifyMethod, getRequest())) {
			String sn = paymentPlugin.getSn(getRequest());
			paymentLog = paymentLogService.findBySn(sn);
			message = paymentPlugin.getNotifyMessage(notifyMethod, getRequest());
			if (paymentLog != null) {
				paymentLogService.handle(paymentLog);
				setAttr("paymentLog", paymentLog);
				setAttr("notifyMessage",message);
			}
		}
		render("/shop/${theme}/payment/plugin_notify.ftl");
	}
}