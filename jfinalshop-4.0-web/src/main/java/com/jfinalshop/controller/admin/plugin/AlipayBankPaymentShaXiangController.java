package com.jfinalshop.controller.admin.plugin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.alipayBankPaymentShaXiang.AlipayBankPaymentPluginShaxiang;
import com.jfinalshop.service.PluginConfigService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller - 支付宝(沙箱)
 * 
 * 
 */
@ControllerBind(controllerKey = "/admin/payment_plugin/alipay_shaxiang_payment")
public class AlipayBankPaymentShaXiangController extends BaseController {

	private AlipayBankPaymentPluginShaxiang alipayBankPaymentPluginShaxiang = new AlipayBankPaymentPluginShaxiang();
	private PluginConfigService pluginConfigService = enhance(PluginConfigService.class);

	/**
	 * 安装
	 */
	public void install() {
		if (!alipayBankPaymentPluginShaxiang.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(alipayBankPaymentPluginShaxiang.getId());
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
		if (alipayBankPaymentPluginShaxiang.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(alipayBankPaymentPluginShaxiang.getId());
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = alipayBankPaymentPluginShaxiang.getPluginConfig();
		setAttr("feeTypes", PaymentPlugin.FeeType.values());
		setAttr("pluginConfig", pluginConfig);
		render("/admin/plugin/alipayBankPayment/setting.ftl");
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
		
		PluginConfig pluginConfig = alipayBankPaymentPluginShaxiang.getPluginConfig();
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