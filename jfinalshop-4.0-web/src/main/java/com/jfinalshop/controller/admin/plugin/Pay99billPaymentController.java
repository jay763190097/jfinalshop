package com.jfinalshop.controller.admin.plugin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.pay99billPayment.Pay99billPaymentPlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 快钱支付
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/payment_plugin/pay_99bill_payment")
public class Pay99billPaymentController extends BaseController {

	private Pay99billPaymentPlugin pay99billPaymentPlugin = new Pay99billPaymentPlugin();
	private PluginConfigService pluginConfigService = enhance(PluginConfigService.class);

	/**
	 * 安装
	 */
	public void install() {
		if (!pay99billPaymentPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(pay99billPaymentPlugin.getId());
			pluginConfig.setIsEnabled(false);
			//pluginConfig.setAttributes(null);
			pluginConfigService.save(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 卸载
	 */
	public void uninstall() {
		if (pay99billPaymentPlugin.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(pay99billPaymentPlugin.getId());
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = pay99billPaymentPlugin.getPluginConfig();
		setAttr("feeTypes", PaymentPlugin.FeeType.values());
		setAttr("pluginConfig", pluginConfig);
		render("/admin/plugin/pay99billPayment/setting.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String paymentName = getPara("paymentName");
		String partner = getPara("partner");
		String key = getPara("key");
		String feeTypeName = getPara("feeType", null);
		PaymentPlugin.FeeType feeType = StrKit.notBlank(feeTypeName) ? PaymentPlugin.FeeType.valueOf(feeTypeName) : null;
		BigDecimal fee = new BigDecimal(getPara("fee", "0"));
		String logo = getPara("logo");
		String description = getPara("description");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Integer order = getParaToInt("orders");
		
		PluginConfig pluginConfig = pay99billPaymentPlugin.getPluginConfig();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(PaymentPlugin.PAYMENT_NAME_ATTRIBUTE_NAME, paymentName);
		attributes.put("partner", partner);
		attributes.put("key", key);
		attributes.put(PaymentPlugin.FEE_TYPE_ATTRIBUTE_NAME, feeType.toString());
		attributes.put(PaymentPlugin.FEE_ATTRIBUTE_NAME, fee.toString());
		attributes.put(PaymentPlugin.LOGO_ATTRIBUTE_NAME, logo);
		attributes.put(PaymentPlugin.DESCRIPTION_ATTRIBUTE_NAME, description);
		pluginConfig.setAttributes(attributes);
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/payment_plugin/list.jhtml");
	}

}